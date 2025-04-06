package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class MappedByOnCollectionAndSimplePropertyException extends CheckXmlModelException {

    public MappedByOnCollectionAndSimplePropertyException(XmlModelClassProperty collectionProperty,
                                                          XmlModelClassProperty simpleProperty) {
        super(String.format("For the collection property %s and simple %s class %s, one type (%s) and one field (%s) are set" +
                    " as layout (mappedBy)",
                collectionProperty.getName(),
                simpleProperty.getName(),
                collectionProperty.getModelClass().getName(),
                collectionProperty.getType(),
                collectionProperty.getMappedBy()),
            "The markup of the child class reference cannot be the same for simple and collection fields.");
    }
}
