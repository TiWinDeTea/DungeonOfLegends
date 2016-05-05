package com.github.tiwindetea.dungeonoflegend.model;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.Stack;

import static java.lang.Thread.sleep;

/**
 * Created by organic-code on 5/1/16.
 */
public class SampleTester extends Application {

    final int xsize = Integer.parseInt(MainPackage.spriteSheetBundle.getString("xresolution")),
            ysize = Integer.parseInt(MainPackage.spriteSheetBundle.getString("yresolution"));
    boolean allVisible = false;
    int player = 0;
    int los = 5;
    int level = 0;
    Vector2i pos = new Vector2i(1, 1),
            click;
    Vector2i wallDown = Tile.WALL_DOWN.getSpritePosition().multiply(this.xsize),
            wallUp = Tile.WALL_TOP.getSpritePosition().multiply(this.xsize),
            wallRight = Tile.WALL_RIGHT.getSpritePosition().multiply(this.xsize),
            wallLeft = Tile.WALL_LEFT.getSpritePosition().multiply(this.xsize),
            wallTopLeft = Tile.WALL_TOPLEFT.getSpritePosition().multiply(this.xsize),
            wallTopRight = Tile.WALL_TOPRIGHT.getSpritePosition().multiply(this.xsize),
            wallDownLeft = Tile.WALL_DOWNLEFT.getSpritePosition().multiply(this.xsize),
            wallDownRight = Tile.WALL_DOWNRIGHT.getSpritePosition().multiply(this.xsize),
            ground = Tile.GROUND.getSpritePosition().multiply(this.xsize),
            unknown = Tile.UNKNOWN.getSpritePosition().multiply(this.xsize),
            closedDoor = Tile.CLOSED_DOOR.getSpritePosition().multiply(this.xsize),
            openedDoor = Tile.OPENED_DOOR.getSpritePosition().multiply(this.xsize),
            stairUp = Tile.STAIR_UP.getSpritePosition().multiply(this.xsize),
            stairDown = Tile.STAIR_DOWN.getSpritePosition().multiply(this.xsize),
            pillar = Tile.PILLAR.getSpritePosition().multiply(this.xsize);
    Stack<Vector2i> path = new Stack<>();
    double zoom = 1;
    Image objectTextures = new Image(MainPackage.path + "/" + MainPackage.spriteSheetBundle.getString("objects.file"));
    Map world = new Map(new Seed(10, 10));
    Group root = new Group();
    ImageView heroe = new ImageView();

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        this.world.generateLevel(this.level);
        while (this.world.getTile(this.pos) != Tile.GROUND) {
            ++this.pos.x;
            if (this.pos.x >= this.world.getSize().x) {
                this.pos.x = 1;
                ++this.pos.y;
            }
        }
        primaryStage.initStyle(StageStyle.UNDECORATED);
        this.heroe.setSmooth(false);
        final Scene scene = new Scene(this.root, this.xsize * (2 * this.los + 1), this.ysize * (2 * this.los + 1), Color.GRAY);
        Image playerTextures = new Image(MainPackage.path + "/" + MainPackage.spriteSheetBundle.getString("players.file"));
        this.heroe.setImage(playerTextures);
        this.heroe.setViewport(new Rectangle2D(0, this.ysize * this.player, this.xsize, this.ysize));

        if (this.xsize != this.ysize)
            throw new UnsupportedOperationException();

        final ArrayList<String> input = new ArrayList();
        scene.setOnKeyPressed(
                new EventHandler<KeyEvent>() {
                    public void handle(KeyEvent e) {
                        String code = e.getCode().toString();

                        // only add once... prevent duplicates
                        if (!input.contains(code))
                            input.add(code);
                        System.out.println("code = " + code);
                    }
                });

        scene.setOnKeyReleased(
                new EventHandler<KeyEvent>() {
                    public void handle(KeyEvent e) {
                        String code = e.getCode().toString();
                        input.remove(code);
                    }
                });
        scene.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (SampleTester.this.allVisible && !input.contains("CLICK"))
                            input.add("CLICK");
                        SampleTester.this.click = new Vector2i((int) (event.getX() / (32 * SampleTester.this.zoom)), (int) (event.getY() / (32 * SampleTester.this.zoom)));
                    }
                }
        );
        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                boolean in = false;
                if (SampleTester.this.path == null) {
                    SampleTester.this.path = new Stack<>();
                }
                if (SampleTester.this.path.size() > 0) {
                    in = true;
                    Vector2i ppos = SampleTester.this.pos, tmp;
                    SampleTester.this.pos = SampleTester.this.path.pop();
                    Direction[] directions = {Direction.DOWN, Direction.RIGHT, Direction.UP, Direction.LEFT};
                    int[] lol = {0, 64, 32, 96};
                    for (int i = 0; i < directions.length; ++i) {
                        tmp = ppos.copy().add(directions[i]);
                        if (tmp.equals(SampleTester.this.pos)) {
                            SampleTester.this.heroe.setViewport(new Rectangle2D(lol[i], SampleTester.this.ysize * SampleTester.this.player, SampleTester.this.xsize, SampleTester.this.ysize));
                        }
                    }
                }
                if (input.contains("LEFT")) {
                    Vector2i previousPos = new Vector2i(SampleTester.this.pos);
                    SampleTester.this.heroe.setViewport(new Rectangle2D(96, SampleTester.this.ysize * SampleTester.this.player, SampleTester.this.xsize, SampleTester.this.ysize));
                    if (SampleTester.this.pos.x > 0)
                        SampleTester.this.pos.x--;
                    if (!Tile.isObstructed(SampleTester.this.world.getTile(SampleTester.this.pos)))
                        in = true;
                    else
                        SampleTester.this.pos = previousPos;

                }

                if (input.contains("RIGHT")) {
                    Vector2i previousPos = new Vector2i(SampleTester.this.pos);
                    SampleTester.this.heroe.setViewport(new Rectangle2D(64, SampleTester.this.ysize * SampleTester.this.player, SampleTester.this.xsize, SampleTester.this.ysize));
                    if (SampleTester.this.pos.x < SampleTester.this.world.getSize().x - 1) {
                        SampleTester.this.pos.x++;
                    }
                    if (!Tile.isObstructed(SampleTester.this.world.getTile(SampleTester.this.pos))) {
                        in = true;
                    } else {
                        SampleTester.this.pos = previousPos;
                }
                }

                if (input.contains("UP")) {
                    Vector2i previousPos = new Vector2i(SampleTester.this.pos);
                    SampleTester.this.heroe.setViewport(new Rectangle2D(32, SampleTester.this.ysize * SampleTester.this.player, SampleTester.this.xsize, SampleTester.this.ysize));
                    if (SampleTester.this.pos.y > 0) {
                        SampleTester.this.pos.y--;
                    }
                    if (!Tile.isObstructed(SampleTester.this.world.getTile(SampleTester.this.pos))) {
                        in = true;
                    } else {
                        SampleTester.this.pos = previousPos;
                    }
                }

                if (input.contains("DOWN"))

                {
                    Vector2i previousPos = new Vector2i(SampleTester.this.pos);
                    SampleTester.this.heroe.setViewport(new Rectangle2D(0, SampleTester.this.ysize * SampleTester.this.player, SampleTester.this.xsize, SampleTester.this.ysize));
                    if (SampleTester.this.pos.y < SampleTester.this.world.getSize().y - 1)
                        SampleTester.this.pos.y++;
                    if (!Tile.isObstructed(SampleTester.this.world.getTile(SampleTester.this.pos)))
                        in = true;
                    else
                        SampleTester.this.pos = previousPos;
                }

                if (input.contains("P")) {
                    SampleTester.this.zoom += 0.1;
                    if (SampleTester.this.allVisible) {
                        primaryStage.setWidth(SampleTester.this.zoom * SampleTester.this.xsize * SampleTester.this.world.getSize().x);
                        primaryStage.setHeight(SampleTester.this.zoom * SampleTester.this.ysize * SampleTester.this.world.getSize().y);
                    } else {
                        primaryStage.setWidth(SampleTester.this.zoom * SampleTester.this.xsize * (2 * SampleTester.this.los + 1));
                        primaryStage.setHeight(SampleTester.this.zoom * SampleTester.this.ysize * (2 * SampleTester.this.los + 1));
                    }
                    in = true;
                }

                if (input.contains("M")) {
                    SampleTester.this.zoom -= 0.1;
                    if (SampleTester.this.allVisible) {
                        primaryStage.setWidth(SampleTester.this.zoom * SampleTester.this.xsize * SampleTester.this.world.getSize().x);
                        primaryStage.setHeight(SampleTester.this.zoom * SampleTester.this.ysize * SampleTester.this.world.getSize().y);
                    } else {
                        primaryStage.setWidth(SampleTester.this.zoom * SampleTester.this.xsize * (2 * SampleTester.this.los + 1));
                        primaryStage.setHeight(SampleTester.this.zoom * SampleTester.this.ysize * (2 * SampleTester.this.los + 1));
                    }
                    in = true;
                }

                if ((input.contains("PLUS") || input.contains("ADD")) && !SampleTester.this.allVisible)

                {
                    ++SampleTester.this.los;
                    primaryStage.setWidth(SampleTester.this.zoom * SampleTester.this.xsize * (2 * SampleTester.this.los + 1));
                    primaryStage.setHeight(SampleTester.this.zoom * SampleTester.this.ysize * (2 * SampleTester.this.los + 1));
                    in = true;
                }

                if ((input.contains("MINUS") || input.contains("SUBTRACT")) && !SampleTester.this.allVisible)

                {
                    if (SampleTester.this.los > 0) {
                        --SampleTester.this.los;
                        primaryStage.setWidth(SampleTester.this.zoom * SampleTester.this.xsize * (2 * SampleTester.this.los + 1));
                        primaryStage.setHeight(SampleTester.this.zoom * SampleTester.this.ysize * (2 * SampleTester.this.los + 1));
                        in = true;
                    }
                }
                if (input.contains("A")) {
                    in = true;
                    SampleTester.this.player = 1 - SampleTester.this.player;
                    SampleTester.this.heroe.setViewport(new Rectangle2D(SampleTester.this.heroe.getViewport().getMinX(), SampleTester.this.player * SampleTester.this.ysize, SampleTester.this.xsize, SampleTester.this.ysize));
                }

                if (input.contains("SPACE")) {
                    for (int i = SampleTester.this.pos.x - 1; i <= SampleTester.this.pos.x + 1; i++) {
                        for (int j = SampleTester.this.pos.y - 1; j <= SampleTester.this.pos.y + 1; j++) {
                            SampleTester.this.world.triggerTile(new Vector2i(i, j), new Mob());
                        }
                    }
                    in = true;
                    try {
                        sleep(40);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (input.contains("ESCAPE")) {
                    SampleTester.this.allVisible = !SampleTester.this.allVisible;
                    in = true;
                    if (SampleTester.this.allVisible) {
                        primaryStage.setWidth(SampleTester.this.zoom * SampleTester.this.xsize * SampleTester.this.world.getSize().x);
                        primaryStage.setHeight(SampleTester.this.zoom * SampleTester.this.ysize * SampleTester.this.world.getSize().y);
                    } else {
                        primaryStage.setWidth(SampleTester.this.zoom * SampleTester.this.xsize * (2 * SampleTester.this.los + 1));
                        primaryStage.setHeight(SampleTester.this.zoom * SampleTester.this.ysize * (2 * SampleTester.this.los + 1));
                    }
                    try {
                        sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (input.contains("CLICK"))

                {
                    SampleTester.this.path = SampleTester.this.world.getPath(SampleTester.this.pos, SampleTester.this.click, false);
                    in = true;
                }

                if (input.contains("N")) {
                    ++SampleTester.this.level;
                    SampleTester.this.world.generateLevel(SampleTester.this.level);
                    SampleTester.this.world.generateLevel(SampleTester.this.level);
                    SampleTester.this.pos = new Vector2i(0, 0);
                    while (SampleTester.this.world.getTile(SampleTester.this.pos) != Tile.GROUND) {
                        ++SampleTester.this.pos.x;
                        if (SampleTester.this.pos.x >= SampleTester.this.world.getSize().x) {
                            SampleTester.this.pos.x = 1;
                            ++SampleTester.this.pos.y;
                        }
                    }
                    if (SampleTester.this.allVisible) {
                        primaryStage.setWidth(SampleTester.this.zoom * SampleTester.this.xsize * SampleTester.this.world.getSize().x);
                        primaryStage.setHeight(SampleTester.this.zoom * SampleTester.this.ysize * SampleTester.this.world.getSize().y);
                    } else {
                        primaryStage.setWidth(SampleTester.this.zoom * SampleTester.this.xsize * (2 * SampleTester.this.los + 1));
                        primaryStage.setHeight(SampleTester.this.zoom * SampleTester.this.ysize * (2 * SampleTester.this.los + 1));
                    }
                    in = true;
                }

                if (in) {
                    SampleTester.this.root.getChildren().clear();
                    SampleTester.this.drawEnvironement();
                    SampleTester.this.heroe.setFitHeight(SampleTester.this.ysize * SampleTester.this.zoom);
                    SampleTester.this.heroe.setFitWidth(SampleTester.this.xsize * SampleTester.this.zoom);
                    if (SampleTester.this.allVisible) {
                        SampleTester.this.heroe.setTranslateX(SampleTester.this.pos.x * SampleTester.this.xsize * SampleTester.this.zoom);
                        SampleTester.this.heroe.setTranslateY(SampleTester.this.pos.y * SampleTester.this.ysize * SampleTester.this.zoom);
                        Label lbl = new Label("Level: " + SampleTester.this.level);
                        Label seed = new Label("Seed: " + SampleTester.this.world.getAlphaSeed() + " ; " + SampleTester.this.world.getBetaSeed());
                        lbl.setFont(Font.font("Liberation", FontWeight.BOLD, 15));
                        seed.setFont(Font.font("Liberation", FontWeight.BOLD, 15));
                        lbl.setTextFill(Color.WHITE);
                        seed.setTextFill(Color.WHITE);
                        SampleTester.this.root.getChildren().addAll(lbl, seed);
                        FontLoader font = Toolkit.getToolkit().getFontLoader();
                        lbl.setTranslateX(primaryStage.getWidth() - font.computeStringWidth(seed.getText(), seed.getFont()));
                        lbl.setTranslateY(primaryStage.getHeight() - 50);
                        seed.setTranslateX(primaryStage.getWidth() - font.computeStringWidth(seed.getText(), seed.getFont()));
                        seed.setTranslateY(primaryStage.getHeight() - 25);
                    } else {
                        SampleTester.this.heroe.setTranslateY((primaryStage.getHeight() - SampleTester.this.ysize * SampleTester.this.zoom) / 2);
                        SampleTester.this.heroe.setTranslateX((primaryStage.getWidth() - SampleTester.this.xsize * SampleTester.this.zoom) / 2);
                    }
                    SampleTester.this.root.getChildren().add(SampleTester.this.heroe);
                }
                try {
                    sleep(60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        this.drawEnvironement();
        if (this.allVisible) {
            this.heroe.setTranslateX(this.pos.x * this.xsize);
            this.heroe.setTranslateY(this.pos.y * this.ysize);
        } else {
            this.heroe.setTranslateY((scene.getHeight() - this.ysize) / 2);
            this.heroe.setTranslateX((scene.getWidth() - this.xsize) / 2);
        }
        this.root.getChildren().add(this.heroe);

        primaryStage.setWidth(SampleTester.this.xsize * (2 * SampleTester.this.los + 1));
        primaryStage.setHeight(SampleTester.this.ysize * (2 * SampleTester.this.los + 1));
        primaryStage.setTitle("~ Dungeon Of Legends test ~");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void drawEnvironement() {
        Tile[][] sight;
        if (this.allVisible) {
            sight = this.world.getMapCopy();
        } else {
            sight = this.world.getLOS(this.pos, this.los);
        }
        for (int i = 0; i < sight.length; i++) {
            for (int j = 0; j < sight[i].length; j++) {
                ImageView sprite = new ImageView();
                sprite.setImage(this.objectTextures);
                Vector2i place;
                switch (sight[i][j]) {
                    case WALL_LEFT:
                        place = this.wallLeft;
                        break;
                    case WALL_RIGHT:
                        place = this.wallRight;
                        break;
                    case WALL_DOWN:
                        place = this.wallDown;
                        break;
                    case WALL_TOP:
                        place = this.wallUp;
                        break;
                    case WALL_TOPLEFT:
                        place = this.wallTopLeft;
                        break;
                    case WALL_TOPRIGHT:
                        place = this.wallTopRight;
                        break;
                    case WALL_DOWNLEFT:
                        place = this.wallDownLeft;
                        break;
                    case WALL_DOWNRIGHT:
                        place = this.wallDownRight;
                        break;
                    case GROUND:
                        place = new Vector2i(this.ground);
                        if (this.allVisible) {
                            place.x += this.xsize * ((i + j) % 5);
                        } else {
                            place.x += this.xsize * ((i + j + this.pos.x + this.pos.y - 2 * this.los) % 5);
                        }
                        break;
                    case OPENED_DOOR:
                        place = this.openedDoor;
                        break;
                    case CLOSED_DOOR:
                        place = this.closedDoor;
                        break;
                    case STAIR_UP:
                        place = this.stairUp;
                        break;
                    case STAIR_DOWN:
                        place = this.stairDown;
                        break;
                    case UNKNOWN:
                        place = this.unknown;
                        break;
                    case PILLAR:
                        place = this.pillar;
                        break;
                    default:
                        place = this.unknown;
                }
                sprite.setViewport(new Rectangle2D(place.x, place.y, this.xsize, this.ysize));
                sprite.setFitWidth(SampleTester.this.xsize * SampleTester.this.zoom);
                sprite.setFitHeight(SampleTester.this.ysize * SampleTester.this.zoom);
                sprite.setTranslateY(j * this.xsize * this.zoom);
                sprite.setTranslateX(i * this.ysize * this.zoom);
                this.root.getChildren().add(sprite);
            }
        }
    }
}
