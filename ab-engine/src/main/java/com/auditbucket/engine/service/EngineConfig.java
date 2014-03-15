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

package com.auditbucket.engine.service;

import com.auditbucket.dao.AuditDao;
import com.auditbucket.helper.VersionHelper;
import com.auditbucket.registration.model.Company;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Mike Holdsworth
 * Since: 29/08/13
 */
@Service
@Transactional
public class EngineConfig {

    @Autowired
    AuditDao auditDAO;

    private String abSearch;

    private String rabbitHost;

    private String rabbitPort;

    private Logger logger = LoggerFactory.getLogger(EngineConfig.class);

    private Boolean multiTenanted = false;
    private WhatService.KV_STORE kvStore =null;

    @Value("${rabbit.host:@null}")
    protected void setRabbitHost(String rabbitHost) {
        if ("@null".equals(rabbitHost)) this.rabbitHost = null;
        else this.rabbitHost = rabbitHost;

    }

    @Value("${rabbit.port:@null}")
    protected void setRabbitPort(String rabbitPort) {
        if ("@null".equals(rabbitPort)) this.rabbitPort = null;
        else this.rabbitPort = rabbitPort;

    }

    @Value("${absearch.make:@null}")
    protected void setAbSearch(String absearchMake) {
        if ("@null".equals(absearchMake)) this.abSearch = null;
        else this.abSearch = absearchMake;
    }

    @Value("${abengine.multiTenanted:@null}")
    protected void setMultiTenanted(String multiTenanted) {
        this.multiTenanted = !"@null".equals(multiTenanted) && Boolean.parseBoolean(multiTenanted);
    }

    @Value("${abengine.kvStore}")
    public void setKvStore (String kvStore){
        if ("@null".equals(kvStore) || kvStore.equalsIgnoreCase("redis"))
            this.kvStore = WhatService.KV_STORE.REDIS;
        else if ( kvStore.equalsIgnoreCase("riak"))
            this.kvStore = WhatService.KV_STORE.RIAK;
        else {
            logger.error("Unable to resolve the abengine.kvstore property [" + kvStore +"]. Defaulting to REDIS");
        }

    }

    public WhatService.KV_STORE getKvStore() {
        return kvStore;
    }

    @Autowired
    Neo4jTemplate template;

    @Async
    public void createTagIndex(Company company) {
        //template.query("create index on (t:Tag" + getTagSuffix(company) + ")", null) ;
        // Performance issue with constraints?
        logger.info("MultiTenant suffix = [" + getTagSuffix(company) + "]");
        template.query("create constraint on (t:Tag" + getTagSuffix(company) + ") assert t.key is unique", null);
        //template.query("create index on (t:Tag" + getTagSuffix(company)+ ")", null);
    }

    public String getTagSuffix(Company company) {
        if (company == null)
            return "";
        return (isMultiTenanted() ? company.getCode() : "");
    }

    public Map<String, String> getHealth() {
        String version = VersionHelper.getABVersion();
        Map<String, String> healthResults = new HashMap<>();
        healthResults.put("ab-engine.version", version);
        healthResults.put("ab-engine", auditDAO.ping());
        String config = System.getProperty("ab.config");
        if (config == null || config.equals(""))
            config = "system-default";
        healthResults.put("config-file", config);
        String integration = System.getProperty("ab.integration");
        healthResults.put("ab.integration", integration);
        healthResults.put("abengine.kvStore", String.valueOf(kvStore));

        //healthResults.put("ab.multiTenanted", multiTenanted.toString());
        if ("http".equalsIgnoreCase(integration)) {
            healthResults.put("absearch.make", abSearch);
        } else {
            healthResults.put("rabbitmq.host", rabbitHost);
            healthResults.put("rabbitmq.port", rabbitPort);
        }
        return healthResults;

    }


    private void doHealth() {
        ObjectMapper om = new ObjectMapper();
        try {
            ObjectWriter or = om.writerWithDefaultPrettyPrinter();
            logger.info("\r\n" + or.writeValueAsString(getHealth()));

        } catch (JsonProcessingException e) {

            logger.error("doHealth", e);
        }
    }

    public boolean isMultiTenanted() {
        return multiTenanted;
    }

    public void setMultiTenanted(boolean multiTenanted) {
        this.multiTenanted = multiTenanted;
    }
}