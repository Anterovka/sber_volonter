package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class NonAbstractClassWithoutPropertiesException extends CheckXmlModelException {
    public NonAbstractClassWithoutPropertiesException(String className) {
        super(join("It is forbidden to declare a class without properties in the model while not specifying the sign",
                XmlModelClass.ABSTRACT_TAG, ". Error in class", className),
            "Set the flag on the class or add properties");
    }
}
