package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Unexpected specification of collection element for primitive values
 */
public class UnexpectedPrimitiveExpressionsCollectionSpecificationException extends FeatherException {

    /**
     * @param context Контекст
     */
    UnexpectedPrimitiveExpressionsCollectionSpecificationException(String context) {
        super("Unexpected specification of collection element for primitive values", param("Context", context));
    }
}
