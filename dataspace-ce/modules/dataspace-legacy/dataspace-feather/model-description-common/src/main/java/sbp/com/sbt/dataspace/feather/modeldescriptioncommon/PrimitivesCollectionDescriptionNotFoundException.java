package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * No primitive collection description found
 */
public class PrimitivesCollectionDescriptionNotFoundException extends FeatherException {

    /**
     * @param propertyName Property name
     */
    PrimitivesCollectionDescriptionNotFoundException(String propertyName) {
        super("Description of the primitive collection not found", param("Property name", propertyName));
    }
}
