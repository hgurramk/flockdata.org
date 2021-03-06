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

package org.flockdata.search.endpoint;

import org.flockdata.search.service.SearchAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * User: mike
 * Date: 5/05/14
 * Time: 9:25 AM
 */
@RequestMapping("/admin")
@RestController
public class AdminEP {
    @Autowired
    SearchAdmin searchAdmin;

    @RequestMapping(value = "/ping", method = RequestMethod.GET)

    String ping() throws Exception {
        // curl -X GET http://localhost:8081/fd-search/v1/admin/ping
        return "pong";
    }

    @RequestMapping(value = "/health", produces = "application/json", method = RequestMethod.GET)

    public Map<String, Object> getHealth() throws Exception {
        // curl -u mike:123 -X GET http://localhost:8081/fd-search/v1/admin/health
        return searchAdmin.getHealth();
    }

}
