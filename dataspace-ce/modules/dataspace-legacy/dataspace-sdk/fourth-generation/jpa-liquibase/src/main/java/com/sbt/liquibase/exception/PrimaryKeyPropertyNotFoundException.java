package com.sbt.liquibase.exception;

public class PrimaryKeyPropertyNotFoundException extends LiquibaseGenException {
    public PrimaryKeyPropertyNotFoundException(String className) {
        super(join("Unable to determine primary key property in class", className),
            "Contact the developers");
    }
}
