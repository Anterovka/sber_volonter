package com.sbt.aggregator.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;

import java.util.Collection;

public class MarkedAsMappedByWithoutParentPropertyException extends AggregateException {

    public MarkedAsMappedByWithoutParentPropertyException(XmlModelClass modelClass, Collection<XmlModelClassProperty> collectionWithTypeToItself,
                                                          Collection<XmlModelClassProperty> parentMarkedProperties) {
        super(join("On class " + modelClass + " found " + collectionWithTypeToItself.size()
                + "The collection with the type of this class. Properties described in mappedBy do not have the parent attribute."),
            join("It is necessary to determine which of the properties", collectClassProperties(parentMarkedProperties),
                "must have a parent flag"));
    }
}
