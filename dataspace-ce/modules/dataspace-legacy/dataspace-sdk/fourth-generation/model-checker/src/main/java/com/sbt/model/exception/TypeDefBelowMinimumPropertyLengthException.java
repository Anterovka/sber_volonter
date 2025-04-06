package com.sbt.model.exception;

import com.sbt.mg.data.model.typedef.XmlTypeDef;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Collection;

import static com.sbt.mg.jpa.JpaConstants.MIN_LENGTH;

public class TypeDefBelowMinimumPropertyLengthException extends CheckXmlModelException {

    public TypeDefBelowMinimumPropertyLengthException(Collection<XmlTypeDef> belowMinimumLengthProperties) {
        super(join("The value of the attribute length is less than the minimum =", MIN_LENGTH, ". Error in type definitions",
                collectTypeDefNames(belowMinimumLengthProperties)),
            join("Increase the length"));
    }
}
