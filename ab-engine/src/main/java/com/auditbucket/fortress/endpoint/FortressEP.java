/*
 * Copyright (c) 2012-2014 "Monowai Developments Limited"
 *
 * This file is part of AuditBucket.
 *
 * AuditBucket is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AuditBucket is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AuditBucket.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.auditbucket.fortress.endpoint;

import com.auditbucket.engine.service.FortressService;
import com.auditbucket.helper.CompanyResolver;
import com.auditbucket.helper.DatagioException;
import com.auditbucket.helper.SecurityHelper;
import com.auditbucket.registration.bean.FortressInputBean;
import com.auditbucket.registration.model.Company;
import com.auditbucket.registration.model.Fortress;
import com.auditbucket.registration.service.CompanyService;
import com.auditbucket.track.bean.DocumentResultBean;
import com.auditbucket.track.model.DocumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: Mike Holdsworth
 * Date: 4/05/13
 * Time: 8:23 PM
 */
@Controller
@RequestMapping("/fortress")
public class FortressEP {

    @Autowired
    FortressService fortressService;

    @Autowired
    CompanyService companyService;

    @Autowired
    SecurityHelper securityHelper;

    @RequestMapping(value = "/", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Collection<Fortress> findFortresses(HttpServletRequest request) throws DatagioException {
        // curl -u mike:123 -X GET  http://localhost:8080/ab/company/Monowai/fortresses
        Company company = CompanyResolver.resolveCompany(request);
        return fortressService.findFortresses(company);
    }

    @RequestMapping(value = "/", produces = "application/json", consumes = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Fortress> registerFortress( @RequestBody FortressInputBean fortressInputBean, HttpServletRequest request) throws DatagioException {
        Company company = CompanyResolver.resolveCompany(request);
        Fortress fortress = fortressService.registerFortress(company, fortressInputBean, true);
        fortressInputBean.setFortressKey(fortress.getFortressKey());
        return new ResponseEntity<>(fortress, HttpStatus.CREATED);

    }

    @RequestMapping(value = "/{code}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Fortress> getFortress(@PathVariable("code") String fortressName, HttpServletRequest request) throws DatagioException {
        Company company = CompanyResolver.resolveCompany(request);
        Fortress fortress = fortressService.findByCode(company, fortressName);
        if (fortress == null)
            return new ResponseEntity<>(fortress, HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(fortress, HttpStatus.OK);
    }

    @RequestMapping(value = "/{code}/docs", method = RequestMethod.GET)
    @ResponseBody
    public Collection<DocumentResultBean> getDocumentTypes(@PathVariable("code") String code, HttpServletRequest request) throws DatagioException {
        Company company = CompanyResolver.resolveCompany(request);
        return  fortressService.getFortressDocumentsInUse(company, code);
    }
}