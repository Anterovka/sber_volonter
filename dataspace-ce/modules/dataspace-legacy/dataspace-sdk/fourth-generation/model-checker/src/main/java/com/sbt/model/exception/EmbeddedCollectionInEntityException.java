package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Collection;

public class EmbeddedCollectionInEntityException extends CheckXmlModelException {
    public EmbeddedCollectionInEntityException(String name, Collection<String> properties) {
        super(join("Embedded properties with the tag", XmlModelClassProperty.COLLECTION_TAG,
                "on properties", properties, "of class/interface", name),
            join("Collection of embeddable can be implemented by defining a new class in the model with the property",
                "нужного вам Embeddable типа"));
    }
}
