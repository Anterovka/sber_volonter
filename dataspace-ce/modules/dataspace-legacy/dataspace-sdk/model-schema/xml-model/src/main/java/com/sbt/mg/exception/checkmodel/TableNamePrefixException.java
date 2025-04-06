package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.ModelHelper;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

/**
 * Exception: The class name is already set in the base module model
 */
public class TableNamePrefixException extends CheckXmlModelException {
    /**
     * @param tablePrefix Name of the class
     */
    public TableNamePrefixException(String tablePrefix) {
        super(join("  The model prefix name must consist of uppercase letters, " +
                "not to contain Cyrillic characters, not to be empty and not to exceed the length of" +
                ModelHelper.MAX_TABLE_PREFIX_NAME, ". Error in prefix name:", tablePrefix),
            "Fix according to the requirements");


    }
}
