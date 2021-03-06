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

package org.flockdata.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Collection;
import java.util.TreeSet;

/**
 * User: mike
 * Date: 16/06/14
 * Time: 10:16 AM
 */
@NodeEntity
@TypeAlias("Concept")
public class Concept  {

    @GraphId
    Long id;

    @Indexed(unique = true)
    private String name;

    //@Relationship(type="KNOWN_RELATIONSHIP", direction = org.neo4j.ogm.annotation.Relationship.OUTGOING)
    @RelatedTo(elementClass = Relationship.class, type="KNOWN_RELATIONSHIP", direction = Direction.OUTGOING)
    Collection<Relationship> relationships;

    protected Concept() {
    }

    public Concept(String name) {
        this();
        this.name = name;

    }

    public Concept(String indexName, String relationship, DocumentType docType) {
        this(indexName);
        addRelationship(relationship, docType);

    }


    public void addRelationship(String relationship, DocumentType docType) {
        if ( relationships == null )
            relationships = new TreeSet<>();

        Relationship node = new Relationship(relationship, docType);
        relationships.add(node);

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Collection<Relationship> getRelationships() {
        return relationships;
    }

    public void addRelationships(Collection<Relationship> tempRlx) {
        this.relationships = tempRlx;
    }

    public boolean hasRelationship(String relationship) {
        for (Relationship rlx: relationships) {
            if ( rlx.getName().equalsIgnoreCase(relationship))
                return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "DocumentType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Concept)) return false;

        Concept that = (Concept) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public Relationship hasRelationship(String relationshipName, DocumentType docType) {
        if ( relationships == null )
            return null;
        for (Relationship relationship : relationships) {
            if (relationship.getName().equalsIgnoreCase(relationshipName)){
                for ( DocumentType documentType: relationship.getDocumentTypes()) {
                    if ( documentType.getId().equals(docType.getId()))
                        return relationship;

                }

            }

        }
        return null;
    }
}