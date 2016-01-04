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

import org.flockdata.model.Fortress;
import org.flockdata.model.SystemUser;
import org.flockdata.registration.bean.FortressInputBean;
import org.flockdata.test.engine.Helper;
import org.flockdata.track.bean.ContentInputBean;
import org.flockdata.track.bean.EntityInputBean;
import org.flockdata.track.bean.TrackRequestResult;
import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by mike on 20/04/15.
 */
public class TestEntitySearch extends EngineBase {
    private Logger logger = LoggerFactory.getLogger(TestEntityTrack.class);

    @org.junit.Before
    public void setup(){
        engineConfig.setDuplicateRegistration(true);
    }

    @Test
    public void count_SearchDocsFromTrackRequestForNew() throws Exception {
        // DAT-387
        logger.debug("### count_SearchDocsFromTrackRequest");
        String callerRef = "mk1hz";
        SystemUser su = registerSystemUser("created_UserAgainstEntityAndLog");

        Fortress fortWP = fortressService.registerFortress(su.getCompany(), new FortressInputBean("count_SearchDocsFromTrackRequest", true).setStoreActive(false).setStoreActive(false));

        EntityInputBean beanA = getContentBean(fortWP.getName(), "poppy", "CompanyNode", "2012",
                new ContentInputBean("billie", null, DateTime.now(), Helper.getSimpleMap("name", "a"), "Answer"));

        EntityInputBean beanB = getContentBean(fortWP.getName(), "poppy", "CompanyNode", "2013",
                new ContentInputBean("billie", null, DateTime.now(), Helper.getSimpleMap("name", "a"), "Answer"));

        ArrayList<EntityInputBean> beans = new ArrayList<>();
        beans.add(beanA);
        beans.add(beanB);
        Collection<TrackRequestResult> results = trackRequests.trackEntities(beans, su.getApiKey());
        assertEquals(2, results.size());

    }

    @Test
    public void count_SearchDocsFromTrackRequestNewAndExisting() throws Exception {
        // DAT-387
        logger.debug("### count_SearchDocsFromTrackRequest");

        SystemUser su = registerSystemUser("created_UserAgainstEntityAndLog");

        Fortress fortWP = fortressService.registerFortress(su.getCompany(), new FortressInputBean("count_SearchDocsFromTrackRequest", true).setStoreActive(false).setStoreActive(false));

        EntityInputBean beanA = getContentBean(fortWP.getName(), "poppy", "CompanyNode", "2012",
                new ContentInputBean("billie", null, DateTime.now(), Helper.getSimpleMap("name", "a"), "Answer"));

        mediationFacade.trackEntity(su.getCompany(), beanA); // Handle beanA as an existing entity
        // Now add a new one
        EntityInputBean beanB = getContentBean(fortWP.getName(), "poppy", "CompanyNode", "2013",
                new ContentInputBean("billie", null, DateTime.now(), Helper.getSimpleMap("name", "a"), "Answer"));


        ArrayList<EntityInputBean> beans = new ArrayList<>();
        beans.add(beanA);
        beans.add(beanB);
        Collection<TrackRequestResult> results = trackRequests.trackEntities(beans, su.getApiKey());
        assertEquals(2, results.size());

        results = trackRequests.trackEntities(beans, su.getApiKey());
        assertEquals("Should be one new and one existing TrackResult returned", 2, results.size());


    }


    private EntityInputBean getContentBean(String fortressName, String fortUserName, String companyName, String callerRef, ContentInputBean contentInputBean) {
        EntityInputBean entityBean = new EntityInputBean(fortressName, fortUserName, companyName, DateTime.now(), callerRef);
        entityBean.setContent(contentInputBean);
        return entityBean;
    }

}
