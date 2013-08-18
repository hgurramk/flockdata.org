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

package com.auditbucket.registration.repo.neo4j.model;

import com.auditbucket.registration.model.Fortress;
import com.auditbucket.registration.model.FortressUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.*;

@NodeEntity
public class FortressUserNode implements FortressUser {
    @GraphId
    Long id;

    @RelatedTo(elementClass = FortressNode.class, type = "fortressUser", direction = Direction.INCOMING)
    private Fortress fortress;

    @Indexed(indexName = "fortressUser")
    private String name = null;

    protected FortressUserNode() {
    }

    public FortressUserNode(Fortress fortress, String fortressUserName) {
        setName(fortressUserName);
        setFortress(fortress);
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.toLowerCase();
    }

    @JsonIgnore
    public Fortress getFortress() {
        return fortress;
    }

    public void setFortress(Fortress fortress) {
        this.fortress = fortress;
    }

    @Override
    public String toString() {
        return "FortressUserNode{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }


}
