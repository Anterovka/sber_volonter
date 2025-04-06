package com.sbt.model.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.List;
import java.util.Map;

public class PluginPropertyChangedException extends CheckXmlModelException {
    public PluginPropertyChangedException(String propertyName, String oldValue, String newValue) {

        super(makeMessage(propertyName, oldValue, newValue), "Return the setting to its original state.");
    }

    private static String makeMessage(String propertyName, String oldValue, String newValue) {
        return String.format(
            "The plugin property %s is not allowed to be changed. The old value was '%s', the new one is '%s'.",
            propertyName,
            oldValue,
            newValue
        );
    }
}
