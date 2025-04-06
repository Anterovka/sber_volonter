package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.data.model.Property;
import com.sbt.mg.exception.AnyPositionException;

public class NonEmbeddableClassPropertyException extends AnyPositionException {
    public NonEmbeddableClassPropertyException(Property property) {
        super(join("The name of the property was passed to the function for obtaining the property value", property.getName(),
                "related class which is not embeddable. Related class:", property.getProperty().getName()),
            "Contact the developers");
    }
}
