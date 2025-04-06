package com.sbt.mg.exception.checkmodel;

public class ClassDuplicationException extends CheckXmlModelException {
    public ClassDuplicationException(String className) {
        super(join("Duplication of class names is detected when describing the model for the class name",
                className),
            join("It is necessary to rename or delete one of the classes with the name", className));
    }
}
