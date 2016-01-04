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

package org.flockdata.helper;

import org.flockdata.search.model.SearchResults;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * Convert String to SearchResult.
 */
@Component("jsonToSearchResultConverter")
public class JsonToSearchResultConverter extends SimpleMessageConverter {

    @Override
    public Object fromMessage(final Message message) throws MessageConversionException {
        final Object content = super.fromMessage(message);
        try {
            if (content instanceof String) {
                ObjectMapper mapper = FdJsonObjectMapper.getObjectMapper();
                return mapper.readValue(((String) content).getBytes(ObjectHelper.charSet), SearchResults.class);
            }
        } catch (IOException e1) {
            throw new MessageConversionException("failed to convert text-based Message content", e1);
        }
        return content;
    }
}
