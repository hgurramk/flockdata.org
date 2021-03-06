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

package org.flockdata.engine.track.service;

import org.flockdata.dao.EntityTagDao;
import org.flockdata.engine.dao.EntityRepo;
import org.flockdata.engine.dao.EntityTagDaoNeo;
import org.flockdata.engine.dao.EntityTagRepo;
import org.flockdata.model.EntityTagIn;
import org.flockdata.model.EntityTagOut;
import org.flockdata.helper.FlockException;
import org.flockdata.helper.SecurityHelper;
import org.flockdata.registration.bean.TagInputBean;
import org.flockdata.model.Company;
import org.flockdata.model.Tag;
import org.flockdata.track.bean.EntityInputBean;
import org.flockdata.track.bean.EntityTagInputBean;
import org.flockdata.model.Entity;
import org.flockdata.model.EntityLog;
import org.flockdata.model.EntityTag;
import org.flockdata.model.Log;
import org.flockdata.track.service.EntityTagService;
import org.flockdata.track.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * User: Mike Holdsworth
 * Date: 27/06/13
 * Time: 5:07 PM
 */
@Service
@Transactional
public class EntityTagServiceNeo4j implements EntityTagService {

    @Autowired
    TagService tagService;

    @Autowired
    SecurityHelper securityHelper;

    @Autowired
    EntityTagDaoNeo entityTagDao;

    @Autowired
    EntityRepo entityRepo;

    @Autowired
    Neo4jTemplate template;

    private Logger logger = LoggerFactory.getLogger(EntityTagServiceNeo4j.class);

    @Override
    public void processTag(Entity entity, EntityTagInputBean entityTagInput) {
        String relationshipName = entityTagInput.getType();

        boolean existing = relationshipExists(entity, entityTagInput.getTagKeyPrefix(), entityTagInput.getTagCode(), relationshipName);
        if (existing)
            // We already have this tagged so get out of here
            return;
        Tag tag = tagService.findTag(entity.getFortress().getCompany(), entityTagInput.getIndex(), entityTagInput.getTagKeyPrefix(), entityTagInput.getTagCode());
        template.save(
                getRelationship(entity, tag, relationshipName, false, new HashMap<>(), entityTagInput.isSince())
        );
    }

    @Override
    public Boolean relationshipExists(Entity entity, String tagCode, String relationshipType) {
        return relationshipExists(entity, null, tagCode, relationshipType);
    }

    @Override
    public Boolean relationshipExists(Entity entity, String keyPrefix, String tagCode, String relationshipType) {
        Tag tag = tagService.findTag(entity.getFortress().getCompany(), keyPrefix, tagCode);
        if (tag == null)
            return false;
        Collection<EntityTag> entityTags = getEntityTags(entity);
        for (EntityTag entityTag : entityTags) {
            String rType = template.getRelationship(entityTag.getId()).getType().name();
            if (entityTag.getTag().getId().equals(tag.getId()) && rType.equals(relationshipType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Only returns the tag - ignores the relationship
     *
     * @param entityTags
     * @param tagInputBean
     * @return
     */
    private Tag getTag(Iterable<EntityTag> entityTags, TagInputBean tagInputBean) {
        for (EntityTag existingTag : entityTags) {
            if (existingTag.getTag().getCode().equalsIgnoreCase(tagInputBean.getCode())
                    && existingTag.getTag().getLabel().equalsIgnoreCase(tagInputBean.getLabel())
                    )

                return existingTag.getTag();
        }
        return null;
    }

    private EntityTag getEntityTag(Iterable<EntityTag> existingTags, TagInputBean tagInputBean) {
        for (EntityTag existingTag : existingTags) {
            if (existingTag.getTag().getCode().equalsIgnoreCase(tagInputBean.getCode())
                    && existingTag.getTag().getLabel().equalsIgnoreCase(tagInputBean.getLabel())) {
                if (tagInputBean.hasRelationship(template.getRelationship(existingTag.getId()).getType().name()))
                    return existingTag;
            }
        }
        return null;
    }

    /**
     * Associates the supplied userTags with the EntityNode
     * <p>
     * in JSON terms....
     * "ClientID123" :{"clientKey","prospectKey"}
     * <p>
     * <p>
     * The value can be null which will create a simple tag for the Entity such as
     * ClientID123
     * <p>
     * They type can be Null, String or a Collection<String> that describes the relationship
     * types to create.
     * <p>
     * If this scenario, ClientID123 is created as a single node with two relationships that
     * describe the association - clientKey and prospectKey
     *
     * @param company
     * @param entity          Entity to associate userTags with
     * @param lastLog
     * @param entityInputBean payload
     */
    @Override
    public Collection<EntityTag> associateTags(Company company, Entity entity, EntityLog lastLog, EntityInputBean entityInputBean) throws FlockException {
        Collection<EntityTag> newEntityTags = new ArrayList<>();
        Collection<EntityTag> tagsToMove = new ArrayList<>();
        Collection<EntityTag> existingTags = (entity.isNewEntity() ? new ArrayList<>() : getEntityTags(entity));
        for (TagInputBean tagInputBean : entityInputBean.getTags()) {

            Tag existingTag = getTag(existingTags, tagInputBean);
            Tag tag;
            if (existingTag == null) {
                tag = tagService.createTag(company, tagInputBean);
            } else
                tag = existingTag;

            if (existingTag == null) {
                newEntityTags.addAll(setRelationships(entity, tag, tagInputBean));
            } else {
                EntityTag entityTag = getEntityTag(existingTags, tagInputBean);
                if (entityTag != null)
                    newEntityTags.add(entityTag);
            }
        }

        if (!entityInputBean.getTags().isEmpty() && !entity.isNewEntity()) {
            // We only consider relocating tags to the log if the caller passes at least one tag set
            for (EntityTag entityTag : existingTags) {
                if (!newEntityTags.contains(entityTag))
                    tagsToMove.add(entityTag);
            }
            if (entityInputBean.isArchiveTags())
                if (lastLog != null) {
                    if (lastLog.isMocked()) {
                        for (EntityTag entityTag : tagsToMove) {
                            // Nowhere to move the tags too so we just delete them
                            template.delete(entityTag);
                        }
                    } else
                        moveTags(lastLog, tagsToMove);
                }
        }
        if (!entityInputBean.isTrackSuppressed())
            for (EntityTag entityTag : newEntityTags) {
                if (entityTag.getId() == null) // ToDo: This check should be redundant
                    template.saveOnly(entityTag);
            }
        return newEntityTags;
    }


    private void moveTags(EntityLog currentLog, Collection<EntityTag> tagsToRelocate) {
        if (!tagsToRelocate.isEmpty()) {
            if (currentLog != null)
                entityTagDao.moveTags(currentLog.getLog(), tagsToRelocate);
        }
    }

    /**
     * Creates and sets the relationship objects to in to the entity
     * <p>
     * Does not save the entity
     *
     * @param entity       Object to affect
     * @param tag          Tag to associated
     * @param tagInputBean Tag control
     * @return EntityTags that were added to the entity.
     */
    private Collection<EntityTag> setRelationships(Entity entity, Tag tag, TagInputBean tagInputBean) {
        Map<String, Object> entityLinks = tagInputBean.getEntityLinks();

        Collection<EntityTag> entityTags = new ArrayList<>();
        long when = (entity.getFortressUpdatedTz() == null ? 0 : entity.getFortressUpdatedTz().getMillis());
        if (when == 0)
            when = entity.getDateCreated();

        if (entityLinks == null) {
            return new ArrayList<>();
        }
        for (String key : entityLinks.keySet()) {
            Object properties = entityLinks.get(key);
            Map<String, Object> propMap;
            if (properties != null && properties instanceof Map) {
                propMap = (Map<String, Object>) properties;
            } else {
                propMap = new HashMap<>();
            }

            propMap.put(EntityTagDao.FD_WHEN, when);
            EntityTag entityTagRelationship = getRelationship(entity, tag, key, tagInputBean.isReverse(), propMap, tagInputBean.isSince());
            if (entityTagRelationship != null) {
                entityTags.add(entityTagRelationship);
            }

        }
        return entityTags;
    }

    /**
     * Calculates an object  between the entity and the tag of the requested type.
     * The relationship is not added to the entity and is just returned to teh caller
     * for processing and persistence.
     *
     * @param entity           valid entity
     * @param tag              tag
     * @param relationshipName name
     * @param isReversed       tag<-entity (false) or entity->tag (true)
     * @param propMap          properties to associate with the relationship
     * @return Null or the EntityTag that was created
     */
    EntityTag getRelationship(Entity entity, Tag tag, String relationshipName, Boolean
            isReversed, Map<String, Object> propMap, boolean isSinceRequired) {

        if (isSinceRequired) {
            long lastUpdate = (entity.getFortressUpdatedTz() == null ? 0 : entity.getFortressUpdatedTz().getMillis());
            propMap.put(EntityTag.SINCE, (lastUpdate == 0 ? entity.getFortressCreatedTz().getMillis() : lastUpdate));
        }
        EntityTag rel;
        if (isReversed)
            rel = new EntityTagOut(entity, tag, relationshipName, propMap);
        else
            rel = new EntityTagIn(entity, tag, relationshipName, propMap);

        logger.trace("Created Relationship Tag[{}] of type {}", tag, relationshipName);
        return rel;
    }

    /**
     * Finds both incoming and outgoing tags for the Entity
     *
     * @param entity Entity the caller is authorised to work with
     * @return EntityTags found
     */
    @Override
    public Collection<EntityTag> findEntityTags(Entity entity) {
        Company company = securityHelper.getCompany();
        return findEntityTags(company, entity);
    }

    public Collection<EntityTag> findEntityTags(Company company, Entity entity) {
        return getEntityTags(entity);
    }

    @Override
    public Collection<EntityTag> findOutboundTags(Entity entity) {
        Company company = securityHelper.getCompany();
        return findOutboundTags(company, entity);
    }

    @Autowired
    EntityTagRepo entityTagRepo;

    @Override
    public Collection<EntityTag> findOutboundTags(Company company, Entity entity) {
        return entityTagRepo.getEntityTagsOut(entity.getId());
    }

    @Override
    public Collection<EntityTag> findInboundTags(Company company, Entity entity) {
        return entityTagRepo.getEntityTagsIn(entity.getId());
    }

    @Override
    public Collection<EntityTag> findInboundTags(Entity entity) {
        Company company = securityHelper.getCompany();
        return findInboundTags(company, entity);
    }

    @Override
    public Collection<EntityTag> getEntityTags(Entity entity) {
        return entityTagDao.getEntityTags(entity);
    }

    @Override
    public Iterable<EntityTag> getEntityTagsWithGeo(Entity entity) {
        return entityTagDao.getEntityTagsWithGeo(entity);
    }

    @Override
    public Collection<EntityTag> findLogTags(Company company, Log log) {
        return entityTagDao.findLogTags(company, log);
    }

    @Override
    public void deleteEntityTags(Entity entity, Collection<EntityTag> entityTags) throws FlockException {
        entityTagDao.deleteEntityTags(entityTags);
    }

    @Override
    public void deleteEntityTags(Entity entity, EntityTag value) throws FlockException {
        Collection<EntityTag> remove = new ArrayList<>(1);
        remove.add(value);
        deleteEntityTags(entity, remove);

    }

    @Override
    public void changeType(Entity entity, EntityTag existingTag, String newType) throws FlockException {
        if (entity == null || existingTag == null || newType == null)
            throw new FlockException(("Illegal parameter"));
        entityTagDao.changeType(entity, existingTag, newType);
    }


    @Override
    public Set<Entity> findEntityTags(Company company, String tagCode) throws FlockException {
        Tag tag = tagService.findTag(company, null, tagCode);
        if (tag == null)
            throw new FlockException("Unable to find the tag [" + tagCode + "]");
        return entityTagDao.findEntityTags(tag);

    }


    @Override
    public Entity moveTags(Company company, Log previousLog, Entity entity) {
        entityTagDao.moveTags(company, previousLog, entity);
        return entity;
    }

    /**
     * @param fromTag tag that will be deleted
     * @param toTag   tag to merge fromTag into
     * @return Collection of affected Entity IDs
     */
    @Override
    public Collection<Long> mergeTags(Long fromTag, Long toTag) {
        return entityTagDao.mergeTags(fromTag, toTag);
    }

    @Override
    public void purgeUnusedTags(String label) {
        entityTagDao.purgeUnusedTags(label);
    }

    @Override
    public Collection<EntityTag> findEntityTagsByRelationship(Entity entity, String relationship) {
        return new ArrayList<>();//entityTagDao.findEntityTagsByRelationship(entity, relationship);
    }
}
