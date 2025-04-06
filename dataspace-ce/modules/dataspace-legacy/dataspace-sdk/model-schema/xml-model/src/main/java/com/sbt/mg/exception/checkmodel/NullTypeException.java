package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.data.model.XmlQueryParam;

import java.util.List;

public class NullTypeException extends CheckXmlModelException {
    public NullTypeException(List<String> nullTypeElements) {
        super(join("In the model, elements were found for which the type (type) was not filled:", collectElements(nullTypeElements)),
            "The type (type) is required to be filled.A type must be set for all listed items.");
    }

    public NullTypeException(XmlQueryParam xmlQueryParam) {
        super(join("In the model, a parameter was found for which the type (type) was not filled:", xmlQueryParam.getName()),
            "The type(type)is required to be filled.A type must be set for all listed items.");
    }

    private static String collectElements(List<String> nullTypeElements) {
        return String.join(", ", nullTypeElements);
    }
}
