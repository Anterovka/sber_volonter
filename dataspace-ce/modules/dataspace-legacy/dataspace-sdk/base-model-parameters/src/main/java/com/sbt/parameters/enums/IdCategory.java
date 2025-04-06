package com.sbt.parameters.enums;

public enum IdCategory {
    SNOWFLAKE,
    MANUAL,
    NO_ID,
    AUTO_ON_EMPTY,
    UUIDV4,
    UUIDV4_ON_EMPTY;

    public boolean isGeneratedAlways() {
        return this == SNOWFLAKE || this == UUIDV4;
    }

    public boolean isGeneratedOnEmpty() {
        return this == AUTO_ON_EMPTY || this == UUIDV4_ON_EMPTY;
    }

}
