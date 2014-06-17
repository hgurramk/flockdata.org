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

package com.auditbucket.engine.repo.neo4j;

import com.auditbucket.engine.repo.neo4j.model.ConceptNode;
import com.auditbucket.registration.model.Company;
import com.auditbucket.track.model.Concept;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.Collection;
import java.util.Set;

/**
 * User: mike
 * Date: 16/06/14
 * Time: 10:33 AM
 */
public interface ConceptTypeRepo extends GraphRepository<ConceptNode> {

    @Query(elementClass = ConceptNode.class,
            value =
                    "MATCH (company:ABCompany) -[:OWNS]->(fortress:_Fortress)<-[:FORTRESS_DOC]-(doc:_DocType) " +
                            " -[:HAS_CONCEPT]->(concept:_Concept)" +
                            "        where id(company)={0} and doc.name in{1}" +
                            "       return concept")

    Set<ConceptNode> findConceptNodes(Company company, Collection<String> documents);

    @Query(elementClass = ConceptNode.class,
            value =
                    "MATCH (company:ABCompany) -[:OWNS]->(fortress:_Fortress)<-[:FORTRESS_DOC]-(doc:_DocType) " +
                            " -[:HAS_CONCEPT]->(concept:_Concept)" +
                            "        where id(company)={0} and doc.name in{1}" +
                            "       return concept")

    Set<Concept> findConcepts(Company company, Collection<String> documents);

}