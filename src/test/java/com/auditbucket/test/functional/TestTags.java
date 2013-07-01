package com.auditbucket.test.functional;

import com.auditbucket.registration.bean.RegistrationBean;
import com.auditbucket.registration.bean.TagInputBean;
import com.auditbucket.registration.model.ICompany;
import com.auditbucket.registration.model.ISystemUser;
import com.auditbucket.registration.model.ITag;
import com.auditbucket.registration.service.FortressService;
import com.auditbucket.registration.service.RegistrationService;
import com.auditbucket.registration.service.TagService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import static junit.framework.Assert.*;

/**
 * User: mike
 * Date: 26/06/13
 * Time: 8:11 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:root-context.xml")
@Transactional

public class TestTags {
    @Autowired
    FortressService fortressService;

    @Autowired
    RegistrationService regService;

    @Autowired
    TagService tagService;


    @Autowired
    private Neo4jTemplate template;
    private Logger log = LoggerFactory.getLogger(TestTags.class);
    private String company = "Monowai";
    private String uid = "mike@monowai.com";
    Authentication authA = new UsernamePasswordAuthenticationToken(uid, "user1");

    @Rollback(false)
    @BeforeTransaction
    public void cleanUpGraph() {
        // This will fail if running over REST. Haven't figured out how to use a view to look at the embedded db
        // See: https://github.com/SpringSource/spring-data-neo4j/blob/master/spring-data-neo4j-examples/todos/src/main/resources/META-INF/spring/applicationContext-graph.xml
        SecurityContextHolder.getContext().setAuthentication(authA);
        Neo4jHelper.cleanDb(template);
    }

    @org.junit.Test
    public void tagCreationAndSecurity() throws Exception {
        ISystemUser iSystemUser = regService.registerSystemUser(new RegistrationBean(company, uid, "bah"));
        assertNotNull(iSystemUser);

        ICompany iCompany = iSystemUser.getCompany();

        ITag tagInput = new TagInputBean(iCompany, "FLOP");
        tagInput = tagService.processTag(tagInput);
        assertNotNull(tagInput);
        assertNotNull(tagInput.getCompany());
        assertNull(tagService.findTag("ABC"));
        assertNotNull(tagService.findTag("FLOP"));

        iSystemUser = regService.registerSystemUser(new RegistrationBean("ABC", "gina", "bah"));
        Authentication authGina = new UsernamePasswordAuthenticationToken("gina", "user1");
        SecurityContextHolder.getContext().setAuthentication(authGina);
        assertNull(tagService.findTag("FLOP")); // Can't see the Monowai company tag

        tagInput = new TagInputBean(iSystemUser.getCompany(), "FLOP");
        tagInput = tagService.processTag(tagInput);
        assertNotNull(tagInput);
        assertNotNull(tagInput.getCompany());
        assertNull(tagService.findTag("ABC"));
        assertNotNull(tagService.findTag("FLOP"));
    }

    @Test
    public void tagUpdate() throws Exception {
        ISystemUser iSystemUser = regService.registerSystemUser(new RegistrationBean(company, uid, "bah"));
        assertNotNull(iSystemUser);

        ICompany iCompany = iSystemUser.getCompany();

        ITag tagInput = new TagInputBean(iCompany, "FLOP");
        tagInput = tagService.processTag(tagInput);
        assertNotNull(tagInput);
        assertNotNull(tagInput.getCompany());
        assertNull(tagService.findTag("ABC"));
        ITag result = tagService.findTag("FLOP");
        result.setName("FLOPPY");
        result = tagService.processTag(result);
        assertEquals("FLOPPY", result.getName());
        assertNull(tagService.findTag("FLOP"));
        assertNotNull(tagService.findTag("FLOPPY"));

    }
}