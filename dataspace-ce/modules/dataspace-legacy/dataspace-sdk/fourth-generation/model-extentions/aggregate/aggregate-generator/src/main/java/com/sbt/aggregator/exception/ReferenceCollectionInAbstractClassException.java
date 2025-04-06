package com.sbt.aggregator.exception;

import com.sbt.mg.data.model.XmlModelClassReference;

public class ReferenceCollectionInAbstractClassException extends AggregateException {

    public ReferenceCollectionInAbstractClassException(XmlModelClassReference xmlModelClassReference) {
        super(join("Abstract class cannot contain reference collections. Error in the class",
                xmlModelClassReference.getModelClass().getName(), "in the property ", xmlModelClassReference.getName()),
            join("Move this property to the first non-abstract class "));
    }
}
