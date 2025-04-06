package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import java.util.Arrays;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Токен не найден
 */
public class TokenNotFoundException extends FeatherException {

    /**
     * @param position           Позиция
     * @param expectedTokenKinds Expected types of tokens
     */
    TokenNotFoundException(int position, TokenKind[] expectedTokenKinds) {
        super("The token was not found", param("Position", position), param("Expected tokens", Arrays.toString(expectedTokenKinds)));
    }
}
