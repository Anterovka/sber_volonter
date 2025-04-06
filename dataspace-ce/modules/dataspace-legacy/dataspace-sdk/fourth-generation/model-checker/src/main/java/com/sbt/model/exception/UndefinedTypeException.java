package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.GeneralSdkException;
import com.sbt.model.exception.parent.TestGeneratorException;

public class UndefinedTypeException extends TestGeneratorException {
    public UndefinedTypeException(XmlModelClassProperty property) {
        super(GeneralSdkException.join("While processing", GeneralSdkException.propertyInCLass("а", property),
                "the type handler could not be determined", property.getType()),
            "Check the correctness of the type.");
    }
}
