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

package org.flockdata.registration.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.flockdata.helper.TagHelper;
import org.flockdata.model.Tag;
import org.flockdata.track.bean.AliasResultBean;
import org.flockdata.model.Alias;
import org.neo4j.graphdb.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Result after creating a tag
 *
 * Created by mike on 11/05/15.
 */
public class TagResultBean {
    String code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String name;
    String key;
    String label;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String message;

    Boolean newTag = false;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    ArrayList<AliasResultBean> aliases = new ArrayList<>();
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    Map<String,Object> properties = new HashMap<>();
    @JsonIgnore
    private Tag tag =null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String relationship;

    public TagResultBean(){}

    public TagResultBean(TagInputBean tagInput, Tag startTag, boolean isNew) {
        this(tagInput, startTag);
        this.newTag = isNew;
    }

    public TagResultBean(TagInputBean tagInputBean, Tag tag){

        this(tag);
        if ( tag == null ){
            this.code = tagInputBean.getCode();
            this.name = tagInputBean.getName();
        }
        if ( tagInputBean != null )
            this.message = tagInputBean.setServiceMessage();

    }


    public TagResultBean (Tag tag ) {
        this();
        this.tag = tag;

        if (tag != null) {
            this.newTag = tag.isNew();
            this.code = tag.getCode();
            this.key = tag.getKey();
            this.name = tag.getName();
            if (code.equals(name))
                name = null;
            this.properties = tag.getProperties();

            for (Alias alias : tag.getAliases()) {
                aliases.add(new AliasResultBean(alias));
            }
        }
    }

    public TagResultBean(TagInputBean tagInput) {
        this(tagInput, null);
    }

    public TagResultBean(Node pc) {
        this.code= pc.getProperty("code").toString();
        if ( pc.hasProperty("name"))
            this.name = pc.getProperty("name").toString();
        this.label = TagHelper.getLabel(pc.getLabels());
    }

    public String getKey() {
        return key;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<AliasResultBean> getAliases() {
        return aliases;
    }

    public String getLabel() {
        return label;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    // Used as a hint to see if we should attempt to create a TagLabel for this tag
    @JsonIgnore
    public boolean isNew() {
        return newTag;
    }

    void setNew(){
        this.newTag = true;
    }

    @Override
    public String toString() {
        return "TagResultBean{" +
                "label='" + label + '\'' +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getRelationship() {
        return relationship;
    }
}
