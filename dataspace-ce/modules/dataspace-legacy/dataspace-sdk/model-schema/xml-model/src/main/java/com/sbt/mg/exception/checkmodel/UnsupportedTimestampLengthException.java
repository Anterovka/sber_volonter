package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.data.model.XmlModelClassProperty;

public class UnsupportedTimestampLengthException extends CheckXmlModelException {

    public UnsupportedTimestampLengthException(XmlModelClassProperty property) {
        super(join("Error in property", property.getName(),
                "class", property.getModelClass().getName(), ".Value length", property.getLength(), "is not supported"),
            join("Change the value of the attribute length to a valid one - from 1 to 6 (inclusive)"));
    }
}
