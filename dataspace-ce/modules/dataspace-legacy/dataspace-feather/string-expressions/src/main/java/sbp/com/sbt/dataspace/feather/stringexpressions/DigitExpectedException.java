package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Expected a digit
 */
public class DigitExpectedException extends FeatherException {

    /**
     * @param position  Позиция
     * @param character Символ
     */
    DigitExpectedException(int position, char character) {
        super("Expected digit", param("Position", position), param("Character", character));
    }
}
