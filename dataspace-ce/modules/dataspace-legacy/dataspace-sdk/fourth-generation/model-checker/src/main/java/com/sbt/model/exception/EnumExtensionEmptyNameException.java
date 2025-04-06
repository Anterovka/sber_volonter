package com.sbt.model.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class EnumExtensionEmptyNameException extends CheckXmlModelException {
    public EnumExtensionEmptyNameException(String enumName, String enumValue) {
        super(String.format("For value %s of enumeration %s in one of the extensions, the empty name is empty.", enumValue, enumName),
            "Specify an extension with a non - empty name.");
    }
}
