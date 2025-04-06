package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.List;

public class RemovedAndDeprecatedItemsInSameMajorReleaseException extends CheckXmlModelException {

    public RemovedAndDeprecatedItemsInSameMajorReleaseException(List<XmlModelClass> xmlModelClasses, List<XmlModelClassProperty> xmlModelClassProperties) {
        super(join("In the model, elements declared as deprecated in the same major version have been removed, which may lead to a violation of backward compatibility of the API:",
                xmlModelClasses.isEmpty() ? "" : xmlModelClasses,
                xmlModelClassProperties.isEmpty() ? "" : xmlModelClassProperties),
            "It is necessary to increase the major version of the model.");
    }

}
