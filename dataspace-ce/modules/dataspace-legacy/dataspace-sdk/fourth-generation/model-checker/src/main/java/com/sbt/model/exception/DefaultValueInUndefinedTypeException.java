package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class DefaultValueInUndefinedTypeException extends CheckXmlModelException {
    public DefaultValueInUndefinedTypeException(XmlModelClassProperty property) {
        super(join("Unprocessed type for applying default value.", propertyInCLass("o", property)),
            "The default values are described for a limited number of types. Please refer to the documentation");
    }
}
