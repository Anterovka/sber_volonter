package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.XmlModelClassReference;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Exception: The class name is already set in the base module model
 */
public class NewElementDeprecatedException extends CheckXmlModelException {

    public NewElementDeprecatedException(List<XmlModelClass> deprecatedClasses,
                                         List<XmlModelClassProperty> deprecatedProperties,
                                         List<XmlModelClassReference> deprecatedReferences) {
        super(makeMessage(deprecatedClasses, deprecatedProperties, deprecatedReferences),
            "If you don't need the element, then you don't need to add it at all.");
    }

    private static String makeMessage(List<XmlModelClass> deprecatedClasses,
                                      List<XmlModelClassProperty> deprecatedProperties,
                                      List<XmlModelClassReference> deprecatedReferences) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("In the model, there are new elements marked as deprecated. ");
        if (!deprecatedClasses.isEmpty()) {
            stringBuilder.append("Classes: ");
            stringBuilder.append(deprecatedClasses.stream().map(XmlModelClass::getName).collect(Collectors.toList()));
            stringBuilder.append(". ");
        }
        if (!deprecatedProperties.isEmpty()) {
            stringBuilder.append("Properties: ");
            stringBuilder.append(deprecatedProperties.stream()
                .map(property -> String.format("%s of class %s", property.getName(), property.getModelClass().getName()))
                .collect(Collectors.toList()));
            stringBuilder.append(". ");
        }
        if (!deprecatedReferences.isEmpty()) {
            stringBuilder.append("Links: ");
            stringBuilder.append(deprecatedReferences.stream()
                .map(reference -> String.format("%s of class %s", reference.getName(), reference.getModelClass().getName()))
                .collect(Collectors.toList()));
            stringBuilder.append(". ");
        }

        return stringBuilder.toString();
    }
}
