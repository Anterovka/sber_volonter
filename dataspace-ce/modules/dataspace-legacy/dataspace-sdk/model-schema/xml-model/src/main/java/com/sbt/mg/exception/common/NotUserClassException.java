package com.sbt.mg.exception.common;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.exception.AnyPositionException;

public class NotUserClassException extends AnyPositionException {
    public NotUserClassException(XmlModelClass modelClass) {
        super(join("Class", modelClass.getName(), "is not a custom one. " +
                "The method can be applied only to custom classes."),
            "Transmit to the method a class with access = UPDATE");
    }
}
