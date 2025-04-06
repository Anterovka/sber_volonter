package com.sbt.model.exception;

import com.sbt.mg.data.model.typedef.XmlTypeDef;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class TypeDefNameEqualToEntityNameException extends CheckXmlModelException {
    protected TypeDefNameEqualToEntityNameException(String typeDefName, String entityTypeStr) {
        super(join("Name of attribute", XmlTypeDef.NAME_TAG, "intersects with the name", entityTypeStr, "from model. Error in name",
            typeDefName), "Fix it to a unique name");
    }
}
