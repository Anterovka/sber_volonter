package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import java.util.Collection;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Duplicate property names were found
 */
public class DuplicatePropertyNamesFoundException extends FeatherException {

    static final String DESCRIPTION = "Duplicate property names have been detected";

    /**
     * @param entityType    Entity type
     * @param propertyNames Names of properties
     */
    DuplicatePropertyNamesFoundException(String entityType, Collection<String> propertyNames) {
        super(DESCRIPTION, param("Entity type", entityType), param("Property names", propertyNames));
    }

    /**
     * @param propertyName Property name
     */
    DuplicatePropertyNamesFoundException(String propertyName) {
        super(DESCRIPTION, param("Property name description", propertyName));
    }
}
