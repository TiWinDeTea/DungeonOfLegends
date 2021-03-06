//////////////////////////////////////////////////////////////////////////////////
//                                                                              //
//     This Source Code Form is subject to the terms of the Mozilla Public      //
//     License, v. 2.0. If a copy of the MPL was not distributed with this      //
//     file, You can obtain one at http://mozilla.org/MPL/2.0/.                 //
//                                                                              //
//////////////////////////////////////////////////////////////////////////////////

package com.github.tiwindetea.raoulthegame.view;

import com.github.tiwindetea.raoulthegame.events.gui.spellselector.SelectorSpellClickEvent;
import com.github.tiwindetea.raoulthegame.listeners.gui.spellselector.SelectorSpellClickListener;
import com.github.tiwindetea.raoulthegame.view.entities.SpellType;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Collection;

/**
 * The type SpellSelector.
 *
 * @author Maxime PINARD
 */
public class SpellSelector extends Parent {

	private static final Font TEXT_FONT = ViewPackage.getMainFont(15);
	private static final Color TEXT_COLOR = Color.WHITE;
	private static final int TITLE_SPACE = 10;

	private SelectorSpellClickListener listener;

	private final ImageView backgroundImage = new ImageView(ViewPackage.DIALOG_IMAGE);

	private VBox vBox = new VBox();
	private HBox hBox = new HBox();

	/**
	 * Instantiates a new SpellSelector.
	 *
	 * @param text       the text
	 * @param spellTypes the spell types
	 * @param listener   the spell click listener
	 */
	public SpellSelector(String text, Collection<SpellType> spellTypes, SelectorSpellClickListener listener) {
		this.listener = listener;
		//getChildren().add(this.backgroundImage);

		Label label = new Label(text);
		label.setFont(TEXT_FONT);
		label.setTextFill(TEXT_COLOR);
		StackPane labelStackPane = new StackPane();
		labelStackPane.getChildren().add(label);
		this.vBox.getChildren().add(labelStackPane);

		double width = this.backgroundImage.getBoundsInParent().getWidth();
		double height = this.backgroundImage.getBoundsInParent().getHeight();
		double deltaX = (width - (spellTypes.size() * ViewPackage.SPRITES_SIZE.x)) / (spellTypes.size() + 1);

		for(SpellType spellType : spellTypes) {
			ImageView imageView = new ImageView();
			imageView.setImage(spellType.getImage());
			imageView.setViewport(new Rectangle2D(
			  spellType.getSpritePosition().x * ViewPackage.SPRITES_SIZE.x,
			  spellType.getSpritePosition().y * ViewPackage.SPRITES_SIZE.y,
			  ViewPackage.SPRITES_SIZE.x,
			  ViewPackage.SPRITES_SIZE.y)
			);
			imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					SpellSelector.this.listener.handle(new SelectorSpellClickEvent(spellType));
				}
			});
			this.hBox.getChildren().add(imageView);
		}
		this.hBox.setSpacing(deltaX);
		StackPane spellsStackpane = new StackPane();
		spellsStackpane.getChildren().add(this.backgroundImage);
		spellsStackpane.getChildren().add(this.hBox);
		this.hBox.setMaxSize(spellTypes.size() * ViewPackage.SPRITES_SIZE.x + (spellTypes.size() - 1) * deltaX, ViewPackage.SPRITES_SIZE.y);
		spellsStackpane.setMinSize(width + 10, height + 10);
		this.vBox.getChildren().add(spellsStackpane);

		this.vBox.setPrefWidth(width);
		this.vBox.setSpacing(TITLE_SPACE);
		getChildren().add(this.vBox);
	}
}
