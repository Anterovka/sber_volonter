package com.sbt.model.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class UserQueryNameAsModelTagNameException extends CheckXmlModelException {

    public UserQueryNameAsModelTagNameException(String queryName, String modelObject) {
        super(join("User request", queryName, "matches the name", modelObject, "of the model"),
            "Ensure uniqueness of names among all model objects (classes, interfaces, enums)");
    }
}
