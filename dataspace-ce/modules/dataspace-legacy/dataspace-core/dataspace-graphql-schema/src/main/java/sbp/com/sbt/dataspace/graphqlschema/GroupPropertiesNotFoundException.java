package sbp.com.sbt.dataspace.graphqlschema;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The grouping properties were not found
 */
public class GroupPropertiesNotFoundException extends FeatherException {

    /**
     * @param groupName The name of the grouping
     */
    GroupPropertiesNotFoundException(String groupName) {
        super("Properties of grouping are not found", param("Grouping name", groupName));
    }
}
