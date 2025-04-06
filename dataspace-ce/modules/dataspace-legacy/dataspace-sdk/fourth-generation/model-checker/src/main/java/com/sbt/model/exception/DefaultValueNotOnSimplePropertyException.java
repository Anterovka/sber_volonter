package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class DefaultValueNotOnSimplePropertyException extends CheckXmlModelException {
    public DefaultValueNotOnSimplePropertyException(XmlModelClassProperty property) {
        super(join("Default values can be set on primitive properties. Error in",
                propertyInCLass("е", property)),
            join("Remove the default value (attribute", XmlModelClassProperty.DEFAULT_VALUE_TAG, ")"));
    }
}
