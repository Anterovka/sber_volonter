package com.sbt.mg.exception.checkmodel;

import static com.sbt.mg.data.model.XmlModel.ALIASES_TAG;

public class TypeDefXmlDuplicationException extends CheckXmlModelException {
    public TypeDefXmlDuplicationException() {
        super(String.format("In the model there are several sections %s.", ALIASES_TAG),
            "Leave one section.");
    }
}
