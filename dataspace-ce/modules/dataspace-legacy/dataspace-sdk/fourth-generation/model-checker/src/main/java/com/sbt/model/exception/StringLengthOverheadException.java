package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;
import com.sbt.mg.jpa.JpaConstants;

import java.util.Collection;

public class StringLengthOverheadException extends CheckXmlModelException {
    public StringLengthOverheadException(XmlModelClass modelClass,
                                         Collection<XmlModelClassProperty> overheadStringProperties) {
        super(join("Exceeding the length of the string in", JpaConstants.MAX_STRING_LENGTH, "characters. Error in properties",
                collectClassProperties(overheadStringProperties), "class", modelClass.getName()),
            join("Use Text type instead of String"));
    }
}
