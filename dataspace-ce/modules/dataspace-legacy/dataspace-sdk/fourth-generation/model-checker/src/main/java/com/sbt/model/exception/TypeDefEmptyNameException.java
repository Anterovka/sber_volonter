package com.sbt.model.exception;

import com.sbt.mg.data.model.typedef.XmlTypeDef;
import com.sbt.mg.data.model.typedef.XmlTypeDefs;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class TypeDefEmptyNameException extends CheckXmlModelException {
    public TypeDefEmptyNameException() {
        super(join("In the model is described", XmlTypeDefs.TYPE_DEF_TAG, "with an empty value in the attribute",
                XmlTypeDef.NAME_TAG),
            "Set the value");
    }
}
