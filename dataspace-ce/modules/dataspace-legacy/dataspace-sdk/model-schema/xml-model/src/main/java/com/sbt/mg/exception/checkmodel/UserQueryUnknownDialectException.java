package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class UserQueryUnknownDialectException extends CheckXmlModelException {

    public UserQueryUnknownDialectException(String queryName, String dialect) {
        super(join("In the user request", queryName, "an unknown dialect is specified",
                dialect),
            "Ensure unique values of dialect among all tags of the SQL query");
    }
}
