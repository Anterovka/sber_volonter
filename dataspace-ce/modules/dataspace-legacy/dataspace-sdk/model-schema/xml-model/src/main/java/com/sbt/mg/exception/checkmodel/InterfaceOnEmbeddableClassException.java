package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.data.model.XmlModelClass;

import java.util.List;

public class InterfaceOnEmbeddableClassException extends CheckXmlModelException {
    public InterfaceOnEmbeddableClassException(List<XmlModelClass> classNames) {
        super(join("On classes marked with", XmlModelClass.EMBEDDED_TAG, ", it is forbidden to define interfaces: ", collectClasses(classNames)),
            join("Remove the tag ", XmlModelClass.IMPLEMENTS_TAG, ", or set the value of the tag ", XmlModelClass.EMBEDDED_TAG, "to false"));
    }
}
