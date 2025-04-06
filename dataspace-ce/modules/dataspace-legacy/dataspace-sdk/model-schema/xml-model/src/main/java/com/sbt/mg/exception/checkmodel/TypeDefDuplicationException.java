package com.sbt.mg.exception.checkmodel;

public class TypeDefDuplicationException extends CheckXmlModelException {
    public TypeDefDuplicationException(String typeDefName) {
        super(join("Duplication of own names types is detected.",
                "Duplication by type", typeDefName),
            join("It is necessary to rename the type with the name", typeDefName));
    }
}
