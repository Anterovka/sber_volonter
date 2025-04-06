package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.GeneralSdkException;
import com.sbt.model.exception.parent.CheckModelException;

import java.util.Collection;

public class UnsupportedTypeInEmbeddableException extends CheckModelException {
    public UnsupportedTypeInEmbeddableException(Collection<XmlModelClassProperty> properties, XmlModelClass modelClass) {
        super(GeneralSdkException.join("In embeddable classes, using any kind of collections is prohibited. Error in class",
                modelClass.getName(), "in properties", GeneralSdkException.collectClassProperties(properties)),
            "Move the property to the class that uses this embeddable class");
    }
}
