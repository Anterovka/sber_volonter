package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Attempt to retrieve collection element outside of collection
 */
public class GetElementOutsideOfCollectionException extends FeatherException {

    /**
     * @param context Контекст
     */
    GetElementOutsideOfCollectionException(String context) {
        super("Attempt to get collection element outside of collection", param("Context", context));
    }
}
