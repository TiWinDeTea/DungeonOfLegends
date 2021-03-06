//////////////////////////////////////////////////////////////////////////////////
//                                                                              //
//     This Source Code Form is subject to the terms of the Mozilla Public      //
//     License, v. 2.0. If a copy of the MPL was not distributed with this      //
//     file, You can obtain one at http://mozilla.org/MPL/2.0/.                 //
//                                                                              //
//////////////////////////////////////////////////////////////////////////////////

package com.github.tiwindetea.raoulthegame.listeners.gui.playerinventory;

import com.github.tiwindetea.raoulthegame.events.gui.playerinventory.ObjectDragEvent;

/**
 * The interface ObjectDragListener.
 *
 * @author Lucas LAZARE
 */
public interface ObjectDragListener {

	/**
	 * Handler associated to an ObjectDragEvent.
	 *
	 * @param e Event to handle
	 */
	void handle(ObjectDragEvent e);
}
