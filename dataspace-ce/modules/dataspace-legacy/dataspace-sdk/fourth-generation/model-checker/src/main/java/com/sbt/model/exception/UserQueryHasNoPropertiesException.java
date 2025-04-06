package com.sbt.model.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class UserQueryHasNoPropertiesException extends CheckXmlModelException {

    public UserQueryHasNoPropertiesException(String queryName) {
        super(join("In the user request", queryName, "no property tags were found"),
            "Specify at least one property tag");
    }
}
