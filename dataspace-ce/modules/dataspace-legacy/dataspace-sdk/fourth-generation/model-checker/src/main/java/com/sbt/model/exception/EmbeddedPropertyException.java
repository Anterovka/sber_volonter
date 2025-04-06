package com.sbt.model.exception;

import com.sbt.mg.exception.GeneralSdkException;
import com.sbt.model.exception.parent.CheckModelException;

public class EmbeddedPropertyException extends CheckModelException {

    public EmbeddedPropertyException(String propertyName) {
        super(GeneralSdkException.join("Forbidden to specify the attribute embedded on the property",
                propertyName,
                "the type of which is not an embeddable class"),
                "Add the embeddable attribute to the class.");
    }
}
