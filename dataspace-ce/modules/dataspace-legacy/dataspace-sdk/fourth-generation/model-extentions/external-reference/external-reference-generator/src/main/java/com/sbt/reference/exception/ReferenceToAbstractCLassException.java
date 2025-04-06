package com.sbt.reference.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.interfaces.ReferenceFromXml;

public class ReferenceToAbstractCLassException extends ExternalReferenceGeneratorException {
    public ReferenceToAbstractCLassException(XmlModelClass modelClass, ReferenceFromXml reference) {
        super(join("In the class", modelClass.getName(), "an external link is defined", reference.getName(),
                "on abstract model class. Class", reference.getType(), "has sign", XmlModelClass.ABSTRACT_TAG),
            "Fix the type of external reference to a non-abstract class");
    }
}
