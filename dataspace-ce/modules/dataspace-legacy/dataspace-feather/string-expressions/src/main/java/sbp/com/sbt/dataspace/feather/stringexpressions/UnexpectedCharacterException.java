package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import java.util.List;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Unexpected character
 */
public class UnexpectedCharacterException extends FeatherException {

    /**
     * @param position          Позиция
     * @param expectedCharacter Expected character
     * @param character         Символ
     */
    UnexpectedCharacterException(int position, char expectedCharacter, char character) {
        super("Unexpected symbol", param("Position", position), param("Expected symbol", expectedCharacter), param("Symbol", character));
    }

    /**
     * @param position           Позиция
     * @param expectedCharacters Expected characters
     * @param character          Символ
     */
    UnexpectedCharacterException(int position, List<Character> expectedCharacters, char character) {
        super("Unexpected symbol", param("Position", position), param("Expected characters", expectedCharacters), param("Symbol", character));
    }
}
