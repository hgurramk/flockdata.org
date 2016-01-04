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

package org.flockdata.test.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flockdata.helper.FdJsonObjectMapper;
import org.flockdata.kv.FdKvConfig;
import org.flockdata.kv.bean.KvContentBean;
import org.flockdata.kv.service.KvService;
import org.flockdata.model.*;
import org.flockdata.kv.KvContent;
import org.flockdata.registration.bean.FortressInputBean;
import org.flockdata.test.engine.Helper;
import org.flockdata.track.bean.ContentInputBean;
import org.flockdata.track.bean.EntityInputBean;
import org.flockdata.track.bean.TrackResultBean;
import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import redis.embedded.RedisServer;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.util.AssertionErrors.fail;

//import redis.embedded.RedisServer;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/store/fdkv-root-context.xml",
})

public class KvServiceTest {


    @Autowired
    private FdKvConfig kvConfig;

    private Logger logger = LoggerFactory.getLogger(KvServiceTest.class);

    private static RedisServer redisServer;

    @Autowired
    private KvService kvService;

    @Before
    public void resetKvStore() {
        kvConfig.setStoreEnabled("true");
        kvConfig.setKvStore(KvService.KV_STORE.MEMORY);
    }

    @BeforeClass
    public static void setup() throws Exception {

        if (redisServer == null) {
            // If you are on Windows
            if (System.getProperty("os.arch").equals("amd64") && System.getProperty("os.name").startsWith("Windows")) {
                URL url = KvServiceTest.class.getResource("/redis/redis-server.exe");
                File redisServerExe = new File(url.getFile());
                redisServer = new RedisServer(redisServerExe, 6379); // or new RedisServer("/path/to/your/redis", 6379);
            } else {
                redisServer = new RedisServer(6379);
            }
            redisServer.start();
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (redisServer != null && redisServer.isActive())
            redisServer.stop();
    }

    @Test
    public void defaults_StoreEnabled() throws Exception {
        assertEquals(KvService.KV_STORE.MEMORY, kvConfig.getKvStore());

    }

    @Test
    public void riak_JsonTest() throws Exception {
        kvConfig.setKvStore(KvService.KV_STORE.RIAK);
        kvMapTest();
        kvConfig.setKvStore(KvService.KV_STORE.MEMORY);
    }

    @Test
    public void redis_JsonTest() throws Exception {
        kvConfig.setKvStore(KvService.KV_STORE.REDIS);
        kvMapTest();
        kvConfig.setKvStore(KvService.KV_STORE.MEMORY);
    }

    @Test
    public void memory_JsonTest() throws Exception {
        kvConfig.setKvStore(KvService.KV_STORE.MEMORY);
        kvMapTest();
    }


    @Test
    public void redis_AttachmentTest() throws Exception {
        kvConfig.setKvStore(KvService.KV_STORE.REDIS);
        kvAttachmentTest();
        kvConfig.setKvStore(KvService.KV_STORE.MEMORY);
    }


    private void kvMapTest() throws Exception {
        logger.debug("Registering system user!");
        //SystemUser su = registerSystemUser("Company", EngineBase.mike_admin);
        //Fortress fortressA = fortressService.registerFortress(su.getCompany(), new FortressInputBean("Entity Test", true));
        String fortress = "Entity Test";
        String docType = "TestAuditX";
        String callerRef = "ABC123R";
        String company = "company";

        Map<String, Object> what = getWhatMap();

        // Represents identifiable entity information
        EntityInputBean entityInputBean = new EntityInputBean(fortress, "wally", docType, new DateTime(), callerRef);

        // The "What" content
        entityInputBean.setContent(new ContentInputBean(what));

        // Emulate the creation of the entity
        Entity entity = Helper.getEntity(company, fortress, "wally", docType);

        // Wrap the entity in a Track Result
        // TrackResultBean represents the general accumulated payload
        TrackResultBean trackResultBean = new TrackResultBean(
                new Fortress(
                        new FortressInputBean("test", true),
                        new Company("MyName")),
                entity, entityInputBean);

        // Create a log with a random primary key
        Log graphLog = new Log(entity);

        // Sets some tracking properties in to the Log and wraps the ContentInputBean in a KV wrapping class
        // This occurs before the service persists the log
        graphLog = kvService.prepareLog(trackResultBean, graphLog);
        // Graph tracks which KVService is storing this content
        EntityLog eLog = new EntityLog(entity, graphLog, new DateTime());

        // Wrap the log result in to the TrackResult
        trackResultBean.setCurrentLog(eLog);

        KvContentBean kvContentBean = new KvContentBean(trackResultBean);
        kvContentBean.setStorage(graphLog.getStorage());
        // RIAK requires a bucket. Other KV stores do not.
        assertNotNull(kvContentBean.getBucket());

        // Finally! the actual write occurs
        try {
            kvService.doWrite(entity, kvContentBean);

            // Retrieve the content we just created
            KvContent kvContent = kvService.getContent(entity, trackResultBean.getCurrentLog().getLog());
            assertNotNull(kvContent);
            assertNotNull(kvContent.getContent().getMetaKey());
            assertNotNull(kvContent.getContent().getCallerRef());

            validateWhat(what, kvContent);
            // Testing that cancel works
            kvService.delete(entity, trackResultBean.getCurrentLog().getLog());

        } catch (AmqpRejectAndDontRequeueException e) {
            // ToDo: Mock RIAK
            if (kvConfig.getKvStore().equals(KvService.KV_STORE.RIAK)) {
                logger.error("Silently passing. No what data to process for {}. KV store is not running", kvConfig.getKvStore());
            } else {
                logger.error("KV Error", e);
                fail("Unexpected KV error");
            }

        }
    }

    private void validateWhat(Map<String, Object> what, KvContent kvContent) throws InterruptedException {
        Thread.sleep(1500);
        assertEquals(what.get("sval"), kvContent.getWhat().get("sval"));
        assertEquals(what.get("lval"), kvContent.getWhat().get("lval"));
        assertEquals(what.get("dval"), kvContent.getWhat().get("dval"));
        assertEquals(what.get("ival"), kvContent.getWhat().get("ival"));
        assertEquals(what.get("bval"), kvContent.getWhat().get("bval"));
        String json = "{\"Athlete\":\"Katerina Neumannová\",\"Age\":\"28\",\"Country\":\"Czech Republic\",\"Year\":\"2002\",\"Closing Ceremony Date\":\"2/24/02\",\"Sport\":\"Cross Country Skiing\",\"Gold Medals\":\"0\",\"Silver Medals\":\"2\",\"Bronze Medals\":\"0\",\"Total Medals\":\"2\"}";
        assertEquals(json, kvContent.getWhat().get("utf-8"));
    }


    public static String getRandomJson() throws JsonProcessingException {
        return getRandomJson(null);
    }

    public static String getRandomJson(String s) throws JsonProcessingException {
        return getJsonFromObject(getWhatMap(s));
    }

    private static Map<String, Object> getWhatMap(String s) {
        Map<String, Object> o = getWhatMap();
        if (s == null)
            return o;

        o.put("random", s);
        return o;
    }

    public static String getJsonFromObject(Map<String, Object> what) throws JsonProcessingException {
        ObjectMapper mapper = FdJsonObjectMapper.getObjectMapper();
        return mapper.writeValueAsString(what);
    }

    public static Map<String, Object> getWhatMap() {
        Map<String, Object> what = new HashMap<>();
        what.put("lval", 123456789012345l);
        what.put("dval", 1234012345.990012d);
        // Duplicated to force compression
        what.put("sval", "Now is the time for all good men to come to the aid of the party.Now is the time for all good men to come to the aid of the party.Now is the time for all good men to come to the aid of the party.Now is the time for all good men to come to the aid of the party.Now is the time for all good men to come to the aid of the party.Now is the time for all good men to come to the aid of the party");
        what.put("sval2", "Now is the time for all good men to come to the aid of the party.Now is the time for all good men to come to the aid of the party.Now is the time for all good men to come to the aid of the party.Now is the time for all good men to come to the aid of the party.Now is the time for all good men to come to the aid of the party.Now is the time for all good men to come to the aid of the party");
        what.put("sval3", "Now is the time for all good men to come to the aid of the party.Now is the time for all good men to come to the aid of the party.Now is the time for all good men to come to the aid of the party.Now is the time for all good men to come to the aid of the party.Now is the time for all good men to come to the aid of the party.Now is the time for all good men to come to the aid of the party");
        what.put("sval4", "Now is the time for all good men to come to the aid of the party.Now is the time for all good men to come to the aid of the party.Now is the time for all good men to come to the aid of the party.Now is the time for all good men to come to the aid of the party.Now is the time for all good men to come to the aid of the party.Now is the time for all good men to come to the aid of the party");
        what.put("ival", 12345);
        what.put("bval", Boolean.TRUE);
        what.put("utf-8", "{\"Athlete\":\"Katerina Neumannová\",\"Age\":\"28\",\"Country\":\"Czech Republic\",\"Year\":\"2002\",\"Closing Ceremony Date\":\"2/24/02\",\"Sport\":\"Cross Country Skiing\",\"Gold Medals\":\"0\",\"Silver Medals\":\"2\",\"Bronze Medals\":\"0\",\"Total Medals\":\"2\"}");
        return what;
    }

    private void kvAttachmentTest() throws Exception {
        logger.debug("Registering system user!");

        String docType = "KvTest";
        String callerRef = "ABC123R";
        Entity entity = Helper.getEntity("myco", "myfort", "myuser", docType);

        EntityInputBean inputBean = Helper.getEntityInputBean(docType, "myfort", "myuser", callerRef, DateTime.now());
        ContentInputBean contentInputBean = new ContentInputBean("wally", new DateTime());
        contentInputBean.setAttachment("test-attachment-data", "PDF", "testFile.txt");
        //inputBean.setContent(contentInputBean);

        try {
            TrackResultBean tr = new TrackResultBean(null, entity, inputBean);
            KvContentBean kvContentBean = new KvContentBean(tr);
            kvService.doWrite(tr.getEntity(), kvContentBean);
            EntityLog entityLog = tr.getCurrentLog();
            KvContent entityContent = kvService.getContent(entity, entityLog.getLog());

            assertNotNull(entityContent);
            // Redis should always be available. RIAK is trickier to install

            assertEquals(contentInputBean.getFileName(), entityLog.getLog().getFileName());
            assertEquals("Value didn't convert to lowercase", "pdf", entityLog.getLog().getContentType());
            assertEquals(contentInputBean.getAttachment(), entityContent.getAttachment());
        } catch (Exception ies) {
            logger.error("KV Stores are configured in config.properties. This test is failing to find the {} server. Is it even installed?", kvConfig.getKvStore());
        }
    }


}

