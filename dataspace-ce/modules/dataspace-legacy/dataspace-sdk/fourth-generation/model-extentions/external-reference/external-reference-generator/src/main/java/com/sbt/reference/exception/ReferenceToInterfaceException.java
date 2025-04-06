package com.sbt.reference.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.interfaces.ReferenceFromXml;

public class ReferenceToInterfaceException extends ExternalReferenceGeneratorException {
    public ReferenceToInterfaceException(XmlModelClass modelClass, ReferenceFromXml reference) {
        super(join("In the class", modelClass.getName(), "an external link is defined", reference.getName(),
                "on the model interface."),
            "Correct the type of external reference to interface implementation");
    }
}
