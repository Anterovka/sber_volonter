package com.sbt.liquibase.exception;

import com.sbt.mg.exception.GeneralSdkException;

public class LiquibaseGenException extends GeneralSdkException {

    public LiquibaseGenException() {
        super();
    }

    public LiquibaseGenException(String message, String solution) {
        super(message, solution);
    }

    @Override
    public String getPosition() {
        return "generation of changelog.xml";
    }
}

