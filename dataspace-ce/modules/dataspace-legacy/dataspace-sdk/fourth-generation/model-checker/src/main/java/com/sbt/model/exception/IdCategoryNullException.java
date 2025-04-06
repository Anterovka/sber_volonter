package com.sbt.model.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class IdCategoryNullException extends CheckXmlModelException {
    public IdCategoryNullException(String className) {
        super(String.format("In the class '%s' for id, the required category attribute is missing", className));
    }
}
