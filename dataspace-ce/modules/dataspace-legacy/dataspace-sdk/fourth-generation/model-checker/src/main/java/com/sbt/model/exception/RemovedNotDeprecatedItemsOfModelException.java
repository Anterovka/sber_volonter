package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.List;

public class RemovedNotDeprecatedItemsOfModelException extends CheckXmlModelException {

    public RemovedNotDeprecatedItemsOfModelException(List<XmlModelClass> xmlModelClasses, List<XmlModelClassProperty> xmlModelClassProperties) {
        super(join("In the model, elements that were not previously declared as deprecated have been removed, which will result in a violation of backward compatibility of the API:",
                xmlModelClasses.isEmpty() ? "" : xmlModelClasses,
                xmlModelClassProperties.isEmpty() ? "" : xmlModelClassProperties),
            "It is necessary to first declare the attribute (class) as deprecated, setting the property isDeprecated = \"true\", and remove it in the data model of the next major version. " +
                "If backward compatibility of the API is not important (for example, if an attribute (class) is removed from a model that is not intended for industrial use)," +
                "then you can build the model with the allowDeleteNonDeprecatedItems setting set to true");
    }

}
