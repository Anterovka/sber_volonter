package com.sbt.aggregator.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;

public class AbstractClassInControlException extends AggregateException {
    public AbstractClassInControlException(XmlModelClassProperty parentProperty) {
        super(join("Abstract class cannot participate in aggregating relationships in the model. Error in the class",
                parentProperty.getType(), "in which the property is used", XmlModelClassProperty.MAPPED_BY_TAG,
                "with the value", parentProperty.getName()),
            join("Move this property to the first non-abstract class and",
                "change the type of", propertyInCLass("a", parentProperty)));
    }
}
