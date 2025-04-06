package com.sbt.liquibase.exception;

public class ColumnNotDefinedException extends LiquibaseGenException {
    public ColumnNotDefinedException(String message) {
        super(message, "Contact the developers");
    }
}
