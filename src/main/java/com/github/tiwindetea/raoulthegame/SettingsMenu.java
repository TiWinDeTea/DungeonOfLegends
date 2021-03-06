//////////////////////////////////////////////////////////////////////////////////
//                                                                              //
//     This Source Code Form is subject to the terms of the Mozilla Public      //
//     License, v. 2.0. If a copy of the MPL was not distributed with this      //
//     file, You can obtain one at http://mozilla.org/MPL/2.0/.                 //
//                                                                              //
//////////////////////////////////////////////////////////////////////////////////

package com.github.tiwindetea.raoulthegame;

import com.github.tiwindetea.raoulthegame.view.ViewPackage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.text.DecimalFormat;

/**
 * The type Settings menu.
 *
 * @author Maxime PINARD
 */
public class SettingsMenu {

	private static final int MENU_MIN_WIDTH = 800;
	private static final int MENU_MIN_HEIGHT = 600;
	private static final int BUTTONS_SPACING = 30;

	private static final Color BACKGROUND_COLOR = Color.rgb(23, 110, 67);
	private final StackPane mainStackPane = new StackPane();
	private final Scene scene = new Scene(this.mainStackPane);

	private final CheckBox simpleAutoEquip = new CheckBox();
	private final CheckBox autoEquip = new CheckBox();
	private final CheckBox autoEquipCanDrop = new CheckBox();
	private final CheckBox permaDeath = new CheckBox();
	private final CheckBox mouseInverted = new CheckBox();
	private final CheckBox skipIntro = new CheckBox();

	private final HBox buttonsHBox = new HBox();
	private final Button save = new Button();
	private final Button cancel = new Button();
	private final Button exit = new Button();

	private final ScrollBar difficultyScroller = new ScrollBar();
	private final Label difficultyLabel = new Label();
	private final HBox difficultyHBox = new HBox();

	private final VBox vBox = new VBox();

	private final SubMenuManager manager;

	public SettingsMenu(SubMenuManager manager) {
		this.manager = manager;

		this.mainStackPane.setBackground(new Background(new BackgroundFill(
			BACKGROUND_COLOR,
			CornerRadii.EMPTY,
			Insets.EMPTY
		  ))
		);

		this.simpleAutoEquip.setText("Simple auto equip");
		this.simpleAutoEquip.setTooltip(new Tooltip("Basic auto equip feature"));
		this.simpleAutoEquip.getStylesheets().add(ViewPackage.BUTTONS_STYLE_FILE_PATH);
		this.simpleAutoEquip.setOnMouseEntered(e -> SettingsMenu.this.simpleAutoEquip.requestFocus());
		this.vBox.getChildren().add(this.simpleAutoEquip);

		this.autoEquip.setText("Auto equip");
		this.autoEquip.setTooltip(new Tooltip("Advanced auto equip feature"));
		this.autoEquip.getStylesheets().add(ViewPackage.BUTTONS_STYLE_FILE_PATH);
		this.autoEquip.setOnMouseEntered(e -> SettingsMenu.this.autoEquip.requestFocus());
		this.vBox.getChildren().add(this.autoEquip);

		this.autoEquipCanDrop.setText("Auto equip can drop");
		this.autoEquipCanDrop.setTooltip(new Tooltip(
		  "Advanced auto equip feature: drop items on floor when the inventory is full"));
		this.autoEquipCanDrop.getStylesheets().add(ViewPackage.BUTTONS_STYLE_FILE_PATH);
		this.autoEquipCanDrop.setOnMouseEntered(e -> SettingsMenu.this.autoEquipCanDrop.requestFocus());
		this.vBox.getChildren().add(this.autoEquipCanDrop);

		this.permaDeath.setText("Permanent death");
		this.permaDeath.setTooltip(new Tooltip("Deletion of the save file upon death"));
		this.permaDeath.getStylesheets().add(ViewPackage.BUTTONS_STYLE_FILE_PATH);
		this.permaDeath.setOnMouseEntered(e -> SettingsMenu.this.permaDeath.requestFocus());
		this.vBox.getChildren().add(this.permaDeath);

		this.mouseInverted.setText("Inverted mouse");
		this.mouseInverted.setTooltip(new Tooltip("Mouse clicks are inverted"));
		this.mouseInverted.getStylesheets().add(ViewPackage.BUTTONS_STYLE_FILE_PATH);
		this.mouseInverted.setOnMouseEntered(e -> SettingsMenu.this.mouseInverted.requestFocus());
		this.vBox.getChildren().add(this.mouseInverted);

		this.skipIntro.setText("Skip intro");
		this.skipIntro.setTooltip(new Tooltip("Intro should be skipped"));
		this.skipIntro.getStylesheets().add(ViewPackage.BUTTONS_STYLE_FILE_PATH);
		this.skipIntro.setOnMouseEntered(e -> SettingsMenu.this.skipIntro.requestFocus());
		this.vBox.getChildren().add(this.skipIntro);

		DecimalFormat df = new DecimalFormat("0.0");
		this.difficultyScroller.valueProperty().addListener(e -> this.difficultyLabel.setText("Difficulty: " + df.format(this.difficultyScroller.getValue())));

		this.difficultyScroller.setMin(0);
		this.difficultyScroller.setMax(2);
		this.difficultyScroller.setUnitIncrement(0.1);
		this.difficultyScroller.setValue(Settings.difficulty);
		this.difficultyHBox.getChildren().addAll(this.difficultyScroller, this.difficultyLabel);

		this.difficultyHBox.setAlignment(Pos.CENTER);
		this.difficultyHBox.setSpacing(BUTTONS_SPACING / 2);
		this.vBox.getChildren().add(this.difficultyHBox);

		this.save.setText("Save");
		this.save.getStylesheets().add(ViewPackage.BUTTONS_STYLE_FILE_PATH);
		this.save.setOnMouseEntered(e -> SettingsMenu.this.save.requestFocus());
		this.save.setOnAction(e -> {
			applyValues();
			Settings.saveSettings();
		});
		this.buttonsHBox.getChildren().add(this.save);

		this.cancel.setText("Cancel");
		this.cancel.getStylesheets().add(ViewPackage.BUTTONS_STYLE_FILE_PATH);
		this.cancel.setOnMouseEntered(e -> SettingsMenu.this.cancel.requestFocus());
		this.cancel.setOnAction(e -> refreshValues());
		this.buttonsHBox.getChildren().add(this.cancel);
		this.buttonsHBox.setAlignment(Pos.CENTER);
		this.buttonsHBox.setSpacing(BUTTONS_SPACING);
		this.vBox.getChildren().add(this.buttonsHBox);

		this.exit.setText("Exit");
		this.exit.getStylesheets().add(ViewPackage.BUTTONS_STYLE_FILE_PATH);
		this.exit.setOnMouseEntered(e -> SettingsMenu.this.exit.requestFocus());
		this.exit.setOnAction(e -> {
			refreshValues();
			SettingsMenu.this.manager.exitSubMenu();
		});
		this.vBox.getChildren().add(this.exit);

		this.vBox.minWidthProperty().bind(this.mainStackPane.widthProperty());
		this.vBox.minHeightProperty().bind(this.mainStackPane.heightProperty());
		this.vBox.setAlignment(Pos.CENTER);
		this.vBox.setSpacing(BUTTONS_SPACING);

		this.mainStackPane.setMinWidth(MENU_MIN_WIDTH);
		this.mainStackPane.setMinHeight(MENU_MIN_HEIGHT);
		this.mainStackPane.getChildren().add(this.vBox);

		refreshValues();
	}

	/**
	 * Gets scene.
	 *
	 * @return the scene
	 */
	public Scene getScene() {
		return this.scene;
	}

	private void refreshValues() {
		this.simpleAutoEquip.setSelected(Settings.simpleAutoEquip);
		this.autoEquip.setSelected(Settings.autoEquip);
		this.autoEquipCanDrop.setSelected(Settings.autoEquipCanDrop);
		this.permaDeath.setSelected(Settings.permaDeath);
		this.mouseInverted.setSelected(Settings.mouseInverted);
		this.skipIntro.setSelected(Settings.skipIntro);
		this.difficultyScroller.setValue(Settings.difficulty);
	}

	private void applyValues() {
		Settings.simpleAutoEquip = this.simpleAutoEquip.selectedProperty().getValue();
		Settings.autoEquip = this.autoEquip.selectedProperty().getValue();
		Settings.autoEquipCanDrop = this.autoEquipCanDrop.selectedProperty().getValue();
		Settings.permaDeath = this.permaDeath.selectedProperty().getValue();
		Settings.mouseInverted = this.mouseInverted.selectedProperty().getValue();
		Settings.skipIntro = this.skipIntro.selectedProperty().getValue();
		Settings.difficulty = this.difficultyScroller.getValue();
	}

}
