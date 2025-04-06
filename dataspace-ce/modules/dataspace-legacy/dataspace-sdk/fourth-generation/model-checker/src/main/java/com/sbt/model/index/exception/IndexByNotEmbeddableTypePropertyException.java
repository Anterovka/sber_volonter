package com.sbt.model.index.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class IndexByNotEmbeddableTypePropertyException extends CheckXmlModelException {

    public IndexByNotEmbeddableTypePropertyException(String modelClass, String propertyName) {
        super(join("Composite index of class", modelClass, "can consist only of primitive fields of class " +
                    "and fields of embedded classes with indication of their fields through '.', however,"
                , "an attempt to specify for indexing a field", propertyName, "of a type that is not an embeddable class."),
            join(" Remove the field ", propertyName, " from the compound index."));
    }
}
