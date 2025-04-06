package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.GeneralSdkException;
import com.sbt.model.exception.parent.CheckModelException;

import java.util.Collection;

public class ManyToManyNotSupportedException extends CheckModelException {
    public ManyToManyNotSupportedException(String className, Collection<XmlModelClassProperty> properties) {
        super(GeneralSdkException.join("Collection ManyToMany is not supported. In the class", className, "properties with the collection attribute without mappedBy were found.",
                "Error in properties", GeneralSdkException.collectClassProperties(properties)),
            GeneralSdkException.join("Create an entity that links classes, or use mappedBy to create a OneToMany relationship"));
    }
}
