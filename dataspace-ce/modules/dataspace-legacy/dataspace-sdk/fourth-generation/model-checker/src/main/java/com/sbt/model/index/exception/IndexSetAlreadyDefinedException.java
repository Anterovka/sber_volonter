package com.sbt.model.index.exception;

import com.sbt.mg.data.model.Property;
import com.sbt.mg.data.model.XmlIndex;
import com.sbt.model.exception.parent.CheckModelException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Exception: The index field set is already defined in the model
 */
public class IndexSetAlreadyDefinedException extends CheckModelException {

    public IndexSetAlreadyDefinedException(Map<String, List<XmlIndex>> indicesClasses) {
        super("In the model, there are indexes with the same set and sequence of fields. Indexes are contained in classes: " +
                getFormattedViewOfIndicesContents(indicesClasses),
            "To configure indexes so that they are different in composition." +
                "Note the attributes of \"unique\" and \"index\" of classes");
    }

    private static String getFormattedViewOfIndicesContents(Map<String, List<XmlIndex>> indicesClasses) {
        StringBuilder formatMessage = new StringBuilder();
        indicesClasses.forEach((className, xmlIndices) -> {
            formatMessage.append(String.format("Class '%s', field set: %s; ", className, getIndicesFormattedView(xmlIndices)));
        });
        return formatMessage.toString();
    }

    private static String getIndicesFormattedView(List<XmlIndex> xmlIndices) {
        List<String> views = new ArrayList<>();
        xmlIndices.forEach(xmlIndex -> {
                    List<String> propertyNames = xmlIndex.getProperties().stream()
                            .map(Property::getName)
                            .map(name -> String.format("['%s']", name))
                            .collect(Collectors.toList());

                    views.add(String.join(",", propertyNames));
                }
        );
        return String.join(";", views);
    }

}
