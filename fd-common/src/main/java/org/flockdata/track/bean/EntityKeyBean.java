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

package org.flockdata.track.bean;

import org.flockdata.model.DocumentType;
import org.flockdata.model.Entity;
import org.flockdata.model.EntityTag;
import org.flockdata.search.IndexHelper;
import org.flockdata.search.model.SearchTag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: mike
 * Date: 9/07/14
 * Time: 5:01 PM
 */
public class EntityKeyBean {
    private String fortressName;
    private String documentType;
    private String company;
    private String metaKey;
    private String code;
    private String name;

    private String description;

    private String index;

    private  HashMap<String, Map<String, ArrayList<SearchTag>>>  searchTags = new HashMap<>();

    private String relationship; // Entity to entity relationship


    private EntityKeyBean(){}

    public EntityKeyBean (DocumentType documentType, String code){
        this.fortressName = documentType.getFortress().getName();
        this.company = documentType.getFortress().getCompany().getCode();
        this.documentType = documentType.getName();
        this.code = code;

    }

    public EntityKeyBean(String fortressName, String documentType, String code){
        this.fortressName = fortressName;
        this.documentType = documentType;
        this.code = code;
    }

    public EntityKeyBean(String code) {
        this.code = code;
    }

    public EntityKeyBean(EntityLinkInputBean crossReferenceInputBean) {
        this.fortressName = crossReferenceInputBean.getFortress();
        this.documentType = crossReferenceInputBean.getDocumentType();
        this.code = crossReferenceInputBean.getCallerRef();
    }

    public EntityKeyBean(Entity entity) {
        this();
        this.fortressName = entity.getSegment().getFortress().getName();
        this.code = entity.getCode();
        this.documentType = entity.getType();
        this.metaKey = entity.getMetaKey();
        this.index = IndexHelper.parseIndex(entity);
        this.description = entity.getDescription();
        this.name = entity.getName();
    }

    public EntityKeyBean(Entity entity, Collection<EntityTag> entityTags) {
        this(entity);
        for (EntityTag entityTag : entityTags) {
            Map<String, ArrayList<SearchTag>> byRelationship = searchTags.get(entityTag.getRelationship());
            if ( byRelationship == null){
                byRelationship = new HashMap<>();
                String rlx = entityTag.getRelationship();

                if (rlx == null ){
                    rlx = "default";
                }
                searchTags.put(rlx.toLowerCase(), byRelationship);
            }
            ArrayList<SearchTag> tags = byRelationship.get(entityTag.getTag().getLabel());
            if ( tags == null ){
                tags = new ArrayList<>();
                byRelationship.put(entityTag.getTag().getLabel(), tags);
            }
            tags.add(new SearchTag(entityTag)) ;
        }
    }

    public String getFortressName() {
        return fortressName;
    }

    public String getDocumentType() {
        if ( documentType == null || documentType.equals(""))
            return "*";
        return documentType;
    }

    public String getCode() {
        return code;
    }

    public String getCompany() {
        return company;
    }

    public String getIndex() {
        return index;
    }

    public HashMap<String, Map<String, ArrayList<SearchTag>>> getSearchTags() {
        return searchTags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityKeyBean)) return false;

        EntityKeyBean entityKey = (EntityKeyBean) o;

        if (!code.equals(entityKey.code)) return false;
        if (documentType != null ? !documentType.equals(entityKey.documentType) : entityKey.documentType != null)
            return false;
        return !(fortressName != null ? !fortressName.equals(entityKey.fortressName) : entityKey.fortressName != null);

    }

    public String getMetaKey() {
        return metaKey;
    }

    @Override
    public int hashCode() {
        int result = fortressName != null ? fortressName.hashCode() : 0;
        result = 31 * result + (documentType != null ? documentType.hashCode() : 0);
        result = 31 * result + code.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "EntityKey {" +
                "fortressName='" + fortressName + '\'' +
                ", documentType='" + documentType + '\'' +
                ", code='" + code + '\'' +
                '}';
    }

    public String getRelationship() {
        return relationship;
    }

    public EntityKeyBean addRelationship(String relationship) {
        this.relationship = relationship;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}
