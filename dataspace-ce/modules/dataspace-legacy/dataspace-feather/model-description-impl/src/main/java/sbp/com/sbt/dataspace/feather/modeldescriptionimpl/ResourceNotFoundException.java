package sbp.com.sbt.dataspace.feather.modeldescriptionimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

/**
 * Ресурс не найден
 */
public class ResourceNotFoundException extends FeatherException {

    ResourceNotFoundException() {
        super("Ресурс не найден");
    }
}
