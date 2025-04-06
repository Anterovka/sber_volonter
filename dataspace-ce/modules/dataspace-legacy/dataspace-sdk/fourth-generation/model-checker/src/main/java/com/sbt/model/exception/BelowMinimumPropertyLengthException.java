package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Collection;

import static com.sbt.mg.jpa.JpaConstants.MIN_LENGTH;

public class BelowMinimumPropertyLengthException extends CheckXmlModelException {

    public BelowMinimumPropertyLengthException(XmlModelClass modelClass,
                                               Collection<XmlModelClassProperty> belowMinimumLengthProperties) {
        super(join("The value of the attribute length is less than the minimum =", MIN_LENGTH, ". Error in properties",
                collectClassProperties(belowMinimumLengthProperties), "класса", modelClass.getName()),
            join("Increase the length"));
    }
}
