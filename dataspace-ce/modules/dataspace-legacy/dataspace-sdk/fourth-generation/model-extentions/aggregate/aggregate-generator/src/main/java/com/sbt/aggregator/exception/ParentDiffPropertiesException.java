package com.sbt.aggregator.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;

import java.util.List;

public class ParentDiffPropertiesException extends AggregateException {

    public ParentDiffPropertiesException(List<XmlModelClassProperty> addedParent,
                                         List<XmlModelClassProperty> droppedParent) {
        super(join("The backward compatibility is broken due to the divergence of the parent properties on the model.",
                "\n\tDivergence on properties:",
                "\n  Added ", addedParent,
                "\n  Removed ", droppedParent),
            "The properties cannot be changed.Return back the signs of parent = \"true\"");
    }
}
