package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

/**
 * Unexpected end of line
 */
public class UnexpectedEndOfStringException extends FeatherException {

    UnexpectedEndOfStringException() {
        super("Unexpected end of line");
    }
}
