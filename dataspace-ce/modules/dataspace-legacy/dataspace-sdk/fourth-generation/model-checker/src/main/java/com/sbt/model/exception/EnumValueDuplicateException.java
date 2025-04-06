package com.sbt.model.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class EnumValueDuplicateException extends CheckXmlModelException {

    public EnumValueDuplicateException(String enumName) {
        super(join("For enum-a", enumName, "duplication of values is found."),
            "Ensure unique values for the enumeration.");
    }
}
