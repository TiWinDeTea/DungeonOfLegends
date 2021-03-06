//////////////////////////////////////////////////////////////////////////////////
//                                                                              //
//     This Source Code Form is subject to the terms of the Mozilla Public      //
//     License, v. 2.0. If a copy of the MPL was not distributed with this      //
//     file, You can obtain one at http://mozilla.org/MPL/2.0/.                 //
//                                                                              //
//////////////////////////////////////////////////////////////////////////////////

package com.github.tiwindetea.raoulthegame.model.livings;

import com.github.tiwindetea.raoulthegame.events.game.living_entities.LivingEntityHealthUpdateEvent;
import com.github.tiwindetea.raoulthegame.events.game.living_entities.LivingEntityManaUpdateEvent;
import com.github.tiwindetea.raoulthegame.events.game.living_entities.LivingEntityMoveEvent;
import com.github.tiwindetea.raoulthegame.events.game.living_entities.LivingEntityXpUpdateEvent;
import com.github.tiwindetea.raoulthegame.listeners.game.GameListener;
import com.github.tiwindetea.raoulthegame.model.Descriptable;
import com.github.tiwindetea.raoulthegame.model.Pair;
import com.github.tiwindetea.raoulthegame.model.space.Tile;
import com.github.tiwindetea.raoulthegame.model.space.Vector2i;
import com.github.tiwindetea.raoulthegame.model.spells.Spell;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * LivingThing.
 *
 * @author Lucas LAZARE
 */
public abstract class LivingThing implements Descriptable {

    protected Tile[][] sight;
    protected int level;
    protected double maxHitPoints;
    protected double hitPoints;
    protected double attackPower;
    protected double defensePower;
    protected Vector2i position;
    protected LivingThing requestedAttack;
    protected String name;
    protected long id;
    protected static final ArrayList<GameListener> listeners = new ArrayList<>();
    protected final Spell[] spells;


    /**
     * Adds a LivingEntity listener
     *
     * @param listener Listener to add
     */
    public static void addGameListener(GameListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    protected static GameListener[] getPlayersListeners() {
        return LivingThing.listeners.toArray(new GameListener[LivingThing.listeners.size()]);
    }

    protected void fireHealthUpdate(LivingEntityHealthUpdateEvent event) {

        for (GameListener listener : getPlayersListeners()) {
            listener.handle(event);
        }
    }

    protected void fireMoveEvent(LivingEntityMoveEvent event) {
        for (GameListener listener : getPlayersListeners()) {
            listener.handle(event);
        }
    }

    protected void fireXpUpdateEvent(LivingEntityXpUpdateEvent event) {
        for (GameListener listener : getPlayersListeners()) {
            listener.handle(event);
        }
    }

    protected void fireManaUpdateEvent(LivingEntityManaUpdateEvent event) {
        for (GameListener listener : getPlayersListeners()) {
            listener.handle(event);
        }
    }

    protected double getPowerGrade() {
        double ap;
        if (this.getType() == LivingThingType.PLAYER) {
            ap = ((Player) this).getAttackPowerNoManaUse();
        } else {
            ap = this.getAttackPower();
        }
        return 5 * (ap / 11 + this.getHitPoints() / 10 + this.getDefensePower());
    }

    /**
     * Instantiates a new Living thing.
     */
    public LivingThing() {
        this.id = Pair.getUniqueId();
        if (this.getType() == LivingThingType.PLAYER) {
            this.spells = new Spell[4];
        } else {
            this.spells = new Spell[1];
        }
    }

    /**
     * Updates the sight.
     *
     * @param sight the new sight
     */
    public void updateSight(Tile[][] sight) {
        this.sight = sight;
    }

    /**
     * Get spells
     *
     * @return the spells
     */
    public Spell[] getSpells() {
        return this.spells;
    }

    /**
     * Gets level.
     *
     * @return the level
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * Gets max hit points.
     *
     * @return the max hit points
     */
    public double getMaxHitPoints() {
        return this.maxHitPoints;
    }

    /**
     * Gets hit points.
     *
     * @return the hit points
     */
    public double getHitPoints() {
        return this.hitPoints;
    }

    /**
     * Gets attack power.
     *
     * @return the attack power
     */
    public double getAttackPower() {
        return this.attackPower;
    }

    /**
     * Gets defense power.
     *
     * @return the defense power
     */
    public double getDefensePower() {
        return this.defensePower;
    }

    /**
     * Gets position.
     *
     * @return the position
     */
    public Vector2i getPosition() {
        return this.position.copy();
    }

    /**
     * Sets position.
     *
     * @param position the new position
     */
    public void setPosition(Vector2i position) {
        fireMoveEvent(new LivingEntityMoveEvent(this.id, position));
        this.position = position;
    }

    /**
     * Getter for the LivingEntity id
     *
     * @return the id
     */
    public long getId() {
        return this.id;
    }

    /**
     * Damages this.
     *
     * @param damages damages taken by this
     * @param source  LivingThing that is the source of the damages
     */
    public void damage(double damages, @Nullable LivingThing source) {
        double diff;
        if (damages > 0) {
            double tmp = 0;
            for (Spell spell : this.spells) {
                if (spell != null) {
                    tmp += spell.ownerDamaged(source, damages);
                }
            }
            damages += tmp;
            diff = this.getDefensePower() - damages;
            if (diff >= -1) {
                diff = -1;
            }
        } else {
            diff = Math.min(this.maxHitPoints - this.hitPoints, -damages);
        }
        this.hitPoints += diff;
        fireHealthUpdate(new LivingEntityHealthUpdateEvent(this.id, this.hitPoints / this.maxHitPoints, (int) Math.round(diff)));
    }

    /**
     * @return true if the LivingThing is alive, false otherwise
     */
    public boolean isAlive() {
        return (((int) this.hitPoints) > 0);
    }

    /**
     * Make this to live.
     *
     * @param mobs    Mobs around this
     * @param players Players around this
     * @param others  Other livings around this
     * @param los     LOS of this
     */
    public abstract void live(List<Mob> mobs, Collection<Player> players, Collection<LivingThing> others, boolean[][] los);

    /**
     * Gets the requested move.
     *
     * @return the requested move
     */
    public abstract Vector2i getRequestedMove();

    /**
     * Gets the requested attack.
     *
     * @return the requested attack
     */
    public LivingThing getRequestedAttack() {
        return this.requestedAttack;
    }

    /**
     * Gets type.
     *
     * @return the type of this
     *
     * @see LivingThingType
     */
    public abstract LivingThingType getType();

    /**
     * Make the players to attack the target
     *
     * @param target target of the attack
     */
    public void attack(@Nonnull LivingThing target) {
        double damages = this.getAttackPower();
        for (Spell spell : this.spells) {
            if (spell != null) {
                damages += spell.ownerAttacking(target);
            }
        }
        target.damage(damages, this);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        /* Asserting o to be a LivingThing */
        return this.equals((LivingThing) o);
    }

    /**
     * Equals boolean.
     *
     * @param vect the vect
     * @return the boolean
     */
    public boolean equals(Vector2i vect) {
        return this.position.equals(vect);
    }

    /**
     * Equals boolean.
     *
     * @param livingThing the living thing
     * @return the boolean
     */
    public boolean equals(LivingThing livingThing) {
        return this.position.equals(livingThing.position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return this.name;
    }

    public String getName() {
        return this.getType().toString();
    }

    public double getAggro() {
        return 0;
    }
}
