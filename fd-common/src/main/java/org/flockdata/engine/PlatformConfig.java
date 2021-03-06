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

package org.flockdata.engine;

import org.flockdata.kv.service.KvService;
import org.flockdata.model.Company;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 * User: mike
 * Date: 14/11/14
 * Time: 1:22 PM
 */
public interface PlatformConfig {

    String getTagSuffix(Company company);

    Map<String, String> getHealth();

    Map<String, String> getHealthAuth();

    boolean isMultiTenanted();

    void setMultiTenanted(boolean multiTenanted);

    void resetCache();

    @Value("${fd-engine.system.constraints:@null}")
    void setSystemConstraints(String constraints);

    KvService.KV_STORE setKvStore(KvService.KV_STORE kvStore);

    KvService.KV_STORE getKvStore();

    boolean isConceptsEnabled();

    void setConceptsEnabled(String conceptsEnabled);

    void setDuplicateRegistration(boolean duplicateRegistration);

    boolean isDuplicateRegistration();

    void setTestMode(boolean testMode);

    boolean isTestMode();

    String authPing();

    boolean createSystemConstraints();

    Boolean isStoreEnabled();

    Boolean isSearchEnabled();

    void setSearchEnabled(String enabled);

    void setStoreEnabled(String enabled);

    boolean isTiming();
}
