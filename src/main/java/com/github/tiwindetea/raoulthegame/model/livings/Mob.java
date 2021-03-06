//////////////////////////////////////////////////////////////////////////////////
//                                                                              //
//     This Source Code Form is subject to the terms of the Mozilla Public      //
//     License, v. 2.0. If a copy of the MPL was not distributed with this      //
//     file, You can obtain one at http://mozilla.org/MPL/2.0/.                 //
//                                                                              //
//////////////////////////////////////////////////////////////////////////////////

package com.github.tiwindetea.raoulthegame.model.livings;

import com.github.tiwindetea.raoulthegame.model.Pair;
import com.github.tiwindetea.raoulthegame.model.space.Direction;
import com.github.tiwindetea.raoulthegame.model.space.Map;
import com.github.tiwindetea.raoulthegame.model.space.Tile;
import com.github.tiwindetea.raoulthegame.model.space.Vector2i;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * Mob.
 *
 * @author Lucas LAZARE
 */
public class Mob extends LivingThing {

    private final int ON_DAMAGE_CHASE_RANGE_BONUS = 100;

    // Mobs know the map perfectly.
    private static Map map;
    private State state = State.SLEEPING;
    private Vector2i requestedPath;
    private Stack<Vector2i> requestedPathStack = new Stack<>();
    private Direction direction = Direction.DOWN;
    private int chaseRange;
    private boolean nameAsked = false;
    private int xpGain;
    private boolean wasHit;
    private final HashMap<Long, Double> aggro = new HashMap<>();

    /**
     * Sets the map.
     *
     * @param map the map
     */
    public static void setMap(Map map) {
        Mob.map = map;
    }

    /**
     * Instantiates a new Mob. (for comparison purposes)
     *
     * @param pos the pos
     */
    public Mob(Vector2i pos) {
        this.position = pos;
    }

    /**
     * Instantiates a new Mob.
     *
     * @param level        level
     * @param maxHitPoints max hit points
     * @param attackPower  attack power
     * @param defensePower defense power
     * @param position     position
     */
    public Mob(String name, int level, int xpGain, double maxHitPoints, double attackPower, double defensePower, int chaseRange, Vector2i position) {
        super();
        this.name = name;
        this.level = level;
        this.xpGain = xpGain;
        this.maxHitPoints = maxHitPoints;
        this.attackPower = attackPower;
        this.defensePower = defensePower;
        this.chaseRange = chaseRange;
        this.position = position;
        this.hitPoints = maxHitPoints;
    }

    private Tile map(Vector2i pos) {
        return Mob.map.getTile(pos);
    }

    private void keepPatroling(Collection<Mob> mobs) {
        if (!Tile.isObstructed(map(this.position.copy().add(this.direction))) && !mobs.contains(new Mob(this.position.copy().add(this.direction)))) {
            this.requestedPath = this.position.copy().add(this.direction);
            return;
        }
        ArrayList<Direction> directions = new ArrayList<>(4);
        directions.addAll(Arrays.asList(Direction.DOWN, Direction.LEFT, Direction.UP, Direction.RIGHT));
        directions.remove(this.direction);
        int index;
        /* Looking for a wall to follow */
        for (index = 0; index < directions.size() &&
                !Tile.isRoomBorder(map(this.position.copy().add(directions.get(index)))); ++index)
            ;

		/* If you are in a corner (twice for corridors)*/
        int count = 0;
        while (Tile.isRoomBorder(map(this.position.copy().add(directions.get((index + 1) % directions.size())))) && count < 2) {
            ++count;
            index = (index + 1) % directions.size();
        }

		/* Follow the wall, or try to find one */
        this.direction = directions.get((index + 1) % directions.size());
        Vector2i next = this.position.copy().add(this.direction);
        if (!Tile.isObstructed(map(next))) {
            this.requestedPath = next;
        } else {
            /* Look for the a non-obstructed tile, ignoring living entities */
            for (index = 0; index < directions.size() && Tile.isObstructed(map(this.position.copy().add(directions.get(index)))); ++index)
                ;
            if (index < directions.size()) {
                this.direction = directions.get(index);
                this.requestedPath = this.position.copy().add(this.direction);
            } else {
                this.requestedPath = this.position.copy();
            }
        }
    }

    private void chase(Collection<LivingThing> collisionsEntities, LivingThing target) {
        this.requestedPathStack = map.getPath(this.position, target.getPosition(), false, collisionsEntities);
    }

    private void wander() {
        Direction[] directions = {Direction.DOWN, Direction.LEFT, Direction.UP, Direction.DOWN};
        ArrayList<Direction> possibleDirs = new ArrayList<>(4);
        for (Direction direction : directions) {
            if (!Tile.isObstructed(map(this.position.copy().add(direction)))) {
                possibleDirs.add(direction);
            }
        }

        if (possibleDirs.size() > 0)
            this.requestedPath = this.position.copy().add(possibleDirs.get(new Random().nextInt(possibleDirs.size())));
        else
            this.requestedPath = this.position.copy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof LivingThing) {
            return ((LivingThing) o).getPosition().equals(this.position);
        } else {
            /* Asserting o to be a pair of LivingThing */
            return ((Pair<LivingThing>) o).object.getPosition().equals(this.position);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void live(List<Mob> mobs, Collection<Player> players, Collection<LivingThing> others, boolean[][] los) {

        if (this.wasHit) {
            this.chaseRange += this.ON_DAMAGE_CHASE_RANGE_BONUS;
        }
        int distance = Integer.MAX_VALUE;
        Collection<LivingThing> shadow = new ArrayList<>(mobs.size() + players.size() + others.size());
        Collection<LivingThing> targets = new ArrayList<>(players.size() + others.size());
        LivingThing chasedTarget = null;
        shadow.addAll(mobs);
        shadow.addAll(others);
        shadow.addAll(players);

        targets.addAll(others);
        targets.addAll(players);

        for (LivingThing target : targets) {
            Vector2i pos = target.getPosition();
            // if the target is in our LOS
            int distanceToTarget = Math.max(Math.abs(pos.x - this.position.x), Math.abs(pos.y - this.position.y));
            if (distanceToTarget < los.length / 2) {
                if (los[los.length / 2 - this.position.x + pos.x][los[0].length / 2 - this.position.y + pos.y]) {
                    Double aggro, paggro;
                    if (chasedTarget == null
                            || (aggro = this.aggro.get(target.getId())) != null
                      && (paggro = this.aggro.get(chasedTarget.getId())) != null && aggro.doubleValue() > paggro.doubleValue()) {
                        chasedTarget = target;
                        distance = this.distanceTo(pos, shadow);
                    }
                }
            }
        }
        if (distance <= 1) {
            this.requestedAttack = chasedTarget;
            this.requestedPath = null;
            this.requestedPathStack.clear();
        } else if (distance <= this.chaseRange) {
            this.chase(shadow, chasedTarget);
            this.state = State.CHASING;
        } else if (this.requestedPathStack.isEmpty()) {

            int value;
			/* Let's do something at random, if I can't chase anyone */
            switch (this.state) {
                case PATROLING:
                    this.keepPatroling(mobs);
                    value = new Random().nextInt(20);
                    if (value == 0) {
                        this.state = State.STANDING;

                    } else if (value == 1) {
                        this.state = State.WANDERING;

                    }
                    break;
                case STANDING:
                    value = new Random().nextInt(4);
                    if (value == 0) {
                        this.state = State.PATROLING;

                    } else if (value == 1) {
                        this.state = State.WANDERING;

                    }
                    break;
                case SLEEPING:
                    if (new Random().nextInt(3) == 0) {
                        this.state = State.STANDING;

                    }
                    break;
                case WANDERING:
                    wander();
                    value = new Random().nextInt(20);
                    if (value == 0) {
                        this.state = State.PATROLING;

                    } else if (value == 1) {
                        this.state = State.STANDING;
                    }
                    this.wander();
                    break;
                default:
                    keepPatroling(mobs);
                    this.state = State.PATROLING;
                    break;
            }
        }
        if (this.wasHit) {
            this.chaseRange -= this.ON_DAMAGE_CHASE_RANGE_BONUS;
            this.wasHit = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector2i getRequestedMove() {
        if (this.requestedPathStack != null && this.requestedPathStack.size() > 0) {
            return this.requestedPathStack.pop();
        }
        return this.requestedPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingThingType getType() {
        return LivingThingType.MOB;
    }

    /**
     * Gets the chase range
     *
     * @return the chase range
     */
    public int getChaseRange() {
        if (this.wasHit) {
            return this.chaseRange + this.ON_DAMAGE_CHASE_RANGE_BONUS;
        }
        return this.chaseRange;
    }

    private int distanceTo(Vector2i pos, Collection<LivingThing> entities) {
        Stack<Vector2i> path = map.getPath(this.position, pos, false, entities);
        return (path == null) ? Integer.MAX_VALUE : path.size();
    }

    /**
     * Gets the xp gain for players on death
     *
     * @return the xp gain
     */
    public int getXpGain() {
        return this.xpGain;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void damage(double damages, @Nullable LivingThing source) {
        super.damage(damages, source);
        this.state = State.CHASING;
        this.wasHit = true;
        if (source != null) {
            Double currentAggro = this.aggro.get(source.getId());
            if (currentAggro != null) {
                this.aggro.remove(source.getId(), currentAggro);
                this.aggro.put(source.getId(), source.getAggro() + currentAggro.doubleValue());
            } else {
                this.aggro.put(source.getId(), source.getAggro());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        if (!this.nameAsked) {
            if (this.attackPower > 0) {
                this.name += " (Lv" + this.level + ".)\n"
                        + "Power grade: " + (int) this.getPowerGrade();
            } else {
                this.name += "\nPeace and harmony on your people";
            }
        }
        this.nameAsked = true;
        return this.name;
    }
}
