package com.sbt.aggregator.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;

public class ParentLinkTypeNotBaseClassException extends AggregateException {
    public ParentLinkTypeNotBaseClassException(XmlModelClassProperty property) {
        super(join("Error occurred on", propertyInCLass("e", property),
                ", since properties with the parent = \"true\" flag are not allowed to reference model classes that do not have all their ancestors as abstract",
                "(have the characteristic", XmlModelClass.ABSTRACT_TAG, ")."),
            join("It is necessary to correct the link to the class (so that all its ancestors have the attribute parent = \"true\")",
                "or delete the parent attribute on the class property"));
    }
}
