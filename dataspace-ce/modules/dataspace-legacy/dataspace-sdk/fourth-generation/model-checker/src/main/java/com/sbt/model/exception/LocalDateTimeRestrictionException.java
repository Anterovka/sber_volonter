package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class LocalDateTimeRestrictionException extends CheckXmlModelException {
    public LocalDateTimeRestrictionException(XmlModelClassProperty property) {
super(join("For LocalDateTime type other default values except now are not allowed. Error in",
                propertyInCLass("е", property)),
                "Установите значение now");
    }
}
