package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.List;
import java.util.stream.Collectors;

public class UnnecessaryFieldLengthException extends CheckXmlModelException {

    public UnnecessaryFieldLengthException(List<XmlModelClassProperty> wrongProperties) {
        super(join("For certain types of fields, it is not necessary to specify the length (length).",
                "Поля:", fieldsInfo(wrongProperties)),
            "The length of the indicated fields should be removed.");
    }

    private static String fieldsInfo(List<XmlModelClassProperty> wrongProperties) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        stringBuilder.append(
                wrongProperties.stream()
                        .map(property -> String.format(
                            "property %s of class %s",
                                property.getName(),
                                property.getModelClass().getName()
                        ))
                        .collect(Collectors.joining(", "))
        );
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
