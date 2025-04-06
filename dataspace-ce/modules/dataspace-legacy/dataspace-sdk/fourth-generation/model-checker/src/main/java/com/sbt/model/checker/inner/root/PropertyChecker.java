package com.sbt.model.checker.inner.root;

import com.google.common.collect.Sets;
import com.sbt.dataspace.pdm.ParameterContext;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.CollectionType;
import com.sbt.mg.data.model.TypeInfo;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.XmlModelClassReference;
import com.sbt.mg.data.model.XmlModelInterface;
import com.sbt.mg.exception.checkmodel.ImplementationPropertyCollectionException;
import com.sbt.mg.exception.checkmodel.ImplementationPropertyNotFoundException;
import com.sbt.mg.exception.checkmodel.ImplementationPropertyTypeException;
import com.sbt.mg.exception.checkmodel.NullTypeException;
import com.sbt.mg.exception.checkmodel.PropertyNameAlreadyDefinedException;
import com.sbt.model.exception.ExtendedPropertyIntersectionException;
import com.sbt.model.exception.MandatoryFalseOnParentPropertiesException;
import com.sbt.model.exception.MandatoryOnClassCollectionPropertiesException;
import com.sbt.model.exception.UnsupportedCollectionException;
import com.sbt.model.exception.UnsupportedIndexInDefineEmbeddableException;
import com.sbt.model.exception.UnsupportedLinkInEmbeddableClassException;
import com.sbt.model.exception.UnsupportedReferenceInEmbeddableClassException;
import com.sbt.model.exception.UnsupportedTypeInEmbeddableException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sbt.mg.ModelHelper.TYPES_INFO;

public class PropertyChecker implements RootChecker {

    private final XmlModel model;
    private final ParameterContext parameterContext;

    public PropertyChecker(XmlModel model, ParameterContext parameterContext) {
        this.model = model;
        this.parameterContext = parameterContext;
    }

    @Override
    public void check() {
        checkAllPropertiesHaveType();
        checkClassDoesNotHaveDuplicateProperties();
        checkNoMappedByOnClassCollection();
        checkEmbeddedProperties();
        checkUniqueClassFields();
        checkNoListCollectionProperties();
        checkNoMandatoryAndParentOnProperty();
        checkInterfacesImplementation();
    }

    /**
     * Checks that all properties in the model are of class and interface types
     */
    private void checkAllPropertiesHaveType() {
// accumulation of all errors
        List<String> result = new ArrayList<>();

// bypass of class fields
        model.getClassesAsList().forEach(modelClass -> {
// Adding to result properties records with the missing type
            result.addAll(modelClass.getPropertiesAsList().stream()
                .filter(property -> property.getType() == null)
                .map(property ->
                    String.format("property %s in class %s",
                        property.getName(),
                        property.getModelClass().getName()
                    ))
                .collect(Collectors.toList())
            );

            final boolean disableEmptyTypeReferenceCheck =
                this.parameterContext.getPluginParameters().isDisableEmptyTypeReferenceCheck();

// Add to result information about external links with a missing type
            result.addAll(modelClass.getReferencesAsList().stream()
                .filter(reference -> {
                    if (disableEmptyTypeReferenceCheck) {
                        return reference.getType() == null;
                    }
                    return StringUtils.isEmpty(reference.getType());
                })
                .map(reference ->
                    String.format("link %s in class %s",
                        reference.getName(),
                        reference.getModelClass().getName())
                ).collect(Collectors.toList())
            );
        });

// bypassing interfaces
        model.getInterfacesAsList().forEach(xmlModelInterface -> {
// checking properties
            result.addAll(xmlModelInterface.getPropertiesAsList().stream()
                .filter(property -> property.getType() == null)
                .map(property ->
                    String.format("property %s in interface %s",
                        property.getName(),
                        property.getModelInterface().getName())
                ).collect(Collectors.toList()));
        });

        //throws an exception
        if (!result.isEmpty()) {
            throw new NullTypeException(result);
        }
    }

    /**
     * Check that the user has not duplicated field and reference names
     */
    private void checkClassDoesNotHaveDuplicateProperties() {
        model.getClassesAsList().forEach(clazz -> {
            Map<String, List<String>> allNames =
                Stream.concat(clazz.getPropertiesAsList().stream().map(XmlModelClassProperty::getName),
                        clazz.getReferencesAsList().stream().map(XmlModelClassReference::getName))
                    .collect(Collectors.groupingBy(Function.identity()));

            List<String> duplicateNames = allNames.entrySet().stream()
                .filter(it -> it.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

            if (!duplicateNames.isEmpty()) {
                throw new PropertyNameAlreadyDefinedException(clazz.getName(), duplicateNames);
            }
        });
    }

    private void checkNoMappedByOnClassCollection() {

        Map<String, List<XmlModelClassProperty>> wrongProperties = new HashMap<>();

        model.getClassesAsList()
            .forEach(modelClass -> {
                final List<XmlModelClassProperty> properties = modelClass.getPropertiesAsList().stream()
                    .filter(property -> property.isMandatory() &&
                        ModelHelper.isModelClassType(model, property.getType()) &&
                        !Objects.isNull(property.getCollectionType()))
                    .collect(Collectors.toList());
                if (!properties.isEmpty()) {
                    wrongProperties.put(modelClass.getName(), properties);
                }
            });

        if (!wrongProperties.isEmpty()) {
            throw new MandatoryOnClassCollectionPropertiesException(wrongProperties);
        }
    }

    /**
     * The properties of embeddable classes are checked on:
     * The original text does not contain any Russian words or phrases to be translated into English. Therefore, no replacement is needed.
     * The original text does not provide information on the replacement needed. Please provide the specific text or word that requires translation and replacement.
     * - отсутствие внешних ссылок
     * - отсутствие пометки индексированного поля
     */
    private void checkEmbeddedProperties() {
        model.getClassesAsList().forEach(this::checkEmbeddedProperty);
    }

    /**
     * The properties of the embeddable class are checked on:
     * The text does not contain any collection properties.
     * The original text does not provide information on the replacement needed. Please provide the specific text or word that requires translation and replacement.
     * - отсутствие внешних ссылок
     * - отсутствие пометки индексированного поля
     */
    private void checkEmbeddedProperty(XmlModelClass modelClass) {
        if (!modelClass.isEmbeddable()) {
            return;
        }

        // The collection fields on embeddable are forbidden
        List<XmlModelClassProperty> collectionProperties = modelClass.getPropertiesAsList().stream()
            .filter(property -> property.getCollectionType() != null)
            .collect(Collectors.toList());

        if (!collectionProperties.isEmpty()) {
            throw new UnsupportedTypeInEmbeddableException(collectionProperties, modelClass);
        }

        // The reference properties to the embeddable are forbidden
        List<XmlModelClassProperty> referenceProperties = modelClass.getPropertiesAsList().stream().
            filter(property -> model.containsClass(property.getType()))
            .collect(Collectors.toList());

        if (!referenceProperties.isEmpty()) {
            throw new UnsupportedLinkInEmbeddableClassException(modelClass, referenceProperties);
        }

        // External links to embeddable are forbidden
        if (!modelClass.getReferencesAsList().isEmpty()) {
            throw new UnsupportedReferenceInEmbeddableClassException(modelClass, modelClass.getReferencesAsList());
        }

        // check for the absence of unique and index attributes on the property
        List<XmlModelClassProperty> propertiesWithIndexAttribute = modelClass.getPropertiesAsList()
            .stream()
            .filter(XmlModelClassProperty::isAnyIndex)
            .collect(Collectors.toList());
        if (!propertiesWithIndexAttribute.isEmpty()) {
            throw new UnsupportedIndexInDefineEmbeddableException(propertiesWithIndexAttribute, modelClass);
        }
    }

    private void checkUniqueClassFields() {
// Check for duplicate fields inheritance within the framework.
        model.getClassesAsList().forEach(xmlModelClass -> checkUniqueFields(xmlModelClass, new HashSet<>()));
    }

    /**
     * Checking for the absence of duplicate fields within the inheritance framework
     */
    private void checkUniqueFields(XmlModelClass xmlModelClass, Set<String> properties) {
        Set<String> modelClassProperties = xmlModelClass.getPropertiesAsList().stream().map(XmlModelClassProperty::getName).collect(Collectors.toSet());
        Sets.SetView<String> intersection = Sets.intersection(properties, modelClassProperties);

        if (!intersection.isEmpty()) {
            throw new ExtendedPropertyIntersectionException(xmlModelClass.getName(), intersection);
        }

        xmlModelClass.getModel().getClassesAsList().stream()
            .filter(xmlModelClass1 -> xmlModelClass.getName().equals(xmlModelClass1.getExtendedClassName()))
            .forEach(xmlModelClass1 -> checkUniqueFields(xmlModelClass1, Sets.union(properties, modelClassProperties)));
    }

    private void checkNoListCollectionProperties() {
// Checking that the collection type is not set to List. List collections are not supported
        model.getClassesAsList().forEach(this::checkNoListCollection);
    }

    /**
     * Checking that the collection type is not set to List. List collections are not supported
     */
    private void checkNoListCollection(XmlModelClass modelClass) {
        List<XmlModelClassProperty> listProperties = modelClass.getPropertiesAsList().stream()
            .filter(property -> property.getCollectionType() == CollectionType.LIST).collect(Collectors.toList());
        if (!listProperties.isEmpty()) {
            throw new UnsupportedCollectionException(modelClass.getName(), listProperties);
        }
    }

    private void checkNoMandatoryAndParentOnProperty() {
        Map<String, List<XmlModelClassProperty>> wrongProperties = new HashMap<>();

        model.getClassesAsList()
                .forEach(modelClass -> {
                    final List<XmlModelClassProperty> properties = modelClass.getPropertiesAsList().stream()
                            .filter(property -> !Objects.isNull(property.getMandatory()) &&
                                    property.isParent())
                            .collect(Collectors.toList());
                    if (!properties.isEmpty()) {
                        wrongProperties.put(modelClass.getName(), properties);
                    }
                });

        if (!wrongProperties.isEmpty()) {
            throw new MandatoryFalseOnParentPropertiesException(wrongProperties);
        }
    }

    /**
     * Checking that classes correctly implement all fields of interfaces
     */
// Checks that objects have properties or external references defined on the interface (by name)
// Checks that the property type (or inner reference) on the object implements or matches the property type on the interface
// checks that the collectability of properties (or internal references) on the interface and objects matches
    private void checkInterfacesImplementation() {
// Creating the structure Interface -> Set<Implementation>
        Map<XmlModelInterface, Set<XmlModelClass>> interfaceToClassesSet = new HashMap<>();
        model.getClassesAsList().forEach(xmlModelClass -> xmlModelClass.getImplementedInterfacesAsSet()
            .forEach(iFace -> interfaceToClassesSet.computeIfAbsent(iFace, str -> new HashSet<>())
                .add(xmlModelClass))
        );

// For each interface, we perform checks of all its implementations
        for (Map.Entry<XmlModelInterface, Set<XmlModelClass>> interfaceToClassesEntry : interfaceToClassesSet.entrySet()) {
            XmlModelInterface xmlModelInterface = interfaceToClassesEntry.getKey();
            for (XmlModelClass implementation : interfaceToClassesEntry.getValue()) {
                xmlModelInterface.getPropertiesAsList().forEach(propOnInterface -> {
                    XmlModelClassProperty propOnClass = implementation.getPropertyWithHierarchyNullable(propOnInterface.getName());
                    if (propOnClass == null) {
                        throw new ImplementationPropertyNotFoundException(propOnInterface.getName(),
                            xmlModelInterface.getName(), implementation.getName());
                    }
// check that in the implementation the descendant of the given type is on the interface
                    if (!checkIfClassInheritsAnother(propOnClass.getType(), propOnInterface.getType())) {
                        throw new ImplementationPropertyTypeException(propOnInterface.getName(),
                            xmlModelInterface.getName(), implementation.getName());
                    }
// we check that the collective property flag matches the interface
                    if (!Objects.equals(propOnInterface.getCollectionType(),
                        propOnClass.getCollectionType())) {
                        throw new ImplementationPropertyCollectionException(propOnInterface.getName(),
                            xmlModelInterface.getName(), implementation.getName());
                    }
                });
            }
        }
    }

    /**
     * Checks that possibleInheritorClassname is a descendant (or implements) possibleParentClassname
     */
    private boolean checkIfClassInheritsAnother(String possibleInheritorClassname, String possibleParentClassname) {
        if (possibleInheritorClassname.equals(possibleParentClassname)) {
            return true;
        }
        TypeInfo typeInfoInheritor = TYPES_INFO.get(possibleInheritorClassname.toLowerCase(Locale.ENGLISH));
        TypeInfo typeInfoParent = TYPES_INFO.get(possibleParentClassname.toLowerCase(Locale.ENGLISH));
        if (typeInfoParent != null && typeInfoInheritor != null &&
            typeInfoInheritor.getFullName().equals(typeInfoParent.getFullName())) {
            return true;
        }
        XmlModelClass possibleInheritor = model.getClassNullable(possibleInheritorClassname);
        XmlModelClass possibleParent = model.getClassNullable(possibleParentClassname);
// If the type on the interface is a class
        if (possibleParent != null) {
            XmlModelClass currentParent = possibleInheritor;
            while (currentParent != null) {
                if (currentParent.equals(possibleParent)) {
                    return true;
                }
                currentParent = currentParent.getExtendedClass();
            }
            return false;
        }
// But it can also be an interface
        XmlModelInterface possibleInterface = model.getInterfaceNullable(possibleParentClassname);
        XmlModelClass currentParent = possibleInheritor;
        while (currentParent != null) {
            if (currentParent.getImplementedInterfacesAsSet().stream()
                    .anyMatch(modelInterface -> modelInterface.getName().equals(possibleInterface.getName()))) {
                return true;
            }
            currentParent = currentParent.getExtendedClass();
        }
        return false;
    }
}
