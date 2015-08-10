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

package org.flockdata.test.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import org.flockdata.helper.FlockException;
import org.flockdata.profile.ImportProfile;
import org.flockdata.registration.bean.SystemUserResultBean;
import org.flockdata.registration.bean.TagInputBean;
import org.flockdata.model.Company;
import org.flockdata.track.bean.CrossReferenceInputBean;
import org.flockdata.track.bean.EntityInputBean;
import org.flockdata.transform.ClientConfiguration;
import org.flockdata.transform.FdWriter;
import org.flockdata.transform.FileProcessor;
import org.flockdata.transform.TrackBatcher;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by mike on 28/01/15.
 */
public class TestImporterPreparsing extends AbstractImport {
    @Test
    public void string_PreParseRow() throws Exception {
        FileProcessor fileProcessor = new FileProcessor();
        ClientConfiguration configuration = getClientConfiguration("/pre-parse.json");
        configuration.setDefaultUser("test");

        ImportProfile params = ClientConfiguration.getImportParams("/pre-parse.json");
        assertEquals(',', params.getDelimiter());
        assertEquals(false, params.hasHeader());
        long rows = fileProcessor.processFile(params, "/properties-rlx.txt", fdWriter, null, configuration);
        assertEquals(4, rows);

    }

    FdWriter fdWriter = new FdWriter() {
        @Override
        public SystemUserResultBean me() {
            return null;
        }

        @Override
        public String flushTags(List<TagInputBean> tagInputBeans) throws FlockException {

            // Check that the payload will serialize
            ObjectMapper om = new ObjectMapper();
            try {
                om.writeValueAsString(tagInputBeans);
            } catch (Exception e) {
                throw new FlockException("Failed to serialize");
            }
            return null;
        }

        @Override
        public String flushEntities(Company company, List<EntityInputBean> entityBatch, ClientConfiguration configuration) throws FlockException {
            // Check that the payload will serialize
            assertEquals(4, entityBatch.size());
            for (EntityInputBean entityInputBean : entityBatch) {
                assertFalse("Expression not parsed for callerRef",entityInputBean.getCallerRef().contains("|"));
                TestCase.assertTrue("Tag not set", entityInputBean.getTags().size() == 3);
                TagInputBean politician= null;
                for (TagInputBean tagInputBean : entityInputBean.getTags()) {
                    assertFalse("Expression not parsed for code", tagInputBean.getCode().contains("|"));
                    if ( tagInputBean.getLabel().equals("Politician"))
                        politician= tagInputBean;
                    if ( tagInputBean.getLabel().equals("InterestGroup")){
                        assertEquals("direct", tagInputBean.getEntityLinks().keySet().iterator().next());
                    }
                }
                assertNotNull(politician);
                HashMap link = (HashMap) politician.getEntityLinks().get("receives");
                assertNotNull(link);
                assertNotNull(link.get("amount"));
                TestCase.assertTrue("Amount not calculated as a value", Integer.parseInt(link.get("amount").toString()) > 0);

            }
            ObjectMapper om = new ObjectMapper();
            try {
                om.writeValueAsString(entityBatch);
            } catch (Exception e) {
                throw new FlockException("Failed to serialize");
            }
            return null;

        }

        @Override
        public int flushXReferences(List<CrossReferenceInputBean> referenceInputBeans) throws FlockException {
            return 0;
        }

        @Override
        public boolean isSimulateOnly() {
            // Setting this to true will mean that the flush routines above are not called
            return false;
        }

        @Override
        public void close(TrackBatcher trackBatcher) {

        }
    };

}
