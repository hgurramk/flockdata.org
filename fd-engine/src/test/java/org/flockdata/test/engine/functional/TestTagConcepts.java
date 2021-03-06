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

package org.flockdata.test.engine.functional;

import junit.framework.TestCase;
import org.flockdata.registration.bean.FortressInputBean;
import org.flockdata.registration.bean.TagInputBean;
import org.flockdata.model.Fortress;
import org.flockdata.model.SystemUser;
import org.flockdata.track.bean.ConceptResultBean;
import org.flockdata.track.bean.DocumentResultBean;
import org.flockdata.track.bean.EntityInputBean;
import org.flockdata.track.bean.RelationshipResultBean;
import org.flockdata.model.DocumentType;
import org.flockdata.model.Entity;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertTrue;

/**
 * Non transactional tests - these are slower due to cleaning the DB down for each test run
 * User: mike
 * Date: 19/06/14
 * Time: 8:47 AM
 */
public class TestTagConcepts extends EngineBase {
    private Logger logger = LoggerFactory.getLogger(TestTagConcepts.class);

    @Override
    public void cleanUpGraph() {
        engineConfig.setTestMode(true);
        super.cleanUpGraph();
    }

    @Test
    public void multipleDocsSameFortress() throws Exception {
        try {
            logger.debug("### multipleDocsSameFortress");

            setSecurity();
            engineConfig.setConceptsEnabled("true");

            Transaction t = beginManualTransaction();
            SystemUser su = registerSystemUser("multipleDocsSameFortress", mike_admin);
            assertNotNull(su);

            Fortress fortress = fortressService.registerFortress(su.getCompany(), new FortressInputBean("multipleDocsSameFortress", true));
            DocumentType dType = conceptService.resolveByDocCode(fortress, "ABC123", true);
            commitManualTransaction(t);// Should only be only one docTypes

            assertNotNull(dType);
            Long id = dType.getId();
            dType = conceptService.resolveByDocCode(fortress, "ABC123", false);
            assertEquals(id, dType.getId());

            EntityInputBean input = new EntityInputBean(fortress.getName(), "jinks", "DocA", new DateTime());
            input.addTag(new TagInputBean("cust123",  "Customer", "purchased").setLabel("Customer"));
            mediationFacade.trackEntity(su.getCompany(), input).getEntity();
            validateConcepts("DocA", su, 1);

            // Different docs, same concepts
            input = new EntityInputBean(fortress.getName(), "jinks", "DocB", new DateTime());
            input.addTag(new TagInputBean("cust123", "Customer",  "purchased").setLabel("Customer"));
            mediationFacade.trackEntity(su.getCompany(), input).getEntity();

            validateConcepts((Collection<String>) null, su, 3); // 3 Doc types.
            assertEquals("Docs In Use not supporting 'null args' for fortress'", 3, queryService.getDocumentsInUse(su.getCompany(), null).size());

            // DAT-112
            Set<DocumentResultBean> found = validateConcepts("DocA", su, 1);
            assertEquals(1, found.size());
            assertEquals(1, found.iterator().next().getConcepts().size());
            found = validateConcepts("DocB", su, 1);
            assertEquals(1, found.size());
            // Removed the mock user
            assertEquals("Didn't find the Document ",1, found.iterator().next().getConcepts().size());
        } finally {
            cleanUpGraph();
        }


    }

    @Test
    public void multipleFortressesSameTag() throws Exception {
        try {
            logger.debug("### multipleFortressesSameTag");

            setSecurity();
            engineConfig.setConceptsEnabled("true");

            Transaction t = beginManualTransaction();
            SystemUser su = registerSystemUser("multipleFortressesSameTag", mike_admin);
            assertNotNull(su);

            Fortress fortressA = fortressService.registerFortress(su.getCompany(), new FortressInputBean("multipleFortressesSameTagA", true));
            Fortress fortressB = fortressService.registerFortress(su.getCompany(), new FortressInputBean("multipleFortressesSameTagB", true));
            commitManualTransaction(t);

            EntityInputBean input = new EntityInputBean(fortressA.getName(), "jinks", "DocA", new DateTime());
            input.addTag(new TagInputBean("cust123", "Customer",  "purchased").setLabel("Customer"));
            mediationFacade.trackEntity(su.getCompany(), input).getEntity();
            Collection<String>documents = new ArrayList<>();
            documents.add("DocA");
            Set<DocumentResultBean> results = conceptService.findConcepts(su.getCompany(), documents, false);
//            assertFalse(results.isEmpty());
            assertEquals(1, results.size());

            input = new EntityInputBean(fortressB.getName(), "jinks", "DocB", new DateTime());
            input.addTag(new TagInputBean("cust123", "Customer",  "purchased").setLabel("Customer"));
            mediationFacade.trackEntity(su.getCompany(), input).getEntity();
            documents.add("DocB");
            results = conceptService.findConcepts(su.getCompany(), documents, false);
            assertEquals(2, results.size());
            schemaService.purge(fortressB);
            results = conceptService.findConcepts(su.getCompany(), documents, false);
            assertEquals(1, results.size());
            Collection<DocumentResultBean> docsInUse = conceptService.getDocumentsInUse(su.getCompany());
            assertEquals(1, docsInUse.size());

        } finally {
            cleanUpGraph();
        }


    }

    @Test
    public void fortressConcepts() throws Exception {
        try {
            logger.debug("### fortressConcepts");

            Transaction t = beginManualTransaction();
            setSecurity();
            SystemUser su = registerSystemUser("fortressConcepts", mike_admin);
            assertNotNull(su);
            engineConfig.setConceptsEnabled("true");
            engineConfig.setTestMode(true);

            Fortress fortA = fortressService.registerFortress(su.getCompany(), new FortressInputBean("fortressConcepts", true));

            DocumentType dType = conceptService.resolveByDocCode(fortA, "ABC123", true);
            commitManualTransaction(t);// Should only be only one docTypes

            assertNotNull(dType);
            Long id = dType.getId();
            dType = conceptService.resolveByDocCode(fortA, "ABC123", false);
            assertEquals(id, dType.getId());

            EntityInputBean input = new EntityInputBean(fortA.getName(), "jinks", "DocA", new DateTime());
            input.addTag(new TagInputBean("cust123", "Customer", "purchased"));
            Entity meta = mediationFacade.trackEntity(su.getCompany(), input).getEntity();

            assertNotNull(entityService.getEntity(su.getCompany(), meta.getMetaKey()));

            input = new EntityInputBean(fortA.getName(), "jinks", "DocA", new DateTime());
            input.addTag(new TagInputBean("cust124", "Customer", "purchased").setLabel("Customer"));

            mediationFacade.trackEntity(su.getCompany(), input).getEntity();

            Collection<String> docs = new ArrayList<>();
            docs.add("DocA");
            Collection<DocumentResultBean> documentTypes = queryService.getConcepts(su.getCompany(), docs);
            assertNotNull(documentTypes);
            assertEquals(1, documentTypes.size());

            // add a second docTypes
            input = new EntityInputBean(fortA.getName(), "jinks", "DocA", new DateTime());
            input.addTag(new TagInputBean("cust123", "Rep", "sold").setLabel("Rep"));
            mediationFacade.trackEntity(su.getCompany(), input);

            documentTypes = queryService.getConceptsWithRelationships(su.getCompany(), docs);
            assertEquals("Only one doc type should exist", 1, documentTypes.size());

            Boolean foundCustomer = false, foundRep = false;

            for (DocumentResultBean docTypes : documentTypes) {
                for (ConceptResultBean concept : docTypes.getConcepts()) {
                    if (concept.getName().equals("Customer")) {
                        foundCustomer = true;
                        assertEquals(1, concept.getRelationships().size());
                        Assert.assertEquals("purchased", concept.getRelationships().iterator().next().getName());
                        assertEquals(true, concept.toString().contains(concept.getName()));
                    }
                    if (concept.getName().equals("Rep")) {
                        foundRep = true;
                        assertEquals(1, concept.getRelationships().size());
                        Assert.assertEquals("sold", concept.getRelationships().iterator().next().getName());
                        assertEquals(true, concept.toString().contains(concept.getName()));
                    }
                }

            }
            assertTrue("Didn't find Customer concept", foundCustomer);
            assertTrue("Didn't find Rep concept", foundRep);
        } finally {
            cleanUpGraph();
        }

    }

    @Test
    public void multipleRelationships() throws Exception {
        try {
            logger.debug("### multipleRelationships");
            setSecurity();
            engineConfig.setConceptsEnabled("true");
            Transaction t = beginManualTransaction();

            SystemUser su = registerSystemUser("multipleRelationships", mike_admin);
            assertNotNull(su);

            Fortress fortress = fortressService.registerFortress(su.getCompany(), new FortressInputBean("multipleRelationships", true));

            DocumentType dType = conceptService.resolveByDocCode(fortress, "ABC123", true);
            commitManualTransaction(t);// Should only be only one docTypes

            assertNotNull(dType);
            Long id = dType.getId();
            dType = conceptService.resolveByDocCode(fortress, "ABC123", false);
            assertEquals(id, dType.getId());

            EntityInputBean input = new EntityInputBean(fortress.getName(), "jinks", "DocA", new DateTime());
            input.addTag(new TagInputBean("cust123",  "Customer", "purchased").setLabel("Customer"));
            input.addTag(new TagInputBean("harry",  "Customer", "soldto").setLabel("Customer"));
            mediationFacade.trackEntity(su.getCompany(), input).getEntity();
            input = new EntityInputBean(fortress.getName(), "jinks", "DocA", new DateTime());
            input.addTag(new TagInputBean("cust121", "Customer",  "purchased").setLabel("Customer"));
            input.addTag(new TagInputBean("harry", "Customer",  "soldto").setLabel("Customer"));
            mediationFacade.trackEntity(su.getCompany(), input).getEntity();
            validateConcepts("DocA", su, 1);

            Collection<String> docs = new ArrayList<>();
            docs.add("DocA");
            Set<DocumentResultBean> docTypes = queryService.getConceptsWithRelationships(su.getCompany(), docs);
            for (DocumentResultBean docType : docTypes) {
                Collection<ConceptResultBean> concepts = docType.getConcepts();
                for (ConceptResultBean concept : concepts) {
                    Collection<RelationshipResultBean> relationships = concept.getRelationships();
                    for (RelationshipResultBean relationship : relationships) {
                        logger.debug(relationship.getName());
                    }
                    if ( concept.getName().equals("User")){
                        // Currently only tracking the created. Should be 2 when tracking the updated
                        assertEquals(1, relationships.size());
                    } else
                        assertEquals(2, relationships.size());

                }
            }
            assertEquals("Docs In Use not supporting 'null args'", 2, queryService.getConceptsWithRelationships(su.getCompany(), null).size());
        } finally {
            cleanUpGraph();
        }

    }

    @Test
    public void relationshipWorkForMultipleDocuments() throws Exception {
        try {
            logger.debug("### relationshipWorkForMultipleDocuments");
            setSecurity();
            engineConfig.setConceptsEnabled("true");
            engineConfig.setTestMode(true);

            Transaction t = beginManualTransaction();

            SystemUser su = registerSystemUser("relationshipWorkForMultipleDocuments", mike_admin);
            assertNotNull(su);

            Fortress fortress = fortressService.registerFortress(su.getCompany(), new FortressInputBean("relationshipWorkForMultipleDocuments", true));

            DocumentType docA = conceptService.resolveByDocCode(fortress, "DOCA", true);
            DocumentType docB = conceptService.resolveByDocCode(fortress, "DOCB", true);
            commitManualTransaction(t);// Should only be only one docTypes

            assertNotNull(docA);
            Long idA = docA.getId();
            docA = conceptService.resolveByDocCode(fortress, docA.getName(), false);
            assertEquals(idA, docA.getId());

            EntityInputBean input = new EntityInputBean(fortress.getName(), "jinks", "DocA", new DateTime());
            input.addTag(new TagInputBean("cust123",  "Customer", "purchased").setLabel("Customer"));
            mediationFacade.trackEntity(su.getCompany(), input).getEntity();
            input = new EntityInputBean(fortress.getName(), "jinks", docB.getName(), new DateTime());
            input.addTag(new TagInputBean("cust121",  "Customer", "purchased").setLabel("Customer"));
            mediationFacade.trackEntity(su.getCompany(), input).getEntity();

            Collection<String> docs = new ArrayList<>();
            docs.add(docA.getName());
            docs.add(docB.getName());
            boolean docAFound = false;
            boolean docBFound = false;
            Set<DocumentResultBean> docTypes = queryService.getConceptsWithRelationships(su.getCompany(), docs);
            for (DocumentResultBean docType : docTypes) {
                Collection<ConceptResultBean> concepts = docType.getConcepts();
                for (ConceptResultBean concept : concepts) {
                    Collection<RelationshipResultBean> relationships = concept.getRelationships();
                    TestCase.assertEquals(1, relationships.size());
//                    for (RelationshipResultBean relationship : relationships) {
//                        assertEquals(1, relationship.getDocumentTypes().size());
//                        if (docType.getName().equals(docA.getName()))
//                            docAFound = true;
//                        else if (docType.getName().equals(docB.getName()))
//                            docBFound = true;
//                    }
                }
            }
            // ToDo: it is unclear if we should track in this manner
//            assertTrue("DocA Not Found in the concept", docAFound);
//            assertTrue("DocB Not Found in the concept", docBFound);
            assertEquals("Docs In Use not supporting 'null args'", 2, queryService.getConceptsWithRelationships(su.getCompany(), null).size());
        } finally {
            cleanUpGraph();
        }

    }

    /**
     * Assert that we only get back relationships for a the selected document type. Checks that
     * Relationships, created via an association to a tag (Linux:Tag), can be filtered by doc type.
     * e.g. Sales and Promo both have a differently named relationship to the Device tag. When retrieving
     * Sales, we should only get the "purchased" relationship. Likewise with Promo, we should only get the "offer"
     *
     * @throws Exception
     */
    @Test
    public void uniqueRelationshipByDocType() throws Exception {
        try {
            logger.debug("### uniqueRelationshipByDocType");
            setSecurity();
            engineConfig.setConceptsEnabled("true");
            engineConfig.setTestMode(true);

            Transaction t = beginManualTransaction();

            SystemUser su = registerSystemUser("uniqueRelationshipByDocType", mike_admin);
            assertNotNull(su);

            Fortress fortress = fortressService.registerFortress(su.getCompany(), new FortressInputBean("fortA", true));

            DocumentType sale = conceptService.resolveByDocCode(fortress, "Sale", true);
            commitManualTransaction(t);
            t = beginManualTransaction();
            DocumentType promo = conceptService.resolveByDocCode(fortress, "Promotion", true);
            commitManualTransaction(t);

            EntityInputBean promoInput = new EntityInputBean(fortress.getName(), "jinks", promo.getName(), new DateTime());
            promoInput.addTag(new TagInputBean("Linux", "Device", "offer").setLabel("Device"));
            //promoInput.addTag(new TagInputBean("Mike", "sold").setLabel("Person"));
            mediationFacade.trackEntity(su.getCompany(), promoInput).getEntity();

            EntityInputBean salesInput = new EntityInputBean(fortress.getName(), "jinks", sale.getName(), new DateTime());
            salesInput.addTag(new TagInputBean("Linux", "Device", "purchased").setLabel("Device"));
            //promoInput.addTag(new TagInputBean("Gary", "authorised").setLabel("Person"));
            mediationFacade.trackEntity(su.getCompany(), salesInput).getEntity();

            Collection<String> docs = new ArrayList<>();
            docs.add(promo.getName());
            docs.add(sale.getName());
            validateConcepts(docs, su, 2);
            docs.clear();
            docs.add(promo.getName());
            Set<DocumentResultBean> foundDocs = validateConcepts(docs, su, 1);
            for (DocumentResultBean foundDoc : foundDocs) {
                assertEquals("Promotion", foundDoc.getName());
                Collection<ConceptResultBean> concepts = foundDoc.getConcepts();
                assertEquals(1, concepts.size());
                boolean deviceFound = false;
//                boolean userFound = false;
                for (ConceptResultBean concept : concepts) {
                    if (concept.getName().equalsIgnoreCase("Device")) {
                        deviceFound = true;
                        assertEquals(1, concept.getRelationships().size());
                    }

                }
                assertEquals(true, deviceFound );
            }
        } finally {
            cleanUpGraph();
        }


    }

    @Test
    public void purgeFortressRemovesConcepts() throws Exception {
        try {
            logger.debug("### uniqueRelationshipByDocType");
            setSecurity();

            engineConfig.setConceptsEnabled("true");
            engineConfig.setTestMode(true);

            Transaction t;

            SystemUser su = registerSystemUser("relationshipWorkForMultipleDocuments", mike_admin);
            assertNotNull(su);

            Fortress fortress = fortressService.registerFortress(su.getCompany(), new FortressInputBean("relationshipWorkForMultipleDocuments", true));

            t = beginManualTransaction();
            DocumentType claim = conceptService.resolveByDocCode(fortress, "Claim", true);
            commitManualTransaction(t);

            EntityInputBean promoInput = new EntityInputBean(fortress.getName(),
                    "jinks",
                    claim.getName(),
                    new DateTime());
            promoInput.addTag(
                    new TagInputBean("a1065", "Claim", "identifier").setLabel("Claim"));

            mediationFacade.trackEntity(su.getCompany(), promoInput).getEntity();

            Collection<String> docs = new ArrayList<>();
            docs.add(claim.getName());
            validateConcepts(docs, su, 1);
            docs.clear();
            docs.add(claim.getName());
            Set<DocumentResultBean> foundDocs = validateConcepts(docs, su, 1);
            for (DocumentResultBean foundDoc : foundDocs) {
                assertEquals("Claim", foundDoc.getName());
                Collection<ConceptResultBean> concepts = foundDoc.getConcepts();
                assertEquals(1, concepts.size());
                boolean claimFound = false;
                boolean userFound = false;
                for (ConceptResultBean concept : concepts) {
                    if (concept.getName().equalsIgnoreCase("Claim")) {
                        claimFound = true;
                        assertEquals(1, concept.getRelationships().size());
                    }

                }

                assertEquals(true, claimFound );
                logger.info(foundDoc.toString());
            }
            mediationFacade.purge( fortress);
            waitAWhile("Waiting for Async processing to complete");
            assertEquals(0, conceptService.getDocumentsInUse(fortress.getCompany()).size());
        } finally {
            cleanUpGraph();
        }

    }

    private Set<DocumentResultBean> validateConcepts(String document, SystemUser su, int expected) throws Exception {
        Collection<String> docs = new ArrayList<>();

        docs.add(document);
        return validateConcepts(docs, su, expected);
    }

    private Set<DocumentResultBean> validateConcepts(Collection<String> docs, SystemUser su, int expected) throws Exception {
        Set<DocumentResultBean> concepts = queryService.getConcepts(su.getCompany(), docs, true);
        String message = "Collection";
        if (docs != null && docs.size() == 1)
            message = docs.iterator().next();
        assertEquals(message + " concepts", expected, concepts.size()); // Purchased docTypes
        return concepts;

    }

}
