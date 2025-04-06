package com.sbt.model.exception;

import com.sbt.mg.exception.GeneralSdkException;
import com.sbt.model.exception.parent.CheckModelException;

public class EnumFieldValueNotDefinedException extends CheckModelException {

    public EnumFieldValueNotDefinedException(String enumClass, String enumObject, String enumField) {
        super(GeneralSdkException.join("In the enumeration", enumClass, "no value is defined for the field", enumField, "in the instance", enumObject),
            "Determine values for all fields of enumeration objects.");
    }
}
