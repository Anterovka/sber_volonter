package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The expression is not a collection of primitive expressions
 */
public class NotPrimitiveExpressionsCollectionException extends FeatherException {

    /**
     * @param context Контекст
     */
    NotPrimitiveExpressionsCollectionException(String context) {
        super("The expression is not a collection of primitive expressions", param("Context", context));
    }
}
