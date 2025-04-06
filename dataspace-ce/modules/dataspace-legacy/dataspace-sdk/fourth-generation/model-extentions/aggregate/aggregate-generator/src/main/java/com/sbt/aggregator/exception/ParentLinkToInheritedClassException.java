package com.sbt.aggregator.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;

import java.util.Collection;

public class ParentLinkToInheritedClassException extends AggregateException {
    public ParentLinkToInheritedClassException(String classParent, Collection<XmlModelClassProperty> properties) {
        super(join("Declaring properties with the type of the same class is an erroneous situation.",
                "Error occurred in the class", classParent, " as properties are defined within it.",
                collectClassProperties(properties), "the types of which are included in the inheritance hierarchy with the class", classParent),
            join("Измените тип", XmlModelClass.PROPERTY_TAG, "на", XmlModelClass.REFERENCE_TAG,
                "или удалите ссылку"));
    }
}
