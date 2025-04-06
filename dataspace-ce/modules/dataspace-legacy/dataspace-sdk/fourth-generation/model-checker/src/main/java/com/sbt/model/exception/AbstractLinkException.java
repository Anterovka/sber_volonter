package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Collection;

public class AbstractLinkException extends CheckXmlModelException {
    public AbstractLinkException(String className, Collection<XmlModelClassProperty> properties) {
        super(join("Models do not allow links to abstract classes. Error in properties",
                collectClassProperties(properties), "of class", className,
                "Referencing is possible only for classes without the abstract tag", XmlModelClass.ABSTRACT_TAG),
            join("change the types to non-abstract classes in the properties"));
    }
}
