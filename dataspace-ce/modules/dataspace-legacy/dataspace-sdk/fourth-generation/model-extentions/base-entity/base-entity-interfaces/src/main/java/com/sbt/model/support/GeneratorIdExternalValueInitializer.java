package com.sbt.model.support;

public interface GeneratorIdExternalValueInitializer {

    default Object $readGeneratorIdExternalValue() {
        return null;
    }

    default void $writeGeneratorIdExternalValue(Object value) {
        /* no-op */
    }

}
