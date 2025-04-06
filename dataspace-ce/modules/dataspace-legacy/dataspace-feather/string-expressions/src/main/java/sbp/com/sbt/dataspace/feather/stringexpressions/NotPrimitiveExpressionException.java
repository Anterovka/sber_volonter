package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The expression is not a primitive expression
 */
public class NotPrimitiveExpressionException extends FeatherException {

    /**
     * @param context Контекст
     */
    NotPrimitiveExpressionException(String context) {
        super("The expression is not a primitive expression", param("Context", context));
    }
}
