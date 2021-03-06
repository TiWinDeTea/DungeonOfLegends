//////////////////////////////////////////////////////////////////////////////////
//                                                                              //
//     This Source Code Form is subject to the terms of the Mozilla Public      //
//     License, v. 2.0. If a copy of the MPL was not distributed with this      //
//     file, You can obtain one at http://mozilla.org/MPL/2.0/.                 //
//                                                                              //
//////////////////////////////////////////////////////////////////////////////////

package com.github.tiwindetea.raoulthegame.listeners.game.entities;

import com.github.tiwindetea.raoulthegame.listeners.game.entities.living_entities.LivingEntityListener;
import com.github.tiwindetea.raoulthegame.listeners.game.entities.static_entities.StaticEntityListener;

/**
 * The interface EntityListener, that regroups some other listeners.
 *
 * @author Maxime PINARD
 */
public interface EntityListener extends LivingEntityListener, StaticEntityListener {
}
