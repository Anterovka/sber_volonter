package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.exception.AnyPositionException;

public class NoFoundEmbeddedPropertyException extends AnyPositionException {
    public NoFoundEmbeddedPropertyException(String property, XmlModelClass modelClass) {
        super(join("Property not found in embedded list with name", property, "in class",
                modelClass),
            "Contact the developers of the module");
    }
}
