package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * No description of the link collection was found
 */
public class ReferencesCollectionDescriptionNotFoundException extends FeatherException {

    /**
     * @param propertyName Property name
     */
    ReferencesCollectionDescriptionNotFoundException(String propertyName) {
        super("Description of the links collection not found", param("Property name", propertyName));
    }
}
