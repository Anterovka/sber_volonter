package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MandatoryFalseOnParentPropertiesException extends CheckXmlModelException {
    public MandatoryFalseOnParentPropertiesException(Map<String, List<XmlModelClassProperty>> wrongProperties) {
        super(String.format(
                "The properties with the specified obligation have been found for the fields with the indication of the parent(parent): %s",
                makeDetail(wrongProperties)
            ),
            "The field's requirement for the parent fields is not indicated. The field is always required." +
                "Eliminate the comments");
    }

    private static String makeDetail(Map<String, List<XmlModelClassProperty>> wrongProperties) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        final String collect = wrongProperties.entrySet().stream()
                .map(entry -> {
                            final List<String> properties = entry.getValue().stream()
                                    .map(XmlModelClassProperty::getName)
                                .collect(Collectors.toList());

                    return String.format("in class %s properties %s",
                        entry.getKey(),
                        properties);
                        }
                )
                .collect(Collectors.joining(", "));

        sb.append(collect);

        sb.append("]");

        return sb.toString();
    }
}
