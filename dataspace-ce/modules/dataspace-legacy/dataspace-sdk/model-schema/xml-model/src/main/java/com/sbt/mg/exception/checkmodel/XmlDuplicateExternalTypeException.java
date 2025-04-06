package com.sbt.mg.exception.checkmodel;

public class XmlDuplicateExternalTypeException extends CheckXmlModelException {
    public XmlDuplicateExternalTypeException(String valueType, String types) {
        super(join("You cannot specify multiple identical ", valueType, " in an external-type ", types),
            join("leave only one mention of the", valueType, " in external-types"));
    }
}
