package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The description of the parent entity is final
 */
public class FinalParentEntityDescriptionException extends FeatherException {

    /**
     * @param parentEntityType Type of parent entity
     */
    FinalParentEntityDescriptionException(String parentEntityType) {
        super("Final description of the parent entity", param("Parent Entity Type", parentEntityType));
    }
}
