package com.sbt.model.exception;

import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.typedef.XmlTypeDef;
import com.sbt.mg.data.model.typedef.XmlTypeDefs;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class TypeDefTypeNotPrimitiveException extends CheckXmlModelException {
    public TypeDefTypeNotPrimitiveException(String typeDefName) {
        super(join(XmlTypeDefs.TYPE_DEF_TAG, "with the name", typeDefName, "contains the attribute", XmlTypeDef.TYPE_TAG,
                "the value of which is not a primitive"),
            join("Set the correct value from the list:", ModelHelper.TYPES_INFO.keySet()));
    }
}
