package com.sbt.model.exception.diff;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class UnicodeStringToStringModificationException extends CheckXmlModelException {

    public UnicodeStringToStringModificationException(XmlModelClassProperty property) {
        super(join("In", propertyInCLass("e", property), "the type has been changed from UnicodeString to String. If this field contains Cyrillic characters, information loss is possible!"),
            join("Increase the length parameter to at least half", property.getLength() * 2));
    }
}
