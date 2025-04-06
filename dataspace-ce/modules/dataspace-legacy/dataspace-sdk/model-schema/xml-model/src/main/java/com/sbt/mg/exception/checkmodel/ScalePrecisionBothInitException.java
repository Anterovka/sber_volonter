package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.data.model.XmlModelClassProperty;

public class ScalePrecisionBothInitException extends CheckXmlModelException {

    public ScalePrecisionBothInitException(String propertyName) {
        super(join("It is forbidden to define two attributes simultaneously", XmlModelClassProperty.PRECISION_TAG,
                "and", XmlModelClassProperty.SCALE_TAG, ". Error in property", propertyName),
            join("It is necessary to use", XmlModelClassProperty.SCALE_TAG));
    }
}
