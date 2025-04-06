package com.sbt.aggregator.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;

public class LinkBetweenAggregatesException extends AggregateException {
    public LinkBetweenAggregatesException(XmlModelClassProperty property) {
        super(join("References between two aggregates are prohibited. Study the documentation on building aggregates.",
                "Error in the property", property.getName(), "of the class", property.getModelClass().getName()),
            join("Make classes", property.getModelClass().getName(), "and", property.getType(),
                "part of one unit or refer to another unit through an external reference",
                "(please use the reference tag to link to another aggregate)."));
    }
}
