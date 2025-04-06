package com.sbt.parameters.enums;

public enum Changeable {
    /** service field or class, not accessible to the user */
    SYSTEM,
    /** Generated property. The user can only read it, but not write to it */
    READ_ONLY,
    /** Property generated once when the entity is created. The consumer can only read it. */
    CREATE,
    /** Usually a custom property, can be read and written by the consumer */
    UPDATE
}
