package sbp.com.sbt.dataspace.feather.modeldescription;

import sbp.com.sbt.dataspace.feather.common.MetaDataManager;

/**
 * Object with metadata manager
 */
public interface ObjectWithMetaDataManager {

    /**
     * Get metadata manager
     */
    // NotNull
    MetaDataManager getMetaDataManager();
}
