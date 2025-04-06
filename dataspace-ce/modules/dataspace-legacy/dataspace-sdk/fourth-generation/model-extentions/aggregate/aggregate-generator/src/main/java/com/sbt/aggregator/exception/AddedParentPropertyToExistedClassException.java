package com.sbt.aggregator.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;

import java.util.List;

public class AddedParentPropertyToExistedClassException extends AggregateException {

    public AddedParentPropertyToExistedClassException(List<XmlModelClassProperty> newParentProperties) {
        super(join("The backward compatibility is broken when trying to add new properties,",
                "помеченные признаком parent=\"true\"",
                newParentProperties, " to the already described classes in the model"),
            "Remove the parent attribute or order customization as a migration to add to the aggregates tree");
    }
}
