package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.MetaDataManager;
import sbp.com.sbt.dataspace.feather.modeldescription.ObjectWithMetaDataManager;

/**
 * Abstract object with additional metadata
 */
class AbstractObjectWithMetaDataManager implements ObjectWithMetaDataManager {

    MetaDataManager metaDataManager = new MetaDataManager();

    @Override
    public MetaDataManager getMetaDataManager() {
        return metaDataManager;
    }
}
