package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class UnsupportedLinkTypeException extends CheckXmlModelException {
    public UnsupportedLinkTypeException(XmlModelClassProperty property) {
        super(join("Unsupported reference relationship type to class with composite key.",
                "Error in", propertyInCLass("e", property)),
            "Contact the developers");
    }
}
