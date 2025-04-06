package com.sbt.mg.exception.checkmodel;

public class SameEnumClassNameException extends CheckXmlModelException {
    public SameEnumClassNameException(String enumName) {
        super(join("Duplication of enum classes in the model with the name", enumName),
            "Fix the class name to an undeclared name in the model");
    }
}
