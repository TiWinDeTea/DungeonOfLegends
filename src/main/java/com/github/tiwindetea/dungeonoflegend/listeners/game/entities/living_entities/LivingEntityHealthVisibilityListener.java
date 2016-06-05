//////////////////////////////////////////////////////////////////////////////////
//                                                                              //
//     This Source Code Form is subject to the terms of the Mozilla Public      //
//     License, v. 2.0. If a copy of the MPL was not distributed with this      //
//     file, You can obtain one at http://mozilla.org/MPL/2.0/.                 //
//                                                                              //
//////////////////////////////////////////////////////////////////////////////////

package com.github.tiwindetea.dungeonoflegend.listeners.game.entities.living_entities;

import com.github.tiwindetea.dungeonoflegend.events.living_entities.LivingEntityHealthVisibilityEvent;

/**
 * Created by Maxime on 22/05/2016.
 */
public interface LivingEntityHealthVisibilityListener {
	void setLivingEntityHealthVisibility(LivingEntityHealthVisibilityEvent e);
}
