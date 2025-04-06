package com.sbt.model.exception;

import com.sbt.mg.data.model.typedef.XmlTypeDef;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Collection;

import static com.sbt.mg.jpa.JpaConstants.MAX_DATE_LENGTH;

public class TypeDefDateFieldException extends CheckXmlModelException {

    public TypeDefDateFieldException(Collection<XmlTypeDef> typeDefs) {
        super(join("For type definitions based on date types, the length cannot be set to more than", MAX_DATE_LENGTH,
                "or less than 1 (except for LocalDateTime and OffsetDateTime, which allow length = 0, meaning time without milliseconds). The error is in the definitions", collectTypeDefNames(typeDefs)),
            join("Set length according to requirements."));
    }
}
