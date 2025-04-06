package com.sbt.mg.exception.checkmodel;

public class InterfaceDuplicationException extends CheckXmlModelException {
    public InterfaceDuplicationException(String className) {
        super(join("Duplication of interface names is detected when describing the model -",
                className),
            join("It is necessary to rename or delete one of the interfaces with the name", className));
    }
}
