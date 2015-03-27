/*
 * Copyright (c) 2012-2015 "FlockData LLC"
 *
 * This file is part of FlockData.
 *
 * FlockData is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FlockData is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FlockData.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.flockdata.engine.track.service;

import org.flockdata.track.bean.TrackResultBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by mike on 21/03/15.
 */
public class TrackBatchSplitter {

    /**
     * inputs is modified such that it will only contain existing TrackResult entities
     *
     * @param inputs all entities to consider - this collection is modified
     * @return Entities from inputs that are new
     */
    public static Collection<TrackResultBean> splitEntityResults(Collection<TrackResultBean> inputs){

        Collection<TrackResultBean> newEntities = new ArrayList<>();
        Iterator<TrackResultBean>iterator = inputs.iterator();
        while (iterator.hasNext()){
            TrackResultBean track = iterator.next();
            if (track.getEntity().isNew()) {
                newEntities.add(track);
                iterator.remove();
            }
        }
        return newEntities;
    }
}