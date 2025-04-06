package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The end of the line was expected
 */
public class EndOfStringExpectedException extends FeatherException {

    /**
     * @param position        Позиция
     * @param remainingString The remaining string
     */
    EndOfStringExpectedException(int position, String remainingString) {
        super("The end of the line was expected", param("Position", position), param("Remaining string", remainingString));
    }
}
