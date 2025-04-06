package com.sbt.model.exception.diff;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.model.exception.parent.CheckModelException;

public class ParentPropertyCanNotBeDeprecatedException extends CheckModelException {
    public ParentPropertyCanNotBeDeprecatedException(XmlModelClassProperty modelClassProperty) {
        super(String.format("Property forming an aggregate relationship - %s.%s cannot be deleted and cannot be deprecated",
                modelClassProperty.getModelClass().getName(), modelClassProperty.getName()),
            "Necessary to return a property in model.xml and/or remove the attribute isDeprecated=\"true\" from it");
    }
}
