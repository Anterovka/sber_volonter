package com.sbt.model.exception;

import com.sbt.mg.data.model.interfaces.XmlProperty;
import com.sbt.model.exception.parent.CheckModelException;

import java.util.List;

public class NotDeprecatedPropertyWithDeprecatedTypeException extends CheckModelException {
    public NotDeprecatedPropertyWithDeprecatedTypeException(List<XmlProperty> propsAndRefs) {
        super(join("The following elements are not deprecated (the flag is not set), but refer to deprecated classes:\n", propsAndRefs),
            "The element or its classes must have the isDeprecated = \"true\"flag set, or the corresponding flag must be removed from the types.");
    }
}
