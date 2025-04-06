package com.sbt.mg.exception.common;

import com.sbt.mg.exception.AnyPositionException;

public class EmbeddedListNotFoundException extends AnyPositionException {
    public EmbeddedListNotFoundException(String propertyName, String className) {
        super(join("For the property with the name", propertyName, "an embedded element was not found in the class", className),
            "Show the error to developers. Attach the model (model.xml) and the current state of the model (pdm.xml)");
    }
}
