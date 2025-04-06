package com.sbt.model.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.List;

public class EmptyEnumException extends CheckXmlModelException {
    public EmptyEnumException(List<String> emptyEnums) {
        super(String.format("In the model, there are enumerations (enum) %s without values.", emptyEnums),
            "The values of enumerations should be added or removed.");
    }
}
