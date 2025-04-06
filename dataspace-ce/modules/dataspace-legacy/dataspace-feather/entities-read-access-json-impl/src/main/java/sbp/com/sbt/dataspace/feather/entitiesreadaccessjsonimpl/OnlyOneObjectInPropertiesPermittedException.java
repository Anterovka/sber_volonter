package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * In the properties is allowed only one object
 */
public class OnlyOneObjectInPropertiesPermittedException extends FeatherException {

    /**
     * @param json JSON
     */
    OnlyOneObjectInPropertiesPermittedException(String json) {
        super("In properties only one object is allowed", param("JSON", json));
    }
}
