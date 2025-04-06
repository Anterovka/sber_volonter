package com.sbt.model.exception;

import com.sbt.mg.data.model.typedef.XmlTypeDef;
import com.sbt.mg.exception.GeneralSdkException;
import com.sbt.model.exception.parent.CheckModelException;

import java.util.Collection;

public class WrongScaledTypeDefException extends CheckModelException {

    public WrongScaledTypeDefException(Collection<XmlTypeDef> typeDefs) {
        super(GeneralSdkException.join("The tag scale can be specified only for the type BigDecimal.",
                "Error in type definitions:", collectTypeDefNames(typeDefs)),
            "Remove the scale attribute from the listed definitions.");
    }
}
