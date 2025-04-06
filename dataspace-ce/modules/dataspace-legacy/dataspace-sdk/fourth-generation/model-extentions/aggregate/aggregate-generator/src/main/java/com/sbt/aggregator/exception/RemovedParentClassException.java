package com.sbt.aggregator.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;

import java.util.List;
import java.util.stream.Collectors;

public class RemovedParentClassException extends AggregateException {
    public RemovedParentClassException(List<XmlModelClassProperty> changedParent) {
        super(join("The class has been deleted, which had the properties parent = \"true\"", changedParent)
            , join("Верните класс обратно в model" + changedParent.stream()
                .map(XmlModelClassProperty::getType).collect(Collectors.joining(", "))));
    }
}
