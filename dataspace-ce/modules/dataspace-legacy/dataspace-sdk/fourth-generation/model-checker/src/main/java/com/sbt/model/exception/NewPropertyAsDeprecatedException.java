package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class NewPropertyAsDeprecatedException extends CheckXmlModelException {

    public NewPropertyAsDeprecatedException(XmlModelClassProperty deprecatedProperty, XmlModelClassProperty newProperty) {
        super(join("The backward compatibility is broken. Creating equivalent names (case is not taken into account) for properties is prohibited",
                "Earlier in the class", deprecatedProperty.getModelClass().getName(),
                "was defined and deleted property with name", deprecatedProperty.getName(),
                ", however, an identical property is defined", newProperty.getName(),
                "in the class", newProperty.getModelClass().getName()),
            join("Change the property name", newProperty.getName(),
                "in the class", newProperty.getModelClass().getName()));
    }
}
