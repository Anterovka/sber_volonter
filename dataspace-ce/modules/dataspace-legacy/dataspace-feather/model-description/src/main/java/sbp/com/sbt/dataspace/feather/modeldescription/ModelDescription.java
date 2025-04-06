package sbp.com.sbt.dataspace.feather.modeldescription;

import java.util.List;
import java.util.Map;

/**
 * Model description
 */
// SpringBean
public interface ModelDescription extends ObjectWithMetaDataManager {

    /**
     * Get entity descriptions
     */
    // NotNull
    Map<String, EntityDescription> getEntityDescriptions();

    /**
     * Get entity description
     *
     * @param entityType Entity type
     */
    // NotNull
    EntityDescription getEntityDescription(String entityType);

    /**
     * Get descriptions of groupings
     */
    // NotNull
    Map<String, List<GroupDescription>> getGroupDescriptions();

    /**
     * Get enumeration descriptions
     */
    // NotNull
    Map<String, EnumDescription> getEnumDescriptions();

    /**
     * Get enumeration description
     *
     * @param enumType The enumeration type
     */
    // NotNull
    EnumDescription getEnumDescription(String enumType);
}
