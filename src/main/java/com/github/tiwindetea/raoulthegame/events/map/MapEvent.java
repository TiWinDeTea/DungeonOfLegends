//////////////////////////////////////////////////////////////////////////////////
//                                                                              //
//     This Source Code Form is subject to the terms of the Mozilla Public      //
//     License, v. 2.0. If a copy of the MPL was not distributed with this      //
//     file, You can obtain one at http://mozilla.org/MPL/2.0/.                 //
//                                                                              //
//////////////////////////////////////////////////////////////////////////////////

package com.github.tiwindetea.raoulthegame.events.map;

import com.github.tiwindetea.raoulthegame.events.Event;
import com.github.tiwindetea.raoulthegame.events.EventType;

/**
 * The type MapEvent.
 *
 * @author Maxime PINARD
 * @author Lucas LAZARE
 */
public abstract class MapEvent extends Event {

    public EventType getType() {
        return EventType.MAP_EVENT;
    }

    /**
     * Gets sub type.
     *
     * @return the sub type
     */
    public abstract MapEventType getSubType();
}