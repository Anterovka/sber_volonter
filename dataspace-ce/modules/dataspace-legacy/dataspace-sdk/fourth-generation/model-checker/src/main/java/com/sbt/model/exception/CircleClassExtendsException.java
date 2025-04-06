package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Set;

public class CircleClassExtendsException extends CheckXmlModelException {
    public CircleClassExtendsException(Set<XmlModelClass> classes) {
        super(join("Noticed loop inheritance by attribute", XmlModelClass.EXTENDS_TAG,
                "for classes: ", collectClasses(classes)),
            "It is necessary to configure the inheritance model correctly for the classes from the list described above.");
    }
}
