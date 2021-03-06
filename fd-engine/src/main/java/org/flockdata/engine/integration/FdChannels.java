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

package org.flockdata.engine.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.messaging.MessageChannel;


/**
 * Created by mike on 3/07/15.
 */
@Configuration
@IntegrationComponentScan
public class FdChannels {
    String neoUrl;
    String searchUrl;

    @Value("${neo4j.url:@null}")
    public void setNeoUrl(String neoUrl) {
        if ("@null".equals(neoUrl))
            this.neoUrl = "http://localhost:7474";
        else
            this.neoUrl = neoUrl;
    }

    @Value("${fd-search.url}")
    public void setFdSearchUrl(String searchUrl){
        this.searchUrl = searchUrl;
    }

    public String getSearchUrl() {
        return searchUrl;
    }

    public String getNeoUrl() {
        return neoUrl;
    }

    public String getUriRoot() {
        return getNeoUrl() + "/fd";
    }

    @Bean
    MessageChannel neoFdMakeTags() {
        return MessageChannels.direct("neoFdMakeTags").get();
    }

    @Bean
    MessageChannel neoFdFindTag() {
        return MessageChannels.direct("neoFdFindTag").get();
    }

    @Bean
    MessageChannel neoFdMakeAlias() {
        return MessageChannels.direct("neoFdMakeAlias").get();
    }

    @Bean
    MessageChannel neoFdMakeEntity() {
        return MessageChannels.direct("neoFdMakeEntity").get();
    }

    @Bean
    MessageChannel neoFdFindEntity() {
        return MessageChannels.direct("neoFdFindEntity").get();
    }

    @Bean
    MessageChannel neoFdFindLabeledEntities() {
        return MessageChannels.direct("neoFdFindLabeledEntities").get();
    }

    @Bean
    MessageChannel neoFdWriteLog() {
        return MessageChannels.direct("neoFdWriteLog").get();
    }

    @Bean
    MessageChannel neoFdGetEntityLog() {
        return MessageChannels.direct("neoFdGetEntityLog").get();
    }

    @Bean
    MessageChannel neoFdGetLastChange() {
        return MessageChannels.direct("neoFdGetLastChange").get();
    }

    @Bean
    MessageChannel neoFdGetEntityLogs() {
        return MessageChannels.direct("neoFdGetEntityLogs").get();
    }

    @Bean
    MessageChannel neoFdFindByCallerRef() {
        return MessageChannels.direct("neoFdFindByCallerRef").get();
    }

    @Bean
    MessageChannel neoFdLogsBeforeDate() {
        return MessageChannels.direct("neoFdLogsBeforeDate").get();
    }

    @Bean
    MessageChannel neoFdCancelLastLog() {
        return MessageChannels.direct("neoFdCancelLastLog").get();
    }

    @Bean
    MessageChannel neoFdAddEntityTag () {
        return MessageChannels.direct("neoFdAddEntityTag").get();
    }

    @Bean
    MessageChannel neoFdGetEntityTag () {
        return MessageChannels.direct("neoFdGetEntityTag").get();
    }

    @Bean
    MessageChannel neoFdGetEntityTags () {
        return MessageChannels.direct("neoFdGetEntityTags").get();
    }

}
