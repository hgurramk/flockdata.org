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

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: mike
 * Date: 31/01/14
 * Time: 6:27 PM
 * http://www.asyncdev.net/2011/12/spring-restful-controllers-and-error-handling/
 */
public class JsonMessage {
    private String message;

    public JsonMessage(String message) {
        this.message = message;
    }
    public ModelAndView asModelAndViewError() {
        MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
        HashMap<String,Object> map = new HashMap<>();
        map.put("message", message);
        return new ModelAndView(jsonView, map );
    }
    public ModelAndView asModelAndViewMessage() {
        MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
        HashMap<String,Object> map = new HashMap<>();
        map.put("message", message);
        return new ModelAndView(jsonView, map );
    }

}
