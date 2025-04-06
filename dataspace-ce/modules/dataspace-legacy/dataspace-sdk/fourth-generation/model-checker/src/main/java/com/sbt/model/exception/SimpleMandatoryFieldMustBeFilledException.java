package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.GeneralSdkException;
import com.sbt.model.exception.parent.CheckModelException;

public class SimpleMandatoryFieldMustBeFilledException extends CheckModelException {
    public SimpleMandatoryFieldMustBeFilledException(XmlModelClassProperty modelClassProperty) {
        super(GeneralSdkException.join("Required property", modelClassProperty, "has no default value default-value"),
            "On the primitive required attributes, set the default value according to the documentation.");
    }
}
