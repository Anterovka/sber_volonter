package com.sbt.model.exception;

import com.sbt.mg.data.model.typedef.XmlTypeDef;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;
import com.sbt.mg.jpa.JpaConstants;

import java.util.Collection;

public class TypeDefStringLengthOverheadException extends CheckXmlModelException {
    public TypeDefStringLengthOverheadException(Collection<XmlTypeDef> typeDefs) {
        super(join("Exceeding the length of the string in", JpaConstants.MAX_STRING_LENGTH, "characters. Error in type definitions:",
                collectTypeDefNames(typeDefs)),
            join("Use Text type instead of String"));
    }
}
