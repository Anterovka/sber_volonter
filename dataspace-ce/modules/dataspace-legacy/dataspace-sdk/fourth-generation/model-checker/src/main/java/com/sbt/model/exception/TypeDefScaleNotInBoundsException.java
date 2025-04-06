package com.sbt.model.exception;

import com.sbt.mg.data.model.typedef.XmlTypeDef;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class TypeDefScaleNotInBoundsException extends CheckXmlModelException {
    public TypeDefScaleNotInBoundsException(XmlTypeDef typeDef) {
        super(join("The scale of the scale field of the BigDecimal type is not correct. Error in determining the type", typeDef.getName()),
            join("The value of scale must be greater than or equal to zero and less than the length (length) of the type.(scale =",
                typeDef.getScale(), ", length =", typeDef.getLength(), ")"));
    }
}
