package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.AnyPositionException;

public class NoFoundEmbeddablePropertyException extends AnyPositionException {
    public NoFoundEmbeddablePropertyException(String embeddablePropertyName, XmlModelClassProperty embeddedProperty) {
        super(join("Not found property", embeddablePropertyName,
                "in the embedded list, the property's name is", embeddedProperty.getName(),
                "in the class", embeddedProperty.getModelClass().getName()),
            "Contact the developers of the module");
    }
}
