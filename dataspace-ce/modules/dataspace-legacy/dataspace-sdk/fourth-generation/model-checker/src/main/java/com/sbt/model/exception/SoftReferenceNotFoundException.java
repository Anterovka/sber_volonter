package com.sbt.model.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class SoftReferenceNotFoundException extends CheckXmlModelException {
    public SoftReferenceNotFoundException(String type) {
        super(join("The specified in external-type type ", type, " is not used in the model"),
            join("Use the soft link type contained in the model"));
    }
}
