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

import org.flockdata.client.Configure;
import org.flockdata.helper.FlockException;
import org.flockdata.profile.ImportProfile;
import org.flockdata.registration.bean.SystemUserResultBean;
import org.flockdata.registration.bean.TagInputBean;
import org.flockdata.registration.model.Company;
import org.flockdata.registration.model.Tag;
import org.flockdata.track.bean.CrossReferenceInputBean;
import org.flockdata.track.bean.EntityInputBean;
import org.flockdata.transform.ClientConfiguration;
import org.flockdata.transform.FdReader;
import org.flockdata.transform.FdWriter;
import org.flockdata.transform.FileProcessor;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

/**
 * Parameter import checks for ImportProfile
 *
 * Created by mike on 28/01/15.
 */
public class TestImportProfileValidation {
    @Test
    public void valid_Properties() throws Exception {
        FileProcessor fileProcessor = new FileProcessor(reader);
        File file = new File("/properties-rlx.json");
        ClientConfiguration configuration = Configure.readConfiguration(file);
        assertNotNull(configuration);
        configuration.setDefaultUser("test");

        ImportProfile params = ClientConfiguration.getImportParams("/properties-rlx.json");
        assertEquals(',', params.getDelimiter());
        assertEquals(false, params.hasHeader());
        params.setFortressName(null);
        try {
            fileProcessor.processFile(params, "/properties-rlx.txt", 0, fdWriter, null, configuration);
            fail("No fortress name found. We should not have gotten here");
        } catch (FlockException e){
            assertTrue(e.getMessage().contains("fortressName attribute."));
        }
        // Should also fail with blank
        params.setFortressName("");
        try {
            fileProcessor.processFile(params, "/properties-rlx.txt", 0, fdWriter, null, configuration);
            fail("No fortress name found. We should not have gotten here");
        } catch (FlockException e){
            assertTrue(e.getMessage().contains("fortressName attribute."));
        }

        params.setFortressName("abc");

        params.setDocumentName(null);
        try {
            fileProcessor.processFile(params, "/properties-rlx.txt", 0, fdWriter, null, configuration);
            fail("No document name found. We should not have gotten here");
        } catch (FlockException e){
            assertTrue(e.getMessage().contains("documentName attribute."));
        }

        params.setDocumentName("");
        try {
            fileProcessor.processFile(params, "/properties-rlx.txt", 0, fdWriter, null, configuration);
            fail("No document name found. We should not have gotten here");
        } catch (FlockException e){
            assertTrue(e.getMessage().contains("documentName attribute."));
        }

    }

    FdWriter fdWriter = new FdWriter() {
        @Override
        public SystemUserResultBean me() {
            return null;
        }

        @Override
        public String flushTags(List<TagInputBean> tagInputBeans) throws FlockException {
            throw new FlockException("Should never get here due to invalid parameters");
        }

        @Override
        public String flushEntities(Company company, List<EntityInputBean> entityBatch, ClientConfiguration configuration) throws FlockException {
            throw new FlockException("Should never get here due to invalid parameters");
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
        public Collection<Tag> getCountries() throws FlockException {
            return null;
        }

        @Override
        public void close() {

        }
    };

    FdReader reader = new FdReader() {
        @Override
        public String resolveCountryISOFromName(String name) throws FlockException {
            return name;
        }

        @Override
        public String resolve(String type, Map<String, Object> args) {
            return null;
        }
    };
}