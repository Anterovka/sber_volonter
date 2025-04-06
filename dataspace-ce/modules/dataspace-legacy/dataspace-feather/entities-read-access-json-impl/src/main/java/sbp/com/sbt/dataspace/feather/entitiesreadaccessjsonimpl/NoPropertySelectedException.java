package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * No properties have been selected
 */
public class NoPropertySelectedException extends FeatherException {

    /**
     * @param json JSON
     */
    NoPropertySelectedException(String json) {
        super("No property has been selected", param("JSON", json));
    }
}
