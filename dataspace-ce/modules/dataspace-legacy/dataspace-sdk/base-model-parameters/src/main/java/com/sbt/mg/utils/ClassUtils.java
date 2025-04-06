package com.sbt.mg.utils;

import com.sbt.mg.jpa.JpaConstants;

import java.util.Objects;

public class ClassUtils {

    private ClassUtils() {}

    public static boolean isFirstUserClass(String extendedClassName) {
        return isBaseClass(extendedClassName) ||
                JpaConstants.BASE_EVENT_NAME.equals(extendedClassName) ||
                JpaConstants.BASE_MERGE_EVENT_NAME.equals(extendedClassName);
    }

    public static boolean isBaseClass(String name) {
        return Objects.equals(JpaConstants.ENTITY_CLASS_NAME, name);
    }
}
