package com.sbt.aggregator.exception;

public class RootParentClassCircleException extends AggregateException {
    public RootParentClassCircleException(String className, String propertyName) {
        super(join("It is forbidden to create aggregate or abstract root classes,",
                "property with parent = \"true\" which forms a loop.",
                "Error in property with name", propertyName, "of class", className),
            join("Add a class", className,
                "in the composition of another aggregate (create one more property with the attribute parent = \"true\"",
                "or remove the parent attribute from the property", propertyName));
    }
}
