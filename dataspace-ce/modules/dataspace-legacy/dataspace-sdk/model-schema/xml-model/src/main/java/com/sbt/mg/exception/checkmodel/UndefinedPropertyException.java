package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.exception.AnyPositionException;

public class UndefinedPropertyException extends AnyPositionException {
    public UndefinedPropertyException(XmlModelClass modelClass, String propertyName) {
        super(join("Property not found", propertyName,
                "in the class", modelClass.getName()),
            join("Specify the correct class that has the property", propertyName,
                "or specify your own property from the list", collectClassProperties(modelClass.getPropertiesAsList())));
    }
}
