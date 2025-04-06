package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.GeneralSdkException;
import com.sbt.model.exception.parent.CheckModelException;

import java.util.Collection;
import java.util.stream.Collectors;

public class WrongScaledPropertyException extends CheckModelException {

    public WrongScaledPropertyException(Collection<XmlModelClassProperty> properties) {
        super(GeneralSdkException.join("The tag scale can be specified only for the type BigDecimal.",
                "Error in fields:", formProperties(properties)),
            "Remove the scale attribute from the listed fields.");
    }

    private static String formProperties(Collection<XmlModelClassProperty> properties) {
        return properties.stream()
            .map(it -> "property" + it.getName() + "of type" + it.getModelClass().getName())
                .collect(Collectors.joining(", "));
    }
}
