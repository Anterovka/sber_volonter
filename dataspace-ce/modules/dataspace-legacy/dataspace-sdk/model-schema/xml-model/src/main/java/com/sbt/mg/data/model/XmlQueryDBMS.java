package com.sbt.mg.data.model;

public enum XmlQueryDBMS {
    ORACLE("oracle"),
    POSTGRESQL("postgresql"),
    H2("h2"),
    ANY(null);


    private final String value;

    XmlQueryDBMS(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
