package routines;

import java.util.Map;

import org.flockdata.helper.FlockException;
import org.flockdata.profile.model.Mappable;
import org.flockdata.profile.model.ProfileConfiguration;
import org.flockdata.registration.bean.TagInputBean;
import org.flockdata.transform.Transformer;
import org.flockdata.transform.csv.CsvEntityMapper;
import org.flockdata.transform.tags.TagMapper;
import org.slf4j.LoggerFactory;

public class TagRoutine {
 
 private static org.slf4j.Logger logger = LoggerFactory.getLogger(Transformer.class);

    public static Mappable getMappable(ProfileConfiguration profile) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Mappable mappable = null;

        if (!(profile.getHandler() == null || profile.getHandler().equals("")))
            mappable = (Mappable) Class.forName(profile.getHandler()).newInstance();
        else if (profile.getTagOrEntity() == ProfileConfiguration.DataType.ENTITY) {
            mappable = CsvEntityMapper.newInstance(profile);
        } else if (profile.getTagOrEntity() == ProfileConfiguration.DataType.TAG) {
            mappable = TagMapper.newInstance(profile);
        } else
            logger.error("Unable to determine the implementing handler");


        return mappable;
    }
    
    public static TagInputBean transformToTag(Map<String, Object> row, ProfileConfiguration importProfile) throws FlockException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        Mappable mappable = getMappable(importProfile);
        mappable.setData(row, importProfile);
        return (TagInputBean) mappable;

    }
}