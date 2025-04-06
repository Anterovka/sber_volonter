package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.XmlModelInterfaceProperty;
import com.sbt.mg.data.model.XmlQueryParam;
import com.sbt.mg.data.model.XmlQueryProperty;
import com.sbt.mg.exception.GeneralSdkException;
import com.sbt.model.exception.parent.CheckModelException;

import java.util.List;

public class UnknownTypeException extends CheckModelException {
    public UnknownTypeException(XmlModelClassProperty xmlModelClassProperty) {
        super(GeneralSdkException.join("Unknown type '", xmlModelClassProperty.getType(), "' of field '", xmlModelClassProperty.getName(), "' in class '", xmlModelClassProperty.getModelClass().getName(), "'"),
            GeneralSdkException.join("The field '", xmlModelClassProperty.getName(), "' needs to be removed in the class '", xmlModelClassProperty.getModelClass().getName(), "'."));
    }

    public UnknownTypeException(XmlModelInterfaceProperty xmlModelInterfaceProperty) {
        super(GeneralSdkException.join("Unknown type '", xmlModelInterfaceProperty.getType(), "' of field '", xmlModelInterfaceProperty.getName(), "' in class '", xmlModelInterfaceProperty.getModelInterface().getName(), "'"),
            GeneralSdkException.join("The field '", xmlModelInterfaceProperty.getName(), "' needs to be removed in class '", xmlModelInterfaceProperty.getModelInterface().getName(), "'."));
    }

    public UnknownTypeException(XmlQueryProperty xmlQueryProperty) {
        super(GeneralSdkException.join("Unknown type '", xmlQueryProperty.getType(), "' of property '", xmlQueryProperty.getName(), "' in query '", xmlQueryProperty.getXmlQuery().getName(), "'"),
            GeneralSdkException.join("The property '", xmlQueryProperty.getName(), "' needs to be removed from the query '", xmlQueryProperty.getXmlQuery().getName(), "'."));
    }

    public UnknownTypeException(XmlQueryParam xmlQueryParam) {
        super(GeneralSdkException.join("Unknown type '", xmlQueryParam.getType(), "' parameter '", xmlQueryParam.getName(), "' in query '", xmlQueryParam.getXmlQuery().getName(), "'"),
            GeneralSdkException.join("Adding type to parameter is required in request '", xmlQueryParam.getXmlQuery().getName(), "'."));
    }

    public UnknownTypeException(List list, String message) {
        super(join(message),
            join("The types of the following fields need to be corrected: \n\t\t ", list));
    }
}
