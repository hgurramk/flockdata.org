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

package org.flockdata.engine.concept.service;

import org.flockdata.engine.track.service.ConceptService;
import org.flockdata.model.DocumentType;
import org.flockdata.model.Fortress;
import org.flockdata.track.bean.EntityInputBean;
import org.neo4j.kernel.DeadlockDetectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.transaction.HeuristicRollbackException;
import java.util.concurrent.Future;

/**
 * User: mike
 * Date: 2/12/14
 * Time: 7:48 AM
 */
@Service
@Async("fd-engine")
public class DocTypeRetryService {
    @Autowired
    ConceptService conceptService;

    private Logger logger = LoggerFactory.getLogger(DocTypeRetryService.class);

    @Retryable(include = {HeuristicRollbackException.class, DataRetrievalFailureException.class, InvalidDataAccessResourceUsageException.class, ConcurrencyFailureException.class, DeadlockDetectedException.class}, maxAttempts = 20, backoff = @Backoff(delay = 150, maxDelay = 500))
    public Future<DocumentType> createDocTypes(Fortress fortress,  EntityInputBean inputBean) {
        DocumentType result;
        if (inputBean.getDocumentType() == null)
            // OldWay
            result = new DocumentType(fortress, inputBean.getDocumentName());
        else
        //  new way DAT-498 - a way to pass the DocType properties
            result =  new DocumentType(fortress, inputBean.getDocumentType());

        result = conceptService.findOrCreate(fortress, result);
        logger.debug("Finished result = {}"+ result);
        return new AsyncResult<>(result);
    }
}
