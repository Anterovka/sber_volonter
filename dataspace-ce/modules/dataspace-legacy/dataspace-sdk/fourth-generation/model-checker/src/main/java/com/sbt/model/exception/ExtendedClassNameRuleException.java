package com.sbt.model.exception;

import com.sbt.mg.ModelHelper;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

/**
 * Exception: The class name is already set in the base module model
 */
public class ExtendedClassNameRuleException extends CheckXmlModelException {
    /**
     * @param className Name of the class
     */
    public ExtendedClassNameRuleException(String className) {
        super(join("The name of the derived class must start with a capital letter, be not empty and not exceed the length",
                ModelHelper.DEFAULT_MAX_CLASS_NAME_LENGTH, ". Error in class name:", className),
            join("The first character of the string in the extends tag must be written in upper case"));
    }
}
