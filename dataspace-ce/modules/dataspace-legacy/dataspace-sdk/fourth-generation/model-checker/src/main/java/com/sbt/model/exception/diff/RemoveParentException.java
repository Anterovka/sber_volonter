package com.sbt.model.exception.diff;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.model.exception.parent.CheckModelException;

public class RemoveParentException extends CheckModelException {
    public RemoveParentException(XmlModelClassProperty modelClassProperty) {
        super(join("Exception class removal from aggregate is not backward compatible change.",
                "The error was found in the class", modelClassProperty.getModelClass().getName(), ". The field was deleted",
                modelClassProperty.getName()),
            "Necessary to return the field with reference to the aggregate (parent = \"true\"). Renaming the property is tantamount to deleting it.");
    }
}
