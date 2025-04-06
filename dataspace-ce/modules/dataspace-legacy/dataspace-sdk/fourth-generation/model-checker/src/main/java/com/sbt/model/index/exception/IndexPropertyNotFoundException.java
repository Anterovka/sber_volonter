package com.sbt.model.index.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class IndexPropertyNotFoundException extends CheckXmlModelException {

    public IndexPropertyNotFoundException(String modelClass, String propertyName) {
        super(join("In the composition of class fields", modelClass,
                "the missing property", propertyName, "for the class", modelClass),
            join("Remove the undefined property from the index, declare the property or remove the index from the model."));
    }
}
