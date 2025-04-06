package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class OneToOneWithoutParentException extends CheckXmlModelException {
    public OneToOneWithoutParentException(XmlModelClassProperty property) {
        super(join("Declare OneToOne links with the tag ", XmlModelClassProperty.MAPPED_BY_TAG,
                "allowed jointly with parent = \"true\" and unique = \"true\". Error in", propertyInCLass("е", property),
                "since the property  ", property.getMappedBy(), " of the class",
                property.getType(), "does not contain the attribute parent = \"true\" and unique = \"true\""),
            "Remove the mappedBy attribute or add parent = \"true\" or add uniqueness unique = \"true\"");
    }
}
