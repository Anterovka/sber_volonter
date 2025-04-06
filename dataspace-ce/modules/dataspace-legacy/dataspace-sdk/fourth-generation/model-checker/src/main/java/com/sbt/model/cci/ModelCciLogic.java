package com.sbt.model.cci;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.sbt.mg.data.model.Property;
import com.sbt.mg.data.model.XmlCciIndex;
import com.sbt.mg.data.model.XmlEmbeddedList;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.ClassNotFoundException;
import com.sbt.mg.exception.checkmodel.UndefinedPropertyException;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.model.cci.exceptions.CciEmbeddedFieldException;
import com.sbt.model.cci.exceptions.CciKeyLengthException;
import com.sbt.model.cci.exceptions.NotSuitableTypeException;
import com.sbt.model.cci.exceptions.PropertyNotFoundException;
import com.sbt.model.common.IndexNamer;
import com.sbt.model.exception.UnexpectedException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModelCciLogic {

    private static final String ID = "id";

    public static final Set<String> CCI_SUITABLE_TYPES = ImmutableSet.copyOf(Arrays.asList("String", "Unicodestring", "Long", "Integer", "Short", "Byte"));
    public static final Map<String, Integer> CCI_TYPE_LENGTH = new HashMap<String, Integer>() {{
        put("Long", String.valueOf(Long.MIN_VALUE).length());
        put("Integer", String.valueOf(Integer.MIN_VALUE).length());
        put("Short", String.valueOf(Short.MIN_VALUE).length());
        put("Byte", String.valueOf(Byte.MIN_VALUE).length());
    }};

    private static final List<String> ALLOW_SYSTEM_FIELDS =
            Stream.of(JpaConstants.OBJECT_ID, JpaConstants.OWNER_ID).collect(Collectors.toList());

    private final IndexNamer indexNamer = new IndexNamer(JpaConstants.DEFAULT_MIN_CROPPED_CLASS_NAME_LENGTH, JpaConstants.MAX_CCI_NAME_LENGTH);
    private final XmlModel model;
    private final int maxCciKeyLength;

    public ModelCciLogic(XmlModel model, int maxCciKeyLength) {
        this.model = model;
        this.maxCciKeyLength = maxCciKeyLength;
    }

    public void check() {
        createFromProperties();
        checkAndEnrichIndices();
        makeCciIndexPropertiesRequired();
    }

    private void createFromProperties() {
        this.model.getClassesAsList().forEach(modelClass -> modelClass.getPropertiesAsList().stream()
                .filter(it -> it.isCciIndex() || !Strings.isNullOrEmpty(getCciIndexNameFromProperty(it)))
                .forEach(property -> {
                    if (!checkSuitableType(property)) {
                        throwNoSuitableException(property);
                    }

                    XmlCciIndex cciIndex = new XmlCciIndex();
                    cciIndex.setName(getCciIndexNameFromProperty(property));
                    if (property.isEmbedded() && !property.isExternalLink()) {
                        cciIndex.setIndexProperties(defineReferenceProperties(property));
                    } else {
                        Property indexProperty = new Property(property.getName());
                        indexProperty.setProperty(property);
                        cciIndex.setIndexProperties(new ArrayList<>(Collections.singletonList(indexProperty)));
                    }

                    modelClass.getCciIndices().add(cciIndex);
                }));
    }

    private String getCciIndexNameFromProperty(XmlModelClassProperty property) {
        return property.getCciIndexName() != null ?
                property.getCciIndexName() : property.getCciName();
    }

    private void checkAndEnrichIndices() {
        this.model.getClassesAsList().forEach(
                modelClass -> modelClass.getCciIndices()
                        .forEach(xmlCciIndex -> {
                            List<Property> cciIndexOriginalProperties = new ArrayList<>(xmlCciIndex.getProperties());
                            decomposeReferenceFieldsIntoComponents(xmlCciIndex, modelClass);
                            checkPropertiesSet(xmlCciIndex, modelClass);
                            replaceSpecialNamesInPlace(xmlCciIndex.getProperties());
                            checkKeyLength(xmlCciIndex, modelClass);

                            if (Strings.isNullOrEmpty(xmlCciIndex.getName())) {
                                xmlCciIndex.setName(indexNamer.generateName(modelClass.getTableName(), cciIndexOriginalProperties));
                            }
                        }));
    }

    private void replaceSpecialNamesInPlace(List<Property> properties) {
        properties
                .forEach(property -> property.setName(processSpecialPropertyNames(property.getName())));
    }

    private void checkKeyLength(XmlCciIndex cciIndex, XmlModelClass modelClass) {
        int currentLength = 0;
        for (Property property : cciIndex.getProperties()) {
            //We cannot control the key length as well as the external link, since the links consist of keys. Only prayer.
            if (isAllowSystemField(property.getName()) || propertyIsReference(property, modelClass)) {
                return;
            }
            currentLength += getPropertyLength(property.getName(), modelClass);
        }
        if (currentLength > this.maxCciKeyLength) {
            throw new CciKeyLengthException(modelClass.getName(),
                    cciIndex.getProperties().stream().map(Property::getName).collect(Collectors.toList()), currentLength, maxCciKeyLength);
        }
    }

    private static boolean isAllowSystemField(String name) {
        return ALLOW_SYSTEM_FIELDS.contains(name);
    }

    private int getPropertyLength(String propertyName, XmlModelClass modelClass) {
        if (propertyName.contains(".")) {
            String[] split = propertyName.split("\\.");
            String embeddedClassName = null;
            XmlModelClass currentClass = modelClass;
            XmlModelClass finalClass = modelClass;
            for (String s : split) {
                finalClass = currentClass;
                embeddedClassName = currentClass.getPropertyWithHierarchyInSingleTable(s).getType();
                currentClass = currentClass.getModel().getClassNullable(embeddedClassName);
            }

            return getLength(finalClass.getPropertyWithHierarchyInSingleTable(split[split.length - 1]));
        } else {
            return getLength(modelClass.getPropertyWithHierarchyInSingleTable(propertyName));
        }
    }

    private static int getLength(XmlModelClassProperty property) {
        Integer length = CCI_TYPE_LENGTH.get(property.getType());
        if (length == null) {
            return property.getLength();
        }
        return length;
    }

    private static boolean propertyIsReference(Property property, XmlModelClass modelClass) {
        if (!property.getName().contains(".")) {
            return false;
        }

        String propertyName = property.getName().split("\\.")[0];
        return modelClass.getPropertyWithHierarchyInSingleTable(propertyName).isExternalLink();
    }

    private void decomposeReferenceFieldsIntoComponents(XmlCciIndex cciIndex, XmlModelClass modelClass) {
        List<Property> forRemove = new ArrayList<>();
        List<Property> forAdd = new ArrayList<>();
        cciIndex.getProperties().forEach(cciProperty -> {
            XmlModelClassProperty xmlProperty =
                    modelClass.getPropertyWithHierarchyInSingleTableNullable(cciProperty.getName());
            if (xmlProperty != null && Boolean.TRUE.equals(xmlProperty.isExternalLink())) {
                forRemove.add(cciProperty);

                XmlModelClass refClass = this.model.getClass(xmlProperty.getType());
                if (refClass == null) {
                    throw new ClassNotFoundException(xmlProperty.getType());
                }
                refClass.getPropertiesAsList().forEach(it ->
                        forAdd.add(new Property(String.join(".", xmlProperty.getName(), it.getName())))
                );

            }
        });
        cciIndex.getProperties().removeAll(forRemove);
        cciIndex.getProperties().addAll(forAdd);
    }

    private void checkPropertiesSet(XmlCciIndex cciIndex, XmlModelClass modelClass) {
        cciIndex.getProperties()
                .forEach(property -> {
                    Queue<String> queue = new ArrayDeque<>(Arrays.asList(property.getName().split("\\.")));
                    if (isObjectIdCciIndex(queue)) {
                        return;
                    }
                    if (queue.size() == 1) {
                        final String indexProperty = queue.peek();
                        final XmlModelClassProperty classProperty;
                        try {
                            classProperty = modelClass.getPropertyWithHierarchyInSingleTable(indexProperty);
                        } catch (UndefinedPropertyException ex) {
                            throw new PropertyNotFoundException(property.getName(), cciIndex.getName(), modelClass.getName());
                        }
                        if (classProperty != null && classProperty.isEmbedded()) {
                            throw new CciEmbeddedFieldException(modelClass.getName(), classProperty.getType(), indexProperty);
                        }
                    }
                    if (!isPropertyExists(queue, modelClass)) {
                        throw new PropertyNotFoundException(property.getName(), cciIndex.getName(), modelClass.getName());
                    }
                });
    }

    private boolean isObjectIdCciIndex(Queue<String> queue) {
        return queue.size() == 1 && ID.equals(queue.peek());
    }

    private boolean isPropertyExists(Queue<String> queue, XmlModelClass modelClass) {
        String first = queue.remove();
        if (queue.isEmpty()) {
            XmlModelClassProperty property = modelClass.getPropertyWithHierarchyInSingleTable(first);
            if (property == null) {
                return false;
            }
            return checkSuitableType(property);
        } else {
            XmlModelClassProperty property = modelClass.getPropertyWithHierarchyInSingleTable(first);
            if (property == null || !property.isEmbedded()) {
                return false;
            }

            String propertyType = property.getType();
            XmlModelClass nextClass = modelClass.getModel().getClassNullable(propertyType);
            if (nextClass == null) {
                return false;
            }
            return isPropertyExists(queue, nextClass);
        }
    }

    private boolean checkSuitableType(XmlModelClassProperty property) {
        if (property.getCollectionType() != null) {
            return false;
        }
        if (CCI_SUITABLE_TYPES.contains(property.getType())) {
            return true;
        }
        if (!property.isEmbedded()) {
            return false;
        } else {
            return isReference(property);
        }
    }

    private void throwNoSuitableException(XmlModelClassProperty property) {
        throw new NotSuitableTypeException(property.getCollectionType() == null ?
            property.getType() : ("Collection" + property.getType()));
    }

    private boolean isReference(XmlModelClassProperty property) {
        Optional<XmlEmbeddedList> optional = property.getModelClass().getEmbeddedPropertyList().stream()
                .filter(embedded -> embedded.getName().equals(property.getName()))
                .findFirst();

        if (!optional.isPresent()) {
            throw new UnexpectedException(
                String.format("The property marked as embedded is missing from the embedded-list in pdm.xml." +
                    "Property '%s', Class '%s'", property.getName(), property.getModelClass().getName()));
        }
        return optional.get().isReference();
    }

    private XmlEmbeddedList getEmbeddedList(XmlModelClassProperty property) {
        Optional<XmlEmbeddedList> first = property.getModelClass().getEmbeddedPropertyList().stream()
                .filter(embedded -> embedded.getName().equals(property.getName()))
                .findFirst();
        return first.orElseThrow(() -> new UnexpectedException(
            String.format("Repeated search in embedded-list-e of class %s property %s did not bring a result???",
                property.getModelClass().getName(),
                property.getName())));
    }

    private List<Property> defineReferenceProperties(XmlModelClassProperty property) {
        XmlEmbeddedList embeddedList = getEmbeddedList(property);
        List<Property> properties = embeddedList.getEmbeddedPropertyList().stream()
                .map(xmlEmbeddedProperty -> new Property(property.getName() + "." + xmlEmbeddedProperty.getName()))
                .collect(Collectors.toList());
        if (properties.isEmpty()) {
            throw new UnexpectedException("The embedded reference element turned out to have no properties!");
        }
        return properties;
    }

    private String processSpecialPropertyNames(String propertyName) {
        if (ID.equals(propertyName)) {
            return JpaConstants.OBJECT_ID;
        }
        return propertyName;
    }

    private void makeCciIndexPropertiesRequired() {
        this.model.getClassesAsList().forEach(modelClass ->
                modelClass.getCciIndices().forEach(cciIndex ->
                        cciIndex.getProperties().forEach(property -> {
                            if (isEmbeddedProperty(property)) {
                                final String classPropertyName = property.getName().split("\\.")[0];
                                modelClass.getPropertyWithHierarchyInSingleTable(classPropertyName).setMandatory(true);
                            } else {
                                if (!ID.equals(property.getName())) {
                                    modelClass.getPropertyWithHierarchyInSingleTable(property.getName()).setMandatory(true);
                                }
                            }
                        })));
    }

    private boolean isEmbeddedProperty(Property property) {
        return property.getName().contains(".");
    }
}
