package com.sbt.model.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class EnumNameAsEntityNameException extends CheckXmlModelException {
    protected EnumNameAsEntityNameException(String enumName, String entityTypeStr) {
        super(join("The name of the enumeration intersects with the name", entityTypeStr, "of the model. Error in the name",
            enumName), "Fix it to a unique name.");
    }
}
