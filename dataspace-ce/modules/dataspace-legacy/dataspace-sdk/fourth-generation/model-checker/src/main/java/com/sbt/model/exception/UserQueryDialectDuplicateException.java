package com.sbt.model.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class UserQueryDialectDuplicateException extends CheckXmlModelException {

    public UserQueryDialectDuplicateException(String queryName, String dialects) {
        super(join("In the user request", queryName, "several tags with the same dialects were found:",
                dialects),
            "Ensure unique values of dialect among all bodies");
    }
}
