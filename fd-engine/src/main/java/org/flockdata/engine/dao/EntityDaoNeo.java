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

package org.flockdata.engine.dao;

import org.flockdata.engine.track.service.TrackEventService;
import org.flockdata.helper.FlockException;
import org.flockdata.kv.service.KvService;
import org.flockdata.registration.service.KeyGenService;
import org.flockdata.registration.service.SystemUserService;
import org.flockdata.track.bean.EntityInputBean;
import org.flockdata.track.bean.EntityKeyBean;
import org.flockdata.track.bean.EntityTXResult;
import org.flockdata.track.bean.TrackResultBean;
import org.flockdata.model.*;
import org.flockdata.track.service.EntityTagService;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;

/**
 * User: Mike Holdsworth
 * Date: 21/04/13
 * Time: 8:00 PM
 */
@Repository("entityDao")
public class EntityDaoNeo {
    @Autowired
    EntityRepo entityRepo;

    @Autowired
    TxRepo txRepo;

    @Autowired
    TrackLogRepo trackLogRepo;

    @Autowired
    DocumentTypeRepo documentTypeRepo;

    @Autowired
    TrackEventService trackEventService;

    @Autowired
    KvService kvService;

    @Autowired
    KeyGenService keyGenService;

    @Autowired
    SystemUserService systemUserService;

    @Autowired
    EntityTagService entityTagService;

    @Autowired
    Neo4jTemplate template;

    private Logger logger = LoggerFactory.getLogger(EntityDaoNeo.class);

    public Entity create(EntityInputBean inputBean, FortressSegment segment, FortressUser fortressUser, DocumentType documentType) throws FlockException {
        String metaKey = (inputBean.isTrackSuppressed() ? null : keyGenService.getUniqueKey());
        Entity entity = new Entity(metaKey, segment, inputBean, documentType);
        entity.setCreatedBy(fortressUser);
        entity.addLabel(documentType.getName());
        if (!inputBean.isTrackSuppressed()) {
            logger.debug("Creating {}", entity);
            entity = save(entity);
        }
        return entity;
    }

    public Entity save(Entity entity) {
        return save(entity, false);
    }

    /**
     * @param entity  to save
     * @param quietly if we're doing this quietly then lastUpdate is not changed
     * @return saved entity
     */
    public Entity save(Entity entity, boolean quietly) {
        if (!quietly)
            entity.bumpUpdate();
        return entityRepo.save(entity);

    }

    public TxRef save(TxRef tagRef) {
        return txRepo.save(tagRef);
    }

    private Entity getCachedEntity(String key) {
        if (key == null)
            return null;
        return entityRepo.findBySchemaPropertyValue(Entity.UUID_KEY, key);
    }

    public Entity findEntity(String key, boolean inflate) {
        Entity entity = getCachedEntity(key);
        if (inflate && entity != null) {
            fetch(entity);

        }
        return entity;
    }

    public Collection<Entity> findByCode(Long fortressId, String callerRef) {
        return entityRepo.findByCode(fortressId, callerRef);

    }

    public Entity findByCodeUnique(Long fortressId, String callerRef) throws FlockException {
        int count = 0;
        Iterable<Entity> entities = findByCode(fortressId, callerRef);
        Entity result = null;
        for (Entity entity : entities) {
            count++;
            result = entity;
            if (count > 1) break;
        }
        if (count > 1)
            throw new FlockException("Unable to find exactly one record for the callerRef [" + callerRef + "]. Found " + count);

        return result;

    }

    public Entity findByCode(Long fortressId, Long documentId, String callerRef) {
        if (logger.isTraceEnabled())
            logger.trace("findByCode fortressUser [" + fortressId + "] docType[" + documentId + "], callerRef[" + callerRef + "]");

        String keyToFind = "" + fortressId + "." + documentId + "." + callerRef;
        Entity result= entityRepo.findBySchemaPropertyValue("key", keyToFind);

        fetch(result);
        return result;
    }

    public Entity fetch(Entity entity) {
        if (entity != null ) {
            template.fetch(entity.getCreatedBy());
            template.fetch(entity.getLastUser());
        }

        return entity;
    }

    public Set<Entity> findEntitiesByTxRef(Long txRef) {
        return entityRepo.findEntitiesByTxRef(txRef);
    }

    public Collection<Entity> findEntities(Long fortressId, Long lastEntityId) {
        return entityRepo.findEntities(fortressId, lastEntityId);
    }

    public Collection<Entity> findEntities(Long fortressId, String label, Long skipTo) {
        //ToDo: Should this pass in timestamp it got to??
        String cypher = "match (f:Fortress)-[:DEFINES]-(s:FortressSegment)-[:TRACKS]->(meta:`" + label + "`) where id(f)={fortress} return meta ORDER BY meta.dateCreated ASC skip {skip} limit 100 ";
        Map<String, Object> args = new HashMap<>();
        args.put("fortress", fortressId);
        args.put("skip", skipTo);
        Iterable<Map<String, Object>> result = template.query(cypher, args);

        Iterator<Map<String, Object>> rows = result.iterator();
        Collection<Entity> results = new ArrayList<>();

        while (rows.hasNext()) {
            Map<String, Object> row = rows.next();
            results.add(template.projectTo(row.get("meta"), Entity.class));
        }

        return results;
    }

    public void delete(Log currentChange) {
        trackLogRepo.delete(currentChange);
    }

    public org.flockdata.model.TxRef findTxTag(@NotEmpty String txTag, @NotNull Company company) {
        return txRepo.findTxTag(txTag, company.getId());
    }

    public org.flockdata.model.TxRef beginTransaction(String id, Company company) {

        org.flockdata.model.TxRef txTag = findTxTag(id, company);
        if (txTag == null) {
            txTag = new TxRef(id, company);
            txRepo.save(txTag);
        }
        return txTag;
    }

    public int getLogCount(Long id) {
        return trackLogRepo.getLogCount(id);
    }

    public Set<EntityLog> getLogs(Long entityId, Date from, Date to) {
        return trackLogRepo.getLogs(entityId, from.getTime(), to.getTime());
    }

    public Set<EntityLog> getLogs(Entity entity) {
        EntityLog mockLog = getMockLog(entity);
        if ( mockLog != null ) {
            Set<EntityLog> results = new HashSet<>();
            results.add(mockLog);
            return results;
        }

        return trackLogRepo.findLogs(entity.getId());
    }

    public Map<String, Object> findByTransaction(org.flockdata.model.TxRef txRef) {
        //Example showing how to use cypher and extract

        String findByTagRef =" match (tag)-[:AFFECTED]->log<-[logs:LOGGED]-(track) " +
                "              where id(tag)={txRef}" +
                "             return logs, track, log " +
                "           order by logs.sysWhen";
        Map<String, Object> params = new HashMap<>();
        params.put("txRef", txRef.getId());


        Iterable<Map<String, Object>> exResult = template.query(findByTagRef, params);

        Iterator<Map<String, Object>> rows;
        rows = exResult.iterator();

        List<EntityTXResult> simpleResult = new ArrayList<>();
        int i = 1;
        //Iterable<Map<String, Object>> results =
        while (rows.hasNext()) {
            Map<String, Object> row = rows.next();
            EntityLog log = template.convert(row.get("logs"), EntityLog.class);
            Log change = template.convert(row.get("log"), Log.class);
            Entity entity = template.convert(row.get("track"), Entity.class);
            simpleResult.add(new EntityTXResult(entity, change, log));
            i++;

        }
        Map<String, Object> result = new HashMap<>(i);
        result.put("txRef", txRef.getName());
        result.put("logs", simpleResult);

        return result;
    }

    public EntityLog save(EntityLog log) {
        // DAT-349 - don't persist mocked logs
        if ( log.isMocked())
            return log;
        logger.debug("Saving track log [{}] - Log ID [{}]", log, log.getLog().getId());
        return template.save(log);
    }

    public String ping() {
        return "Neo4J is OK";
    }

    public Log prepareLog(Company company, FortressUser fUser, TrackResultBean payLoad, TxRef txRef, Log previousChange) throws FlockException {
        ChangeEvent event = trackEventService.processEvent(company, payLoad.getContentInput().getEvent());
        Log changeLog = new Log(fUser, payLoad.getContentInput(), txRef);

        changeLog.setEvent(event);
        changeLog.setPreviousLog(previousChange);
        try {
            changeLog = kvService.prepareLog(payLoad, changeLog);
        } catch (IOException e) {
            throw new FlockException("Unexpected error talking to What Service", e);
        }
        return changeLog;
    }

    private EntityLog getMockLog(Entity entity){
        // DAT-349 returns a mock log if storage history is not being maintained by a KV impl
        if ( !entity.getSegment().getFortress().isStoreEnabled()){
            Log log = new Log(entity);
            return new EntityLog(entity, log, entity.getFortressCreatedTz());
        }
        return null;
    }

    public EntityLog getLog(Entity entity, Long logId) {

        EntityLog mockLog = getMockLog( entity);
        if ( mockLog !=null )
            return mockLog;

        Relationship change = template.getRelationship(logId);
        if (change != null)
            try {
                return (EntityLog) template.getDefaultConverter().convert(change, EntityLog.class);
            } catch (NotFoundException nfe) {
                // Occurs if fd-search has been down and the database is out of sync from multiple restarts
                logger.error("Error converting relationship to a LoggedRelationship");
                return null;
            }
        return null;
    }

    public Entity getEntity(Long pk) {
        return entityRepo.findOne(pk);
        //return template.findOne(pk, EntityNode.class);
    }

    public Log fetch(Log lastChange) {
        if ( lastChange.getId() == null || lastChange.getId() ==0l)
            return lastChange;
        return template.fetch(lastChange);
    }

    public EntityLog writeLog(Entity entity, Log newLog, DateTime fortressWhen) throws FlockException {

        EntityLog entityLog =new EntityLog(entity, newLog, fortressWhen);

        if (entity.getId() == null)// Graph tracking is suppressed; caller is only creating search docs
            return entityLog;

        if ( entity.getSegment().getFortress().isStoreDisabled() )
            return entityLog;

        logger.debug(entity.getMetaKey());

        Entity currentState = template.fetch(entity);
        // Entity is being committed in another thread? On occasion metakKey is null on refresh meaning the Log does not get created
        // Easiest way to test is when there is not fortress and you track the first request in to it.
        // DAT-419

        while ( currentState.getMetaKey() ==null )
            currentState = template.fetch(entity);

        if ( entity.getMetaKey() == null )
            throw new FlockException("Where has the metaKey gone?");

        entity.setLastUser(newLog.getMadeBy());
        entity.setLastChange(newLog);
        entity.setFortressLastWhen(fortressWhen.getMillis());

        if (currentState.getLastChange() == null) {
            template.save(entity);
        } else {
            logger.debug("About to save new log");
            template.save(newLog);
            template.save(entityLog);
            logger.debug("Saved new log");
            setLatest(entity);
        }

        logger.debug("Added Log - Entity [{}], Log [{}], Change [{}]", entity.getId(), newLog.getEntityLog(), newLog.getId());
        // Saving the entity causes the Log properties to be lazy initialised. If the caller wants these, then they need to fetch the object
        return entityLog;
    }

    void setLatest(Entity entity) {
        EntityLog latest = null;
        boolean moreRecent;


        Set<EntityLog> entityLogs = getLogs(entity.getId(), entity.getFortressUpdatedTz().toDate(), new DateTime().toDate());


        for (EntityLog entityLog : entityLogs) {
            if (latest == null || entityLog.getFortressWhen() > latest.getFortressWhen())
                latest = entityLog;
        }
        if (latest == null)
            return;
        moreRecent = (latest.getLog().getEntityLog().getFortressWhen() > entity.getFortressUpdatedTz().getMillis() );
        if (moreRecent) {
            logger.debug("Detected a more recent change ", new DateTime(latest.getFortressWhen()), entity.getId(), latest.getFortressWhen());

            entity.setLastChange(latest.getLog());
            entity.setLastUser(latest.getLog().getMadeBy());
            entity.setFortressLastWhen(latest.getFortressWhen());
        }


        entity = entityRepo.save(entity);
        logger.debug("Saved change for Entity [{}], log [{}]", entity.getId(), latest);

    }

    public void linkEntities(Entity entity, Collection<Entity> entities, String refName) {
        Node target = template.getPersistentState(entity);
        for (Entity sourceEntity : entities) {
            Node dest = template.getPersistentState(sourceEntity);
            if (template.getRelationshipBetween(sourceEntity, target, refName) == null)
                template.createRelationshipBetween(dest, target, refName, null);
        }
    }

    public Map<String, Collection<Entity>> getCrossReference(Entity entity, String xRefName) {
        Node n = template.getPersistentState(entity);

        RelationshipType r = DynamicRelationshipType.withName(xRefName);
        Iterable<Relationship> rlxs = n.getRelationships(r);
        Map<String, Collection<Entity>> results = new HashMap<>();
        Collection<Entity> entities = new ArrayList<>();
        results.put(xRefName, entities);
        for (Relationship rlx : rlxs) {
            entities.add(template.projectTo(rlx.getOtherNode(n), Entity.class));
        }
        return results;

    }

    public Map<String, Entity> findEntities(Company company, Collection<String> metaKeys) {
        logger.debug("Looking for {} entities for company [{}] ", metaKeys.size(), company);
        Collection<Entity> foundEntities = entityRepo.findEntities(company.getId(), metaKeys);
        Map<String, Entity> unsorted = new HashMap<>();
        for (Entity foundEntity : foundEntities) {
            if ( foundEntity.getSegment().getFortress().getCompany().getId().equals(company.getId()))
                unsorted.put(foundEntity.getMetaKey(), foundEntity);
        }
        return unsorted;
    }

    @Transactional
    public void purgeTagRelationships(Collection<String> entities) {
        // ToDo: Check if this works with huge datasets - it' doesn't fix via batch
        trackLogRepo.purgeTagRelationships(entities);
    }

    @Transactional
    public void purgeFortressLogs(Collection<String> entities) {
        trackLogRepo.purgeLogsWithUsers(entities);
        trackLogRepo.purgeFortressLogs(entities);
    }

    @Transactional
    public void purgePeopleRelationships(Collection<String> entities) {
        entityRepo.purgePeopleRelationships(entities);
    }

    @Transactional
    public void purgeEntities(Collection<String> entities) {
        entityRepo.purgeEntityLinks(entities);
        entityRepo.purgeEntities(entities);
    }

    @Transactional
    public void purgeFortressDocuments(Fortress fortress) {
        documentTypeRepo.purgeFortressDocuments(fortress.getId());
    }

    public EntityLog getLastLog(Long entityId) {
        Entity entity = getEntity(entityId);
        return getLastEntityLog(entity);
    }

    public EntityLog getLastEntityLog(Entity entity) {

        Log lastChange = entity.getLastChange();
        if (lastChange == null) {
            // If no last change, then this might be a mock log
            return getMockLog( entity);
        }

        return trackLogRepo.getLog(entity.getLastChange().getId());
    }

    public Set<EntityLog> getLogs(Long id, Date date) {
        return trackLogRepo.getLogs(id, date.getTime());
    }

    public Collection<Entity> getEntities(Collection<Long> entities) {
        return entityRepo.getEntities(entities);
    }

    public Collection<String> getEntityBatch(Long id, int limit) {
        return entityRepo.findEntitiesWithLimit(id, limit);
    }

    public Entity findParent(Entity childEntity) {
        return entityRepo.findParent(childEntity.getId());
    }

    public Collection<EntityKeyBean> getInboundEntities(Entity childEntity, boolean withEntityTags) {
        Collection<EntityKeyBean> results = new ArrayList<>();
        Collection<Entity> entities = entityRepo.findInbountEntities(childEntity.getId());
        entities.stream().filter(entity -> withEntityTags).forEach(entity -> {
            Collection<EntityTag> entityTags = entityTagService.findEntityTags(childEntity.getFortress().getCompany(), entity);
            // ToDo: Requires the entity to entity relationship name
            results.add(new EntityKeyBean(entity, entityTags).addRelationship(""));
        });
        return results;
    }
}
