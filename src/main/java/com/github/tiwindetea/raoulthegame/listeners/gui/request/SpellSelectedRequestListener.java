//////////////////////////////////////////////////////////////////////////////////
//                                                                              //
//     This Source Code Form is subject to the terms of the Mozilla Public      //
//     License, v. 2.0. If a copy of the MPL was not distributed with this      //
//     file, You can obtain one at http://mozilla.org/MPL/2.0/.                 //
//                                                                              //
//////////////////////////////////////////////////////////////////////////////////

package com.github.tiwindetea.raoulthegame.listeners.gui.request;

import com.github.tiwindetea.raoulthegame.events.gui.requests.SpellSelectedRequestEvent;

/**
 * The interface SpellSelectedRequestListener.
 *
 * @author Maxime PINARD
 */
public interface SpellSelectedRequestListener {

	/**
	 * Handler associated to a SpellSelectedRequestEvent.
	 *
	 * @param e Event to handle
	 */
	void handle(SpellSelectedRequestEvent e);
}
