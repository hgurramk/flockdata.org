package org.flockdata.engine.dao;

import org.flockdata.model.Tag;
import org.flockdata.registration.bean.TagResultBean;
import org.neo4j.cypher.internal.compiler.v2_2.PathImpl;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by mike on 28/12/15.
 */
@Service
public class TagPathDao {
    @Autowired
    Neo4jTemplate template;


    private Logger logger = LoggerFactory.getLogger(TagPathDao.class);

    public Collection<Map<String, Object>> getPaths(Tag tag, int length, String label) {
        String query = "match p=(t) -[*.."+length+"]->(targetTag:`"+label+"`) where id(t)= {0}   return p";
        Map<String, Object> params = new HashMap<>();
        params.put("0", tag.getId());
        Collection<Map<String, Object>> results = new ArrayList<>();
        Result<Map<String, Object>> qResults = template.query(query, params);
        for (Map<String, Object> row : qResults) {
            PathImpl paths = (PathImpl) row.get("p");

            Map<String, Object> path = new HashMap<>();
            results.add(path);
            Integer pathCount = 0;
            TagResultBean root = null;
            String relationship = null;
            for (Iterator<PropertyContainer> iterator = paths.iterator(); iterator.hasNext(); ) {
                PropertyContainer pc = iterator.next();
                if (pc instanceof Node) {
                    TagResultBean tagResultBean = new TagResultBean((Node) pc);
                    path.put((pathCount++).toString(), tagResultBean);
                    if ( iterator.hasNext()) {
                        Relationship rel = (Relationship) iterator.next(); // Get the relationship
                        relationship = rel.getType().name();
                        tagResultBean.setRelationship(relationship);
//                        logger.info("Ignoring {} for {}, tag {}", rel.getType().name(), pathCount--, tagResultBean);
                    }
                } else {
                    logger.info("What the hell am I doing here?");
                }


            }
        }
        return results;
    }

}
