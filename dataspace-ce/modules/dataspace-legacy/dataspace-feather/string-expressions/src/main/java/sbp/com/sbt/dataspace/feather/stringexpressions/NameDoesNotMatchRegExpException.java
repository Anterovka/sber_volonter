package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The name does not match the regular expression
 */
public class NameDoesNotMatchRegExpException extends FeatherException {

    /**
     * @param name   Name
     * @param regexp expression
     */
    NameDoesNotMatchRegExpException(String name, String regexp) {
        super("The name does not match the regular expression", param("Name", name), param("Regular expression", regexp));
    }
}
