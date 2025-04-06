package com.sbt.model.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class DuplicateExtensionNameException extends CheckXmlModelException {
    public DuplicateExtensionNameException(String enumName, String enumValue) {
        super(String.format("Duplication of extension names in enumeration. Enumeration %s, value %s", enumName, enumValue),
            "Assign a unique extension name. Case is not considered.");
    }
}
