package com.sbt.model.exception.diff;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.model.exception.parent.CheckModelException;

public class AddParentException extends CheckModelException {
    public AddParentException(XmlModelClassProperty modelClassProperty) {
        super(join("Inclusion of a class in an aggregate is not a backward compatible change.",
                "The error was found in the class", modelClassProperty.getModelClass().getName(), ". A field has been added",
                modelClassProperty.getName()),
            "Necessary to remove the field with a link to the aggregate(parent = \"true\").");
    }
}
