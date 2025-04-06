package com.sbt.parameters.enums;

public enum DBMS {

    ORACLE("oracle"),
    NO_ORACLE("!oracle"),
    POSTGRES("postgresql"),
    H2("h2"),
    ANY(null);


    private final String liquibaseValue;

    DBMS(String liquibaseValue) {
        this.liquibaseValue = liquibaseValue;
    }

    public String getLiquibaseValue() {
        return liquibaseValue;
    }
}
