package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
* The expression is not a condition
 */
public class NotConditionException extends FeatherException {

    /**
     * @param context Контекст
     */
    NotConditionException(String context) {
super("The expression is not a condition", param("Context", context));
    }
}
