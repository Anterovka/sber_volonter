package com.sbt.model.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Collection;

public class ExtendedPropertyIntersectionException extends CheckXmlModelException {
    public ExtendedPropertyIntersectionException(String className, Collection<String> properties) {
        super(join("Properties have been found whose names have already been declared in the parent classes of the ", className,
                ". Intersecting properties:", String.join(",", properties)),
            "Eliminate property name collisions");
    }
}
