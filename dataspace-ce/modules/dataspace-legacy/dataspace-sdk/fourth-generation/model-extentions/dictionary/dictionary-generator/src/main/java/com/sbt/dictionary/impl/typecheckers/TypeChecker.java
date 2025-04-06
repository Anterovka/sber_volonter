package com.sbt.dictionary.impl.typecheckers;

import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.XmlModelClassProperty;

import java.util.Locale;
import java.util.Objects;

public abstract class TypeChecker {

    protected static int getPropertyLength(XmlModelClassProperty property) {
        if (!Objects.isNull(property.getLength())) {
            return property.getLength();
        }
        return ModelHelper.TYPES_INFO.get(property.getType().toLowerCase(Locale.ENGLISH)).getFirstNumber();
    }

    /**
     * Extraction of non-null exception message.
     * @param exception
     * @return the first non-null message, or nul, if all messages in the hierarchy are null.
     */
    protected static String getMessage(Exception exception) {
        Throwable currentThrowable = exception;
        int threshold = 100; // Защита от зацикливания
        int counter = 0;
        while (currentThrowable != null && currentThrowable.getMessage() == null && counter < threshold) {
            currentThrowable = currentThrowable.getCause();
            counter++;
        }

        if (currentThrowable == null) {
            return null;
        }
        return currentThrowable.getMessage();
    }

    public abstract void check(Object value);

}
