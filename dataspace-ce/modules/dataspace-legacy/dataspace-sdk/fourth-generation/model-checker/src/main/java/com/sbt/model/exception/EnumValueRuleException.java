package com.sbt.model.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

/**
 * Exception: The class name is already set in the base module model
 */
public class EnumValueRuleException extends CheckXmlModelException {
    /**
     * @param className Name of the class
     */
    public EnumValueRuleException(String value, String className, int maxLenght) {
        super(join("Value", value, "in enum class", className,
                "must consist of only uppercase (capital) Latin characters,",
                "numbers and symbols \"_\", not be empty and not exceed the length", maxLenght),
            "Correct according to the requirements.");
    }
}
