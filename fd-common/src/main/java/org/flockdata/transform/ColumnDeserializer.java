/*
 * Copyright (c) 2012-2014 "FlockData LLC"
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

package org.flockdata.transform;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flockdata.helper.FdJsonObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

/**
 * User: mike
 * Date: 27/05/14
 * Time: 4:25 PM
 */
public class ColumnDeserializer extends JsonDeserializer<ArrayList<ColumnDefinition>> {
    @Override
    public ArrayList<ColumnDefinition> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ArrayList<ColumnDefinition> values = new ArrayList<>();
        JsonNode node = jp.getCodec().readTree(jp);
        ObjectMapper om = FdJsonObjectMapper.getObjectMapper();
        for (JsonNode jsonNode : node) {
            values.add(om.readValue(jsonNode.toString(), ColumnDefinition.class));

        }
        return values;
    }
}
