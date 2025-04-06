package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MandatoryOnClassCollectionPropertiesException extends CheckXmlModelException {
    public MandatoryOnClassCollectionPropertiesException(Map<String, List<XmlModelClassProperty>> wrongProperties) {
        super(String.format(
                "Properties with completed mandatory requirements for the link collection have been found: %s",
                makeDetail(wrongProperties)
            ),
            "The field's requirement for the links collection is not necessary." +
                "The reverse link is populated when an item of the collection is created." +
                "Устраните замечания");
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
