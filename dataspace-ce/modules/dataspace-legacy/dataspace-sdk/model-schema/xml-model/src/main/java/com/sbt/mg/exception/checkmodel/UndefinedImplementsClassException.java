package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.data.model.XmlModelClass;

public class UndefinedImplementsClassException extends CheckXmlModelException {
    public UndefinedImplementsClassException(String className, String iName) {
        super(join("The class", className, "has an interface", iName, "which is not described in the model"),
            join("Define proper interfaces for the class", className, "or fix the tag", XmlModelClass.IMPLEMENTS_TAG));
    }
}
