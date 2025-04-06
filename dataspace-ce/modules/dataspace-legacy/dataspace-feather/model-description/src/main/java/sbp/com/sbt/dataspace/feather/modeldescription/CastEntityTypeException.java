package sbp.com.sbt.dataspace.feather.modeldescription;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

/**
 * Entity type casting error
 */
public class CastEntityTypeException extends FeatherException {

    /**
     * @param entityType       Entity type
     * @param targetEntityType Target entity type
     */
    CastEntityTypeException(String entityType, String targetEntityType) {
        super("Error in entity type casting", entityType + " cannot be cast to " + targetEntityType);
    }
}
