package com.sbt.aggregator.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;

public class LinkBetweenAggregatesEmbeddableException extends AggregateException {
    public LinkBetweenAggregatesEmbeddableException(XmlModelClassProperty property,
                                                    XmlModelClassProperty propertyInEmbeddable,
                                                    XmlModelClass classOwner) {
        super(join("Links between two aggregates are prohibited.",
                "In the class", property.getModelClass().getName(), "is defined a property named", property.getName(),
                "type", property.getType(), ". Type", property.getType(), "contains a property",
                propertyInEmbeddable.getName(), "type", propertyInEmbeddable.getType(),
                "Classes", property.getModelClass().getName(), "and", classOwner.getName(),
                "belong to different aggregates. The process of building aggregates is studied in the documentation."),
            join("Link classes", property.getModelClass().getName(), "and", classOwner.getName(),
                "in one aggregate or use the reference tag for the property", propertyInEmbeddable.getName()));
    }
}
