/*
 * Copyright (c) 2012-2013 "Monowai Developments Limited"
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

package com.auditbucket.test.functional;

/**
 * User: Mike Holdsworth
 * Date: 27/06/13
 * Time: 4:49 PM
 */

import com.auditbucket.audit.model.AuditHeader;
import com.auditbucket.audit.model.AuditTag;
import com.auditbucket.audit.model.DocumentType;
import com.auditbucket.bean.AuditHeaderInputBean;
import com.auditbucket.bean.AuditResultBean;
import com.auditbucket.bean.AuditSummaryBean;
import com.auditbucket.bean.AuditTagInputBean;
import com.auditbucket.engine.service.AuditManagerService;
import com.auditbucket.engine.service.AuditService;
import com.auditbucket.engine.service.AuditTagService;
import com.auditbucket.registration.bean.RegistrationBean;
import com.auditbucket.bean.TagInputBean;
import com.auditbucket.registration.model.Company;
import com.auditbucket.registration.model.Fortress;
import com.auditbucket.registration.model.SystemUser;
import com.auditbucket.registration.model.Tag;
import com.auditbucket.registration.service.FortressService;
import com.auditbucket.registration.service.RegistrationService;
import com.auditbucket.registration.service.TagService;
import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.data.neo4j.support.node.Neo4jHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

/**
 * User: Mike Holdsworth
 * Date: 29/06/13
 * Time: 8:11 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:root-context.xml")
@Transactional
public class TestAuditTags {
    @Autowired
    FortressService fortressService;

    @Autowired
    AuditManagerService auditManager;

    @Autowired
    AuditService auditService;

    @Autowired
    RegistrationService regService;

    @Autowired
    TagService tagService;

    @Autowired
    AuditTagService auditTagService;

    @Autowired
    private Neo4jTemplate template;

    //private Logger log = LoggerFactory.getLogger(TestAuditTags.class);

    private String company = "Monowai";
    private String uid = "mike@monowai.com";
    private Authentication authA = new UsernamePasswordAuthenticationToken(uid, "user1");

    @Rollback(false)
    @BeforeTransaction
    public void cleanUpGraph() {
        // This will fail if running over REST. Haven't figured out how to use a view to look at the embedded db
        // See: https://github.com/SpringSource/spring-data-neo4j/blob/master/spring-data-neo4j-examples/todos/src/main/resources/META-INF/spring/applicationContext-graph.xml
        SecurityContextHolder.getContext().setAuthentication(authA);
        if (!"http".equals(System.getProperty("neo4j")))
            Neo4jHelper.cleanDb(template);
    }

    @Autowired
    GraphDatabase graphDatabase;

    @Test
    public void tagAuditRecords() throws Exception {
        SystemUser iSystemUser = regService.registerSystemUser(new RegistrationBean(company, uid, "bah"));
        assertNotNull(iSystemUser);

        Fortress fortress = fortressService.registerFortress("ABC");
        assertNotNull(fortress);

        Company iCompany = iSystemUser.getCompany();

        TagInputBean flopTag = new TagInputBean("FLOP");

        tagService.processTag(flopTag);
        //assertNotNull(result);
        AuditHeaderInputBean inputBean = new AuditHeaderInputBean("ABC", "auditTest", "aTest", new DateTime(), "abc");
        AuditResultBean resultBean = auditManager.createHeader(inputBean);
        AuditHeader header = auditService.getHeader(resultBean.getAuditKey());

        AuditTagInputBean auditTag = new AuditTagInputBean(resultBean.getAuditKey(), null, "!!!");
        try {
            auditTagService.processTag(header, auditTag);
            fail("No null argument exception detected");
        } catch (IllegalArgumentException ie) {
            // This should have happened
        }
        // First auditTag created
        auditTag = new AuditTagInputBean(header.getAuditKey(), flopTag.getName(), "ABC");

        auditTagService.processTag(header, auditTag);

        Boolean tagRlxExists = auditTagService.relationshipExists(header, flopTag.getName(), "ABC");
        assertTrue("Tag not found " + flopTag.getName(), tagRlxExists);

        auditTagService.processTag(header, auditTag);
        // Behaviour - Can't add the same tagValue twice for the same combo
        tagRlxExists = auditTagService.relationshipExists(header, flopTag.getName(), "ABC");
        assertTrue(tagRlxExists);
    }

    @Test
    public void renameRelationship() throws Exception {

        SystemUser iSystemUser = regService.registerSystemUser(new RegistrationBean(company, uid, "bah"));
        fortressService.registerFortress("ABC");

        Company iCompany = iSystemUser.getCompany();
        TagInputBean tagInput = new TagInputBean("FLOP");

        tagService.processTag(tagInput);
        //assertNotNull(result);
        AuditHeaderInputBean aib = new AuditHeaderInputBean("ABC", "auditTest", "aTest", new DateTime(), "abc");
        Map<String, Object> tagValues = new HashMap<>();
        tagValues.put("TagA", "AAAA");
        tagValues.put("TagB", "BBBB");
        tagValues.put("TagC", "CCCC");
        tagValues.put("TagD", "DDDD");
        aib.setTagValues(tagValues);
        AuditResultBean resultBean = auditManager.createHeader(aib);
        AuditHeader auditHeader = auditService.getHeader(resultBean.getAuditKey());
        Set<AuditTag> tagSet = auditTagService.findAuditTags(auditHeader);

        assertNotNull(tagSet);
        assertEquals(4, tagSet.size());
        assertFalse(auditTagService.relationshipExists(auditHeader, "TagC", "!!Twee!!"));//
        // Remove a single tag
        for (AuditTag value : tagSet) {
            if (value.getTag().getName().equals("TagC"))
                auditTagService.changeType(auditHeader, value, "!!Twee!!");
        }

        assertTrue(auditTagService.relationshipExists(auditHeader, "TagC", "!!Twee!!"));
    }

    @Test
    public void createAndDeleteAuditTags() throws Exception {

        SystemUser iSystemUser = regService.registerSystemUser(new RegistrationBean(company, uid, "bah"));
        fortressService.registerFortress("ABC");

        Company iCompany = iSystemUser.getCompany();
        TagInputBean tagInput = new TagInputBean("FLOP");

        tagService.processTag(tagInput);
        //assertNotNull(result);
        AuditHeaderInputBean aib = new AuditHeaderInputBean("ABC", "auditTest", "aTest", new DateTime(), "abc");
        Map<String, Object> tagValues = new HashMap<>();
        tagValues.put("TagA", "AAAA");
        tagValues.put("TagB", "BBBB");
        tagValues.put("TagC", "CCCC");
        tagValues.put("TagD", "DDDD");
        aib.setTagValues(tagValues);
        AuditResultBean resultBean = auditManager.createHeader(aib);
        AuditHeader auditHeader = auditService.getHeader(resultBean.getAuditKey());
        Set<AuditTag> tagSet = auditTagService.findAuditTags(auditHeader);

        assertNotNull(tagSet);
        assertEquals(4, tagSet.size());
        // Remove a single tag
        for (AuditTag value : tagSet) {
            if (value.getTag().getName().equals("TagB"))
                auditTagService.deleteAuditTag(auditHeader, value);
        }
        tagSet = auditTagService.findAuditTags(auditHeader);
        assertNotNull(tagSet);
        assertEquals(3, tagSet.size());
        // Ensure that the deleted tag is not in the results
        for (AuditTag auditTag : tagSet) {
            assertFalse(auditTag.getTag().getName().equals("TagB"));
        }
    }

    @Test
    public void nullTagValueCRUD() throws Exception {
        SystemUser iSystemUser = regService.registerSystemUser(new RegistrationBean(company, uid, "bah"));
        fortressService.registerFortress("ABC");

        Company iCompany = iSystemUser.getCompany();
        TagInputBean tagInput = new TagInputBean("FLOP");

        tagService.processTag(tagInput);
        //assertNotNull(result);
        AuditHeaderInputBean aib = new AuditHeaderInputBean("ABC", "auditTest", "aTest", new DateTime(), "abc");
        Map<String, Object> tagValues = new HashMap<>();
        // In this scenario, the Tag name is the key if the value is null
        tagValues.put("TagA", null);
        tagValues.put("TagB", null);
        tagValues.put("TagC", null);
        tagValues.put("TagD", "DDDD");
        aib.setTagValues(tagValues);
        AuditResultBean resultBean = auditManager.createHeader(aib);
        AuditHeader auditHeader = auditService.getHeader(resultBean.getAuditKey());
        Set<AuditTag> tagSet = auditTagService.findAuditTags(auditHeader);
        assertNotNull(tagSet);
        assertEquals(4, tagSet.size());

        auditService.updateHeader(auditHeader);
        auditHeader = auditService.getHeader(auditHeader.getAuditKey());
        AuditSummaryBean summaryBean = auditService.getAuditSummary(auditHeader.getAuditKey());
        tagSet = summaryBean.getTags();
        assertNotNull(tagSet);
        Set<AuditHeader> headers = auditTagService.findTagAudits("TagA");
        assertNotNull(headers);
        assertNotSame(headers.size() + " Audit headers returned!", 0, headers.size());

        assertEquals(auditHeader.getAuditKey(), headers.iterator().next().getAuditKey());
        headers = auditTagService.findTagAudits("TagC");
        assertNotNull(headers);
        assertEquals(auditHeader.getAuditKey(), headers.iterator().next().getAuditKey());
        headers = auditTagService.findTagAudits("TagD");
        assertNotNull(headers);
        assertEquals(auditHeader.getAuditKey(), headers.iterator().next().getAuditKey());
    }

    @Test
    public void duplicateTagNotCreated() throws Exception {
        SystemUser iSystemUser = regService.registerSystemUser(new RegistrationBean(company, uid, "bah"));
        fortressService.registerFortress("ABC");

        Company iCompany = iSystemUser.getCompany();
        TagInputBean tagInput = new TagInputBean("FLOP");

        tagService.processTag(tagInput);
        //assertNotNull(result);
        AuditHeaderInputBean aib = new AuditHeaderInputBean("ABC", "auditTest", "aTest", new DateTime(), "abc");
        Map<String, Object> tagValues = new HashMap<>();
        // This should create the same Tag object
        tagValues.put("TagA", "camel");
        tagValues.put("taga", "lower");
        tagValues.put("tAgA", "mixed");
        aib.setTagValues(tagValues);
        AuditResultBean resultBean = auditManager.createHeader(aib);
        AuditHeader auditHeader = auditService.getHeader(resultBean.getAuditKey());
        Tag tag = tagService.findTag("Taga");
        assertNotNull(tag);
        Set<AuditTag> auditTags = auditTagService.findAuditTags(auditHeader);
        for (AuditTag auditTag : auditTags) {
            assertEquals("Expected same tag for each relationship", tag.getId(), auditTag.getTag().getId());
        }
        assertEquals("Expected 3 relationships for the same tag", 3, auditTags.size());

    }

    @Test
    public void differentTagTypeSameTagName() throws Exception {
        SystemUser iSystemUser = regService.registerSystemUser(new RegistrationBean(company, uid, "bah"));
        fortressService.registerFortress("ABC");

        Company iCompany = iSystemUser.getCompany();
        TagInputBean tagInput = new TagInputBean("FLOP");

        tagService.processTag(tagInput);
        //assertNotNull(result);
        AuditHeaderInputBean aib = new AuditHeaderInputBean("ABC", "auditTest", "aTest", new DateTime(), "abc");
        Map<String, Object> tagValues = new HashMap<>();
        // This should create the same Tag object
        Collection list = new ArrayList();
        list.add("Type1");
        list.add("Type2");
        list.add("Type3");

        tagValues.put("TagA", list);

        aib.setTagValues(tagValues);
        AuditResultBean resultBean = auditManager.createHeader(aib);
        AuditHeader auditHeader = auditService.getHeader(resultBean.getAuditKey());
        Set<AuditTag> tagSet = auditTagService.findAuditTags(auditHeader);
        assertNotNull(tagSet);
        assertEquals(3, tagSet.size());

        AuditSummaryBean summaryBean = auditManager.getAuditSummary(auditHeader.getAuditKey());
        assertNotNull(summaryBean);
        assertEquals(3, summaryBean.getTags().size());

    }

    public void documentTypesWork() {
        regService.registerSystemUser(new RegistrationBean(company, uid, "bah"));
        fortressService.registerFortress("ABC");

        String docName = "CamelCaseDoc";
        DocumentType docType = tagService.resolveDocType(docName);
        assertNotNull(docType);
        assertEquals(docName.toLowerCase(), docType.getCode());
        assertEquals(docName, docType.getName());
        // Should be finding by code which is always Lower
        DocumentType sameDoc = tagService.resolveDocType(docType.getCode().toUpperCase());
        Assert.assertNotNull(sameDoc);
        assertSame(sameDoc.getId(), docType.getId());

    }

    @Test
    public void tagListAndSingular() throws Exception {
        SystemUser iSystemUser = regService.registerSystemUser(new RegistrationBean(company, uid, "bah"));
        assertNotNull(iSystemUser);

        Fortress fortress = fortressService.registerFortress("ABC");
        assertNotNull(fortress);

        AuditHeaderInputBean inputBean = new AuditHeaderInputBean("ABC", "auditTest", "aTest", new DateTime(), "abc");
        Map<String, Object> tags = new HashMap<>();
        Collection<String> types = new ArrayList<>();
        types.add("email-to");
        types.add("email-cc");
        tags.put("mike@auditbucket.com", types);
        tags.put("np@auditbucket.com", "email-cc");
        inputBean.setTagValues(tags);
        AuditResultBean resultBean = auditManager.createHeader(inputBean);
        AuditHeader header = auditService.getHeader(resultBean.getAuditKey());
        Set<AuditTag> tagResults = auditTagService.findAuditTags(header);
        assertEquals("Union of type and tag does not total", 3, tagResults.size());
        AuditSummaryBean summaryBean = auditService.getAuditSummary(header.getAuditKey());
        assertEquals(3, summaryBean.getTags().size());
    }

    @Test
    public void mapRelationshipsWithNullProperties() throws Exception {
        SystemUser iSystemUser = regService.registerSystemUser(new RegistrationBean(company, uid, "bah"));
        assertNotNull(iSystemUser);

        Fortress fortress = fortressService.registerFortress("ABC");
        assertNotNull(fortress);

        AuditHeaderInputBean inputBean = new AuditHeaderInputBean("ABC", "auditTest", "aTest", new DateTime(), "abc");
        Map<String, Object> tags = new HashMap<>();
        Map<String, Map> types = new HashMap<>();
        types.put("email-to", null);
        types.put("email-cc", null);
        tags.put("mike@auditbucket.com", types);
        tags.put("np@auditbucket.com", "email-cc");
        inputBean.setTagValues(tags);
        AuditResultBean resultBean = auditManager.createHeader(inputBean);
        AuditHeader header = auditService.getHeader(resultBean.getAuditKey());
        Set<AuditTag> tagResults = auditTagService.findAuditTags(header);
        AuditSummaryBean summaryBean = auditService.getAuditSummary(header.getAuditKey());
        assertEquals("Union of type and tag does not total", 3, tagResults.size());
        assertEquals(3, summaryBean.getTags().size());
    }

    @Test
    public void mapRelationshipsWithProperties() throws Exception {
        SystemUser iSystemUser = regService.registerSystemUser(new RegistrationBean(company, uid, "bah"));
        assertNotNull(iSystemUser);

        Fortress fortress = fortressService.registerFortress("ABC");
        assertNotNull(fortress);

        AuditHeaderInputBean inputBean = new AuditHeaderInputBean("ABC", "auditTest", "aTest", new DateTime(), "abc");
        Map<String, Object> tags = new HashMap<>();
        Map<String, Map> relationships = new HashMap<>();
        Map<String, Object> propA = new HashMap<>();
        Map<String, Object> propB = new HashMap<>();
        propA.put("myValue", 10);
        propB.put("myValue", 20);

        relationships.put("email-to", propA);
        relationships.put("email-cc", propB);
        tags.put("mike@auditbucket.com", relationships);
        tags.put("np@auditbucket.com", "email-cc");
        inputBean.setTagValues(tags);
        AuditResultBean resultBean = auditManager.createHeader(inputBean);
        AuditHeader header = auditService.getHeader(resultBean.getAuditKey());
        Set<AuditTag> tagResults = auditTagService.findAuditTags(header);
        AuditSummaryBean summaryBean = auditService.getAuditSummary(header.getAuditKey());
        assertEquals("Union of type and tag does not total", 3, tagResults.size());
        assertEquals(3, summaryBean.getTags().size());
    }

    @Test
    public void duplicateRLXTypesNotStored() throws Exception {
        SystemUser iSystemUser = regService.registerSystemUser(new RegistrationBean(company, uid, "bah"));
        assertNotNull(iSystemUser);

        Fortress fortress = fortressService.registerFortress("ABC");
        assertNotNull(fortress);

        AuditHeaderInputBean inputBean = new AuditHeaderInputBean("ABC", "auditTest", "aTest", new DateTime(), "abc");
        Map<String, Object> tags = new HashMap<>();
        Collection<String> types = new ArrayList<>();
        types.add("email-to");
        types.add("email-to");
        types.add("email-to");
        tags.put("mike@auditbucket.com", types);
        inputBean.setTagValues(tags);
        AuditResultBean resultBean = auditManager.createHeader(inputBean);
        AuditHeader header = auditService.getHeader(resultBean.getAuditKey());
        Set<AuditTag> tagResults = auditTagService.findAuditTags(header);
        // ToDo In Neo4j2 remove the generic tag
        assertEquals("One for the Generic tag and one for exploration", 1, tagResults.size());
    }

    @Test
    public void tagsAndValuesWithSpaces() throws Exception {
        SystemUser iSystemUser = regService.registerSystemUser(new RegistrationBean(company, uid, "bah"));
        assertNotNull(iSystemUser);

        Fortress fortress = fortressService.registerFortress("ABC");
        assertNotNull(fortress);

        AuditHeaderInputBean inputBean = new AuditHeaderInputBean("ABC", "auditTest", "aTest", new DateTime(), "abc");
        Map<String, Object> tags = new HashMap<>();
        Collection<String> types = new ArrayList<>();
        types.add("email to");
        types.add("email cc");
        tags.put("mike auditbucket.com", types);
        tags.put("np auditbucket.com", "email cc");
        inputBean.setTagValues(tags);
        AuditResultBean resultBean = auditManager.createHeader(inputBean);
        AuditHeader header = auditService.getHeader(resultBean.getAuditKey());
        Set<AuditTag> tagResults = auditTagService.findAuditTags(header);
        assertEquals("Union of type and tag does not total", 3, tagResults.size());
        AuditSummaryBean summaryBean = auditService.getAuditSummary(header.getAuditKey());
        assertEquals(3, summaryBean.getTags().size());
    }

    @Test
    public void nestedStructureInHeader() throws Exception {
        SystemUser iSystemUser = regService.registerSystemUser(new RegistrationBean(company, uid, "bah"));
        assertNotNull(iSystemUser);

        Fortress fortress = fortressService.registerFortress("ABC");
        assertNotNull(fortress);

        AuditHeaderInputBean inputBean = new AuditHeaderInputBean("ABC", "auditTest", "aTest", new DateTime(), "abc");
        TagInputBean country = new TagInputBean("New Zealand");
        TagInputBean wellington = new TagInputBean("Wellington");
        TagInputBean auckland = new TagInputBean("Auckland");
        country.setAssociatedTag("capital-city", wellington);
        country.setAssociatedTag("city", auckland);
        TagInputBean section = new TagInputBean("Thorndon");
        wellington.setAssociatedTag("section", section);
        TagInputBean building = new TagInputBean("ABC House");
        section.setAssociatedTag("houses", building);

        inputBean.setAssociatedTag(country);
        AuditResultBean resultBean = auditManager.createHeader(inputBean);
        assertNotNull(resultBean);
        // Tags are not associated with the header rather the structure is enforced while importing
        Tag countryTag = tagService.findTag("New Zealand");
        Tag cityTag = tagService.findTag("Wellington");
        Tag sectionTag = tagService.findTag("Thorndon");
        Tag houseTag = tagService.findTag("ABC House");

        assertNotNull(countryTag);
        assertEquals(1, tagService.findDirectedTags(countryTag).size());
        assertNotNull(cityTag);
        assertEquals(2, tagService.findDirectedTags(cityTag).size());
        assertNotNull(sectionTag);
        assertEquals(1, tagService.findDirectedTags(sectionTag).size());
        assertNotNull(houseTag);
        assertEquals(0, tagService.findDirectedTags(houseTag).size());
    }

}
