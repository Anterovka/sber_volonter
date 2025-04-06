package com.sbt.model.exception;

import com.sbt.mg.data.model.typedef.XmlTypeDef;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class TypeDefEqualToPrimitiveException extends CheckXmlModelException {
    public TypeDefEqualToPrimitiveException(String typeDefName) {
        super(join("Name of attribute", XmlTypeDef.NAME_TAG, "intersects with the name of primitive. Error in name",
            typeDefName), "Fix the name");
    }
}
