package com.sbt.aggregator.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;

public class TwoParentOnDifferentTypeException extends AggregateException {

    public TwoParentOnDifferentTypeException(String className, XmlModelClassProperty firstParent, XmlModelClassProperty secondParent) {
        super(join("In the class", className, "there are two properties [", firstParent.getName(), ',',
                secondParent.getName(), "] with the parent flag=\"true\".",
                "The error is that when defining two properties with parent, one of them must be of class type", className,
                ", while the found properties refer to ", firstParent.getType(), " and ", secondParent.getType()),
            join("It is necessary to determine which of the properties ", firstParent.getName(),
                "or", secondParent.getName(), "should remain with the parent flag"));
    }
}
