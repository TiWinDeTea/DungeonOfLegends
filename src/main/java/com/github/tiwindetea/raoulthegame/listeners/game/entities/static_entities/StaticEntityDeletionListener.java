//////////////////////////////////////////////////////////////////////////////////
//                                                                              //
//     This Source Code Form is subject to the terms of the Mozilla Public      //
//     License, v. 2.0. If a copy of the MPL was not distributed with this      //
//     file, You can obtain one at http://mozilla.org/MPL/2.0/.                 //
//                                                                              //
//////////////////////////////////////////////////////////////////////////////////

package com.github.tiwindetea.raoulthegame.listeners.game.entities.static_entities;

import com.github.tiwindetea.raoulthegame.events.static_entities.StaticEntityDeletionEvent;

/**
 * The interface StaticEntityDeletionListener
 * @author Maxime PINARD
 */
public interface StaticEntityDeletionListener {
	/**
	 * Handler associated to a StaticEntityDeletionEvent
	 * @param e Event to handle
	 */
	void deleteStaticEntity(StaticEntityDeletionEvent e);
}