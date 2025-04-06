package com.sbt.reference.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.interfaces.ReferenceFromXml;

public class ReferenceToEmbeddableCLassException extends ExternalReferenceGeneratorException {
    public ReferenceToEmbeddableCLassException(XmlModelClass modelClass, ReferenceFromXml reference) {
        super(join("In the class", modelClass.getName(), "an external link is defined", reference.getName(),
                "on the embeddable model class. The class", reference.getType(), "has the attribute", XmlModelClass.EMBEDDED_TAG),
            "Correct the type of external reference to the nonEmbeddable class");
    }
}
