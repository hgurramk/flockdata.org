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

package org.flockdata.engine.admin.endpoint;

import org.flockdata.engine.PlatformConfig;
import org.flockdata.helper.ApiKeyInterceptor;
import org.flockdata.helper.CompanyResolver;
import org.flockdata.helper.FlockException;
import org.flockdata.helper.SecurityHelper;
import org.flockdata.model.Company;
import org.flockdata.track.service.MediationFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Engine admin
 * User: mike
 * Date: 15/04/14
 * Time: 9:09 PM
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("/admin")
public class AdminEP {

    @Qualifier("mediationFacadeNeo")
    @Autowired
    MediationFacade mediationFacade;

    @Autowired
    SecurityHelper securityHelper;

    @Autowired
    PlatformConfig engineConfig;

    private static Logger logger = LoggerFactory.getLogger(AdminEP.class);

    @RequestMapping(value = "/cache", method = RequestMethod.DELETE)
    public void resetCache() {
        engineConfig.resetCache();

    }


    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public String getPing() {
        // curl -X GET http://localhost:8081/fd-engine/v1/track/ping
        return engineConfig.authPing();
    }


    @RequestMapping(value = "/health", method = RequestMethod.GET)
    public Map<String, String> getHealth(HttpServletRequest request) throws FlockException {
        Object o = request.getAttribute(ApiKeyInterceptor.API_KEY);
        if ( o == null )
            o = request.getHeader(ApiKeyInterceptor.API_KEY);
        String apiKey = "";
        if (o != null )
            apiKey = o.toString();

        if ( "".equals(apiKey))
            apiKey = null;
        if ( request.getAttribute(ApiKeyInterceptor.COMPANY) == null &&
                 apiKey == null )
            return engineConfig.getHealthAuth();// Caller may have admin role but not belong to a company
        return engineConfig.getHealth();
    }


    @RequestMapping(value = "/{fortressCode}/rebuild", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public ResponseEntity<String> rebuildSearch(@PathVariable("fortressCode") String fortressCode,
                                                HttpServletRequest request) throws FlockException {
        Company company = CompanyResolver.resolveCompany(request);
        logger.info("Reindex command received for " + fortressCode + " from [" + securityHelper.getLoggedInUser() + "]");
        String message = mediationFacade.reindex(company, fortressCode);
        return new ResponseEntity<>(message, HttpStatus.ACCEPTED);
    }


    @RequestMapping(value = "/{fortressName}/{docType}/rebuild", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public ResponseEntity<String> rebuildSearch(@PathVariable("fortressName") String fortressName, @PathVariable("docType") String docType,
                                                HttpServletRequest request) throws FlockException {
        Company company = CompanyResolver.resolveCompany(request);

        logger.info("Reindex command received for " + fortressName + " & docType " + docType + " from [" + securityHelper.getLoggedInUser() + "]");
        String message = mediationFacade.reindexByDocType(company, fortressName, docType);
        return new ResponseEntity<>(message, HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/{fortressName}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public ResponseEntity<String> purgeFortress(@PathVariable("fortressName") String fortressCode,
                                                HttpServletRequest request) throws FlockException {
        Company company = CompanyResolver.resolveCompany(request);

        mediationFacade.purge(company, fortressCode);
        return new ResponseEntity<>("Purging " + fortressCode + "... This may take a while", HttpStatus.ACCEPTED);

    }


    @RequestMapping(value = "/{fortressName}/{docType}/validate", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public ResponseEntity<String> validateFromSearch(@PathVariable("fortressName") String fortressName, @PathVariable("docType") String docType,
                                                HttpServletRequest request) throws FlockException {
        Company company = CompanyResolver.resolveCompany(request);

        logger.info("Validate command received for " + fortressName + " & docType " + docType + " from [" + securityHelper.getLoggedInUser() + "]");
        String message = mediationFacade.validateFromSearch(company, fortressName, docType);

        return new ResponseEntity<>(message, HttpStatus.ACCEPTED);
    }

}
