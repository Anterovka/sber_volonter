package com.sbt.model.index;

import com.sbt.mg.data.model.ClassStrategy;
import com.sbt.mg.data.model.XmlIndex;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.sbt.reference.ExternalReferenceGenerator.ENTITY_ID;
import static com.sbt.reference.ExternalReferenceGenerator.ROOT_ENTITY_ID;

public class ModelIndexUtils {

    /**For SingleTable, it returns all indices from the inheritance chain; otherwise, it is for the passed class.*/
    public static Collection<XmlIndex> collectAllIndices(XmlModelClass modelClass) {
        Collection<XmlIndex> allIndices = new ArrayList<>(modelClass.getIndices());
        if (modelClass.getStrategy() == ClassStrategy.SINGLE_TABLE) {
            XmlModelClass parentClass = modelClass.getExtendedClass();
            while (parentClass != null) {
                allIndices.addAll(parentClass.getIndices());
                parentClass = parentClass.getExtendedClass();
            }
        }
        return allIndices;
    }

    /**
     * Is there an index for the property
     */
    public static boolean isPropertyIndexed(XmlModelClassProperty property) {
        Collection<XmlIndex> allIndexes = collectAllIndices(property.getModelClass());
        return allIndexes.stream().anyMatch(index -> isPropertyIndexedBy(property, index, true));
    }

    public static boolean isPropertyIndexedBy(XmlModelClassProperty property, XmlIndex index, boolean onlySimpleIndex) {
        String propertyName = property.getName();
        if (property.isExternalSoftReference()) {
            if (property.isExternalLink()) {
                boolean equals = Objects.equals(index.getProperties().get(0).getName(),
                        String.format("%s.%s", propertyName, ENTITY_ID)
                );
                return onlySimpleIndex
                        ? index.getProperties().size() == 1 && equals
                        : equals;
            } else {
                boolean equals = Objects.equals(index.getProperties().get(0).getName(),
                        String.format("%s.%s", propertyName, ENTITY_ID)) &&
                        Objects.equals(index.getProperties().get(1).getName(),
                                String.format("%s.%s", propertyName, ROOT_ENTITY_ID));
                return onlySimpleIndex
                        ? index.getProperties().size() == 2 && equals
                        : equals;
            }
        } else if (property.isEmbedded()) {
            List<XmlModelClassProperty> embeddedProperties
                    = property.getModelClass().getModel().getClass(property.getType()).getPropertiesAsList();
// If the index has a different number of fields than the embedded field, then it's definitely not the right index
            if (onlySimpleIndex && index.getProperties().size() != embeddedProperties.size()) {
                return false;
            }
// If the property can be covered by a compound index, then we check that the index has at least as many fields as
// in the embeddable class, otherwise the index definitely does not fit
            if (!onlySimpleIndex && embeddedProperties.size() > index.getProperties().size()) {
                return false;
            }

// we check that the index starts with the passed property
            for (int i = 0; i < embeddedProperties.size(); i++) {
                if (!index.getProperties().get(i).getName().equalsIgnoreCase(
                        propertyName + "." + embeddedProperties.get(i).getName()
                )) {
                    return false;
                }
            }
            return true;
        } else {
// If a normal property
            boolean equals = Objects.equals(index.getProperties().get(0).getName(), propertyName);
            return onlySimpleIndex
                    ? index.getProperties().size() == 1 && equals
                    : equals;
        }
    }

/** Finds indices starting with the given property */
    public static List<XmlIndex> findAbsorbIndices(XmlModelClassProperty property) {
        Collection<XmlIndex> allIndices = collectAllIndices(property.getModelClass());
        return allIndices.stream()
                .filter(index -> isPropertyIndexedBy(property, index, false))
                .collect(Collectors.toList());
    }

}
