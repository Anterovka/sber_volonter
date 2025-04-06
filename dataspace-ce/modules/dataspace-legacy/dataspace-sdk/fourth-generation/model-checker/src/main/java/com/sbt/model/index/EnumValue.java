package com.sbt.model.index;

import java.util.Arrays;

public class EnumValue<T extends Enum<?>> {

    private final Enum<?> anEnum;

    private EnumValue(T anEnum) {
        this.anEnum = anEnum;
    }

    public static <T extends Enum<?>> EnumValue<T> of(T anEnum) {
        return new EnumValue<>(anEnum);
    }

    @SafeVarargs
    public final boolean in(T... values) {
        return Arrays.stream(values).anyMatch(it -> it == this.anEnum);
    }
}
