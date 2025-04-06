package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.data.model.Property;
import com.sbt.mg.data.model.XmlIndex;
import com.sbt.mg.data.model.XmlModelClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Exception: Unsupported index
 */
public class UnsupportedPropertyIndexException extends CheckXmlModelException {
    private static Map<XmlIndex, List<Property>> indexPropertiesToMap(List<Property> deprecatedIndexProperties) {
        return deprecatedIndexProperties.stream()
                .collect(Collectors.groupingBy(Property::getXmlIndex));
    }

    private static String indexProperties(List<Property> deprecatedIndexProperties) {
        Map<String, List<String>> mapClassProperties = new HashMap<>();
        deprecatedIndexProperties.forEach(property -> {
            XmlIndex xmlIndex = property.getXmlIndex();
            if (Objects.nonNull(xmlIndex)) {
                XmlModelClass modelClass = xmlIndex.getModelClass();
                if (Objects.nonNull(modelClass)) {
                    String nameModelClass = modelClass.getName();
                    mapClassProperties.putIfAbsent(nameModelClass, new ArrayList<>());
                    mapClassProperties.get(nameModelClass).add(property.getName());
                }
            }
        });

        List<String> listClassProperties = new ArrayList<>();
        if (mapClassProperties.isEmpty()) {
            deprecatedIndexProperties.forEach(property -> listClassProperties.add(property.getName()));
        } else {
            mapClassProperties.keySet().stream().forEach(nameClass ->
                listClassProperties.add(String.format("class: [%s], properties marked as deprecated: [%s]",
                            nameClass,
                            String.join(", ", mapClassProperties.get(nameClass)))));
        }
        return String.join("; ", listClassProperties);
    }

    /**
     * @param className    Class name
     * @param propertyName Property name
     */
    public UnsupportedPropertyIndexException(String className, String propertyName) {
        super(join("In the class", className, "an index is declared on the property", propertyName, "of LOB type.",
                "The index is not supported."),
            "Уберите индекс с свойства");
    }

    public UnsupportedPropertyIndexException(List<Property> deprecatedIndexProperties) {
        super(join("The model contains indices where deprecated fields are used: \n",
                indexProperties(deprecatedIndexProperties)),
            "It is necessary to remove deprecated fields from the indexes.");
    }
}
