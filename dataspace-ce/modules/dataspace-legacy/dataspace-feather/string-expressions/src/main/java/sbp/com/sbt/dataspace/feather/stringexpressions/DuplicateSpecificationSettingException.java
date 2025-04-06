package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Duplicates of the settings specification were found
 */
public class DuplicateSpecificationSettingException extends FeatherException {

    /**
     * @param type    Тип
     * @param context Контекст
     */
    DuplicateSpecificationSettingException(TokenKind type, String context) {
        super("Duplicate settings specification detected", param("Type", type), param("Context", context));
    }
}
