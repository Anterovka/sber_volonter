package com.sbt.model.exception;

import com.sbt.mg.data.model.typedef.XmlTypeDef;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.List;
import java.util.stream.Collectors;

public class UnnecessaryTypeDefLengthException extends CheckXmlModelException {

    public UnnecessaryTypeDefLengthException(List<XmlTypeDef> wrongProperties) {
        super(join("For certain types of fields, it is not necessary to specify the length (length).",
                "Поля:", fieldsInfo(wrongProperties)),
            "The length indication of the specified fields should be removed.");
    }

    private static String fieldsInfo(List<XmlTypeDef> wrongProperties) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        stringBuilder.append(
                wrongProperties.stream()
                        .map(typeDef -> String.format(
                                "type %s in the type definition(type-defs)",
                                typeDef.getName()
                        ))
                        .collect(Collectors.joining(", "))
        );
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
