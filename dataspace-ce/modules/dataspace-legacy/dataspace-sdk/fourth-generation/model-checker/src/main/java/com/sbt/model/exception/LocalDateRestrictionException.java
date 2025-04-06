package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class LocalDateRestrictionException extends CheckXmlModelException {
    public LocalDateRestrictionException(XmlModelClassProperty property) {
        super(join("For type LocalDate, other default values besides now are not allowed. Error in",
                propertyInCLass("е", property)),
            "Set the value now");
    }
}
