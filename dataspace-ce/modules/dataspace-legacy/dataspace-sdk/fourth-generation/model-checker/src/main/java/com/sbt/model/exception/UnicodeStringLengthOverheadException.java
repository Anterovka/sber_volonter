package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;
import com.sbt.mg.jpa.JpaConstants;

import java.util.Collection;

public class UnicodeStringLengthOverheadException extends CheckXmlModelException {
    public UnicodeStringLengthOverheadException(XmlModelClass modelClass,
                                                Collection<XmlModelClassProperty> overheadUnicodeStringProperties) {
        super(join("Exceeding the length of a Unicode string in", JpaConstants.MAX_UNICODE_STRING_LENGTH, "characters. Error in properties",
                collectClassProperties(overheadUnicodeStringProperties), "class", modelClass.getName()),
            join("Use Text instead of UnicodeString type"));
    }
}
