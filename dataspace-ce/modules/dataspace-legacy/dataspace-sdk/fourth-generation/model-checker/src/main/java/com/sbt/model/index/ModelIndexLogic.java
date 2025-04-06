package com.sbt.model.index;

import com.sbt.dataspace.pdm.PdmModel;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.NameHelper;
import com.sbt.mg.data.model.ClassStrategy;
import com.sbt.mg.data.model.Property;
import com.sbt.mg.data.model.PropertyType;
import com.sbt.mg.data.model.TypeInfo;
import com.sbt.mg.data.model.XmlIndex;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.XmlModelClassReference;
import com.sbt.mg.exception.checkmodel.ClassNotFoundException;
import com.sbt.mg.exception.checkmodel.UnsupportedPropertyIndexException;
import com.sbt.model.exception.AbstractClassIndexException;
import com.sbt.model.exception.IndexNameAlreadyDefinedException;
import com.sbt.model.exception.IndexOnMappedByPropertyException;
import com.sbt.model.exception.ReferenceCollectionInComplexIndexException;
import com.sbt.model.exception.UnsupportedIndexInDefineEmbeddableException;
import com.sbt.model.index.exception.EmptyIndexException;
import com.sbt.model.index.exception.IncompatibleAttributesWithCollectionException;
import com.sbt.model.index.exception.IncompatibleAttributesWithMappedByException;
import com.sbt.model.index.exception.IndexByNotEmbeddableTypePropertyException;
import com.sbt.model.index.exception.IndexCollectionException;
import com.sbt.model.index.exception.IndexPropertyNotFoundException;
import com.sbt.model.index.exception.IndexSetAlreadyDefinedException;
import com.sbt.model.utils.Models;
import com.sbt.parameters.enums.Changeable;
import com.sbt.reference.ExternalReferenceGenerator;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.sbt.mg.ModelHelper.isPrimitiveType;

public class ModelIndexLogic {

    private final XmlModel model;
    private final PdmModel pdmModel;
    private final PluginParameters pluginParameters;

// Used during meta generation
    private List<String> warnings;

    public static final Predicate<XmlModelClassProperty> indexedCollectionPredicate = modelClassProperty ->
            modelClassProperty.getMappedBy() == null
                    && !modelClassProperty.isEmbedded()
//                    && !modelClassProperty.isDeprecated()
                    && modelClassProperty.getCollectionType() != null;

    private static final Predicate<XmlModelClass> isClassNeedPkIndex = modelClass ->
            !modelClass.isAbstract() && !modelClass.isEmbeddable();


    public ModelIndexLogic(XmlModel model, PluginParameters pluginParameters, PdmModel pdmModel) {
        this.model = model;
        this.pluginParameters = pluginParameters;
        this.pdmModel = pdmModel;
    }

    public void initIndexes(List<String> warnings) {
        this.warnings = warnings;

        checkAndCorrectIndices();
        moveReferenceCollectionIndexesToServiceClass();
        checkClobAndBlobDoesNotHaveIndex();
        dropAbsorbingIndices();
// Left the method, but in fact earlier it was checked that there are no duplicate indices (checkDoubleIndices)
        dropIndexIfEqualsProperties();
// We mark the indexes as deprecated if they are inside deprecated classes.
        deprecateIndexesInDeprecatedClasses();
        // For some reason, for indexed embedded properties, the index name is filled in the processModelClassProperty and set on the property
        initIndexNames();
// In the model, there can be explicit user indices using deprecated fields
        checkIndexWithDeprecatedProperties();
    }

    private void checkAndCorrectIndices() {
        List<XmlModelClass> modelClasses = model.getClassesAsList();

// In Checker, there is also a check for checkInEmbeddableDoesNotDefineIndex();

// We check that the collection or mappedBy field is not marked as indexable or unique
        checkCollectionsDoesNotHaveIndexTag(modelClasses);
// Check that there are no indices on abstract classes, but it is some strange
        checkAbstractClassesDoesNotHaveIndices();
// Here we check in case the generators created incorrect indices
        checkNoIndexesInEmbeddable(model);
// Translates attributes marked as indexable into complex indexes
        createComplexIndexFromProperties();

        checkAllIndexPropertyExistsInClassesAndSetXmlProperty(model);

// Replaces embedded properties with their internal properties
        replaceEmbeddedPropertiesInIndex(model);
// We check that mappedBy is not used in indices or collection properties
        checkIndicesPropertiesNotCollection(modelClasses);
// Check for duplicate indices
        checkDoubleIndices(modelClasses);
    }

    private void moveReferenceCollectionIndexesToServiceClass() {
        model.getClassesAsList().forEach(modelClass -> {
                    final List<XmlIndex> indicesForDelete = modelClass.getIndices().stream()
                            .filter(it -> it.getProperties().size() == 1 &&
                                    it.getProperties().get(0).getReference() != null &&
                                    Objects.nonNull(it.getProperties().get(0).getReference().getCollectionType())
                            ).collect(Collectors.toList());

                    // Transfer to service classes.
                    indicesForDelete.forEach(index -> {
                        final XmlModelClassReference reference = index.getProperties().get(0).getReference();
                        // References are moved to the property. A utility class is needed."
                        final XmlModelClassProperty property = modelClass.getProperty(reference.getName());

                        final XmlModelClass serviceClass = model.getClassNullable(property.getType());
                        if (serviceClass == null) {
                            throw new ClassNotFoundException(reference.getType());
                        }

                        final Property indexProperty = new Property(
                                ExternalReferenceGenerator.REFERENCE + "." + ExternalReferenceGenerator.ENTITY_ID
                        );
                        indexProperty.setProperty(
                                model.getClass(serviceClass.getProperty(ExternalReferenceGenerator.REFERENCE).getType())
                                        .getProperty(ExternalReferenceGenerator.ENTITY_ID)
                        );
                        indexProperty.setPropertyOwner(serviceClass.getProperty(ExternalReferenceGenerator.REFERENCE));

                        serviceClass.addIndex(
                                createIndex(indexProperty)
                        );
                    });

                    indicesForDelete.forEach(modelClass::removeIndex);
                }

        );
    }

    private XmlIndex createIndex(Property property) {
        final XmlIndex xmlIndex = XmlIndex.create();
        xmlIndex.setIndexProperties(Collections.singletonList(property));

        return xmlIndex;
    }

    /**
* Checks that no indexes are declared in the embeddable class.
     */
    public static void checkNoIndexesInEmbeddable(XmlModel model) {
        List<XmlModelClass> listEmbeddableClassWithDefineIndexes = model.getClassesAsList()
                .stream()
                .filter(XmlModelClass::isEmbeddable)
                .filter(clazz -> !clazz.getIndices().isEmpty() || clazz.getPropertiesAsList().stream().anyMatch(prop -> prop.isAnyIndex()))
                .collect(Collectors.toList());
        if (!listEmbeddableClassWithDefineIndexes.isEmpty()) {
            throw new UnsupportedIndexInDefineEmbeddableException(listEmbeddableClassWithDefineIndexes);
        }
    }

    private void checkCollectionsDoesNotHaveIndexTag(List<XmlModelClass> modelClasses) {
        modelClasses.forEach(modelClass ->
                modelClass.getPropertiesAsList().forEach(property -> {
                            if ((property.isAnyIndex())) {
                                if (property.getCollectionType() != null) {
                                    if (property.getCategory() != PropertyType.PRIMITIVE) {
                                        throw new IncompatibleAttributesWithCollectionException(property);
                                    }
                                }

                                if (property.getMappedBy() != null) {
                                    throw new IncompatibleAttributesWithMappedByException(property);
                                }
                            }
                        }
                ));
    }

    /**
     * Replaces embedded properties with their internal properties
     */
    public static void replaceEmbeddedPropertiesInIndex(XmlModel xmlModel) {
        xmlModel.getClassesAsList().forEach(ModelIndexLogic::replaceEmbeddedIndex);
    }

    private static void replaceEmbeddedIndex(XmlModelClass modelClass) {
        // On embeddable classes, there can be no indices.
        if (modelClass.isEmbeddable()) {
            return;
        }

        //but indices can involve embedded fields
        XmlModel modelClassModel = modelClass.getModel();
        modelClass.getIndices().stream()
                .filter(ModelIndexLogic::containsEmbeddedProps)
                .forEach(xmlIndex -> {
                    List<Property> properties = xmlIndex.getProperties();
                    List<Property> newProperties = new ArrayList<>();

                    for (Property property : properties) {
                        XmlModelClassProperty xmlModelClassProperty = property.getProperty();

                        // If the index property is not embedded, then we simply add it to the collection without processing
                        if (!xmlModelClassProperty.isEmbedded()) {
                            newProperties.add(property);
                            continue;
                        }

                        //Such a hat happens when a link leads to a class with a compound identifier.
                        XmlModelClass embeddableClass = modelClassModel.getClass(xmlModelClassProperty.getType());
                        XmlModelClass trueEmbeddableClass = modelClassModel.getClassNullable(embeddableClass.getId().getType());
                        if (trueEmbeddableClass != null) {
                            embeddableClass = trueEmbeddableClass;
                        }

                        for (XmlModelClassProperty innerProperty : embeddableClass.getPropertiesAsList()) {
                            Property newProperty = new Property(xmlModelClassProperty.getName()
                                    + '.' + innerProperty.getName());

                            newProperty.setPropertyOwner(xmlModelClassProperty);
                            newProperty.setProperty(innerProperty);
                            newProperties.add(newProperty);
                        }
                    }
                    xmlIndex.setIndexProperties(newProperties);
                });
    }

    /**
     * In the index properties sets links to property and propertyOwner (XmlModelClassProperty)
     * Checking that properties are not collectible
     * Embedded properties are not revealed
     */
    public static void checkAllIndexPropertyExistsInClassesAndSetXmlProperty(XmlModel model) {
        model.getClassesAsList().forEach(modelClass -> modelClass.getIndices()
                .forEach(index -> {
                    index.getProperties().stream()
// If the property field is not filled
                            .filter(it -> Objects.isNull(it.getProperty()))
                            .forEach(property -> {
                                String[] splitPropertyName = property.getName().split("\\.");
                                if (splitPropertyName.length > 1) {
// Sets links to XmlModelClassProperty in Property and PropertyOwner attributes
// in accordance with the passed path splitPropertyName
                                    processIndexPropertyForEmbeddableClass(property, modelClass, splitPropertyName);
                                    return;
                                }
                                XmlModelClassProperty modelClassProperty =
                                        modelClass.getPropertyWithHierarchyInSingleTableNullable(property.getName());
                                if (modelClassProperty == null) {
                                    throw new IndexPropertyNotFoundException(modelClass.getName(), property.getName());
                                }
                                property.setProperty(modelClassProperty);
                            });
                    index.getProperties().stream()
                            .filter(it -> Objects.isNull(it.getReference()))
                            .forEach(property ->
                                property.setReference(modelClass.getReferenceNullable(property.getName())));
                }));
    }

    /**
* Sets links to XmlModelClassProperty in attributes Property and PropertyOwner according to the passed splitPropertyName path
     */
    private static void processIndexPropertyForEmbeddableClass(Property property, XmlModelClass clazz, String[] splitPropertyName) {
        XmlModelClass currentClass = clazz;
        for (int i = 0; i < splitPropertyName.length; i++) {
            XmlModelClassProperty modelClassProperty =
                    currentClass.getPropertyWithHierarchyInSingleTableNullable(splitPropertyName[i]);
            if (modelClassProperty == null) {
                throw new IndexPropertyNotFoundException(currentClass.getName(), splitPropertyName[i]);
            }

            if (i < splitPropertyName.length - 1) {
                currentClass = currentClass.getModel().getClassNullable(modelClassProperty.getType());
                if (currentClass == null) {
                    throw new IndexByNotEmbeddableTypePropertyException(clazz.getName(), splitPropertyName[i]);
                }

                //There may be a situation where a field refers to a class with a complex index, then at the logical
                // Coverage of this functionality with tests is required, as there are many questions about it.
                if (i == 0) {
                    XmlModelClass referenceClass = currentClass.getModel().getClassNullable(currentClass.getId().getType());
                    if (referenceClass != null) {
                        currentClass = referenceClass;
                    }
                }

                if (!currentClass.isEmbeddable()) {
                    throw new IndexByNotEmbeddableTypePropertyException(clazz.getName(), splitPropertyName[i]);
                }
            }

            if (i == 0) {
                property.setPropertyOwner(modelClassProperty);
            } else if (i == splitPropertyName.length - 1) {
                property.setProperty(modelClassProperty);
            }
        }
    }

    private static boolean containsEmbeddedProps(XmlIndex xmlIndex) {
        return xmlIndex.getProperties().stream().anyMatch(property -> property.getProperty().isEmbedded());
    }

    private void dropAbsorbingIndices() {
        if (pluginParameters.isGenerateAllIndices()) {
            return;
        }
        model.getClassesAsList().forEach(modelClass -> {

            // If SINGLE_TABLE, then indexes need to be removed everywhere in the hierarchy.
            // Save class index collections to delete directly from class hierarchies.
            List<List<XmlIndex>> allIndexesLists = new ArrayList<>();

            allIndexesLists.add(modelClass.getIndices());
            // If the inheritance strategy is SingleTable, then we add all child indices later
            if (modelClass.getStrategy() == ClassStrategy.SINGLE_TABLE) {
                Set<XmlModelClass> allInheritedClasses = ModelHelper.getAllChildClasses(modelClass);
                allInheritedClasses.forEach(it -> allIndexesLists.add(it.getIndices()));
            }

            // We translate it into a flat list
            List<XmlIndex> indices = new ArrayList<>();
            for (List<XmlIndex> item : allIndexesLists) {
                indices.addAll(item);
            }

            // We choose not PK indices and sort by the number of properties in the index
            List<XmlIndex> notPKSortedIndexes = indices.stream()
                    .filter(index -> !index.isPrimaryKey())
                    .sorted(Comparator.comparingInt(index -> index.getProperties().size()))
                    .collect(Collectors.toList());

            List<XmlIndex> indexForRemove = new ArrayList<>();

            notPKSortedIndexes.stream()
                    //This condition is not transferred to notPKSortedIndexes, as a non-unique index may be overridden by a unique one.
                    .filter(index -> !index.isUnique())
                    .forEach(currentIndex -> {
                        List<XmlIndex> absorbingIndices = findAbsorbingIndices(notPKSortedIndexes, currentIndex);

                        if (!absorbingIndices.isEmpty()) {
                            indexForRemove.add(currentIndex);
                            absorbingIndices.add(0, currentIndex);
                            warnings.add(makeWarnMessage(Collections.singletonMap(modelClass.getName(), absorbingIndices)));
                        }
                    });

            indexForRemove.forEach(index -> {
                index.getModelClass().removeIndex(index);
            });
        });
    }

    private void dropIndexIfEqualsProperties() {
        List<XmlModelClass> modelClasses = model.getClassesAsList();
        modelClasses.forEach(modelClass -> {
            List<XmlIndex> modelClassIndices = modelClass.getIndices();
            List<XmlIndex> indexForRemove = new ArrayList<>();
            modelClassIndices.stream()
                    .filter(xmlIndex -> !indexForRemove.contains(xmlIndex))
                    .forEach(xmlIndex -> {

                        List<XmlIndex> indexesWithSameProperties = modelClassIndices.stream()
                                .filter(it -> Objects.equals(xmlIndex.getProperties(), it.getProperties()))
                                .collect(Collectors.toList());

                        if (indexesWithSameProperties.size() > 1) {
                            XmlIndex survivorIndex = indexesWithSameProperties.stream()
                                    .filter(XmlIndex::isUnique)
                                    .findFirst()
                                    .orElse(indexesWithSameProperties.get(0));

                            indexesWithSameProperties.stream()
                                    .filter(it -> !it.equals(survivorIndex))
                                    .forEach(indexForRemove::add);
                        }
                    });
            indexForRemove.forEach(index -> index.getModelClass().removeIndex(index));
        });

        modelClasses.stream()
                .filter(modelClass -> modelClass.getStrategy() == ClassStrategy.SINGLE_TABLE)
                .forEach(modelClass -> {
                            List<XmlIndex> existingIndexes = new ArrayList<>();

                            Set<XmlModelClass> inheritedClasses = ModelHelper.getAllChildClasses(modelClass, true);

                            inheritedClasses.forEach(inheritedClass -> {
                                List<XmlIndex> indices = inheritedClass.getIndices();
                                List<XmlIndex> indexForRemove = new ArrayList<>();

                                indices.forEach(index -> {
                                    Optional<XmlIndex> existing = existingIndexes.stream()
                                            .filter(existingIndex -> isPropertiesEqualsByColumnNames(existingIndex.getProperties(), index.getProperties()))
                                            .findFirst();

                                    if (existing.isPresent()) {
                                        XmlIndex existingIndex = existing.get();
                                        if (index.isUnique() && !existingIndex.isUnique()) {
                                            existingIndex.setUnique();
                                        }
                                        indexForRemove.add(index);
                                    } else {
                                        existingIndexes.add(index);
                                    }
                                });

                                indexForRemove.forEach(index -> index.getModelClass().removeIndex(index));
                            });
                        }
                );
    }

    private boolean isPropertiesEqualsByColumnNames(List<Property> properties1, List<Property> properties2) {
        if (properties1.size() == properties2.size()) {
            for (int i = 0; i < properties1.size(); i++) {
                if (!Objects.equals(properties1.get(i).getProperty().getColumnName(), properties2.get(i).getProperty().getColumnName())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Indices are checked for empty fields and duplication by properties
     * Including unique indices
     */
    private void checkDoubleIndices(Collection<XmlModelClass> classes) {
        Map<String, List<XmlIndex>> indicesClasses = new HashMap<>();
        classes.forEach(clazz -> {
            String clazzName = clazz.getName();
            List<XmlIndex> doubleIndices = new ArrayList<>();
            List<XmlIndex> indices = clazz.getIndices();
            Set<String> indicesPropertySets = new HashSet<>(indices.size());
            for (XmlIndex index : indices) {
                String indexPropertySet = index.getProperties().stream()
                        .map(Property::getName)
                        .reduce((s, s2) -> s + ";" + s2)
                        .orElseThrow(() -> new EmptyIndexException(clazzName));
                if (!indicesPropertySets.add(indexPropertySet)) {
                    doubleIndices.add(index);
                }
            }
            if (!doubleIndices.isEmpty()) {
                indicesClasses.put(clazzName, doubleIndices);
            }
        });
        if (!indicesClasses.isEmpty()) {
            throw new IndexSetAlreadyDefinedException(indicesClasses);
        }
    }

    // To find all indices that contain the current one, excluding isPrimaryKey and unique indices
    private static List<XmlIndex> findAbsorbingIndices(Collection<XmlIndex> allIndices, XmlIndex currentIndex) {
        // If the current index is unique, then the check is only needed for a complete match of the fields. It exists.
        if (currentIndex.isUnique() || currentIndex.isPrimaryKey()) {
            return Collections.emptyList();
        }

        return allIndices.stream()
                .filter(index -> {
                    List<Property> currentProperties = currentIndex.getProperties();
                    // If this is the same index, or there are fewer fields in the index than in the given one, then we skip
                    // such index cannot be covering
                    // indices with an equal number of fields are also skipped, as duplicates should have been processed earlier
                    if (index == currentIndex || index.getProperties().size() <= currentProperties.size()) {
                        return false;
                    }

                    for (int i = 0; i < currentProperties.size(); i++) {
                        if (!Objects.equals(currentProperties.get(i), index.getProperties().get(i))) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    private static String makeWarnMessage(Map<String, List<XmlIndex>> indicesClasses) {
        return String.format("In the model, there are indices that overlap in their composition with other indices. " +
                "Indices are contained in classes: %s",
            getFormattedViewOfIndicesContents(indicesClasses));

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

    /**
     * Checks that in indexes mappedby is not used or collection properties
     */
    private void checkIndicesPropertiesNotCollection(Collection<XmlModelClass> classes) {
final String ERROR_TEMPLATE = "in class %s index with field %s";
        List<String> mappedByFoundErrors = new ArrayList<>();
        List<String> collectionFoundErrors = new ArrayList<>();
        List<String> referenceCollectionInComplexIndexErrors = new ArrayList<>();
        classes.forEach(modelClass ->
                modelClass.getIndices().forEach(index ->
                        index.getProperties().forEach(property -> {
                            //If the index from property 1 of the reference collection is valid, then it can be done.
                            if (index.getProperties().size() == 1 && Objects.nonNull(property.getProperty().getCollectionType()) &&
                                    Boolean.TRUE.equals(property.getProperty().isExternalLink())) {
                                return;
                            }
                            //If the index is from 1, which is the property that the collection of primitives has, then it can.
                            if (index.getProperties().size() == 1 && Objects.nonNull(property.getProperty().getCollectionType()) &&
                                    Boolean.TRUE.equals(isPrimitiveType(property.getProperty().getType()))) {
                                return;
                            }

                            if (index.getProperties().size() > 1 && Objects.nonNull(property.getProperty().getCollectionType())
                                    && Boolean.TRUE.equals(property.getProperty().isExternalLink())) {
                                referenceCollectionInComplexIndexErrors.add(String.format(ERROR_TEMPLATE, modelClass.getName(), property.getName()));
                            }

                            if (property.getProperty().getMappedBy() != null) {
                                mappedByFoundErrors.add(String.format(ERROR_TEMPLATE, modelClass.getName(), property.getName()));
                            }
                            if (property.getProperty().getCollectionType() != null) {
                                collectionFoundErrors.add(String.format(ERROR_TEMPLATE, modelClass.getName(), property.getName()));
                            }
                        })
                )
        );

        if (!referenceCollectionInComplexIndexErrors.isEmpty()) {
            throw new ReferenceCollectionInComplexIndexException(referenceCollectionInComplexIndexErrors);
        }

        if (!mappedByFoundErrors.isEmpty()) {
            throw new IndexOnMappedByPropertyException(mappedByFoundErrors);
        }

        if (!collectionFoundErrors.isEmpty()) {
            throw new IndexCollectionException(collectionFoundErrors);
        }
    }

    private void checkClobAndBlobDoesNotHaveIndex() {
        this.model.getClassesAsList().forEach(modelClass ->
                modelClass.getIndices().forEach(it ->
                        it.getProperties().forEach(property -> {
                            Models.fillCategoryAndTypeInfo(property.getProperty());
                            TypeInfo typeInfo = property.getProperty().getTypeInfo();
                            if ("clob".equals(typeInfo.getHbmName()) || "blob".equals(typeInfo.getHbmName())) {
                                throw new UnsupportedPropertyIndexException(modelClass.getName(), property.getName());
                            }
                        })
                )
        );
    }

    /**
     * Marks indexes as deprecated if they are inside deprecated classes.
     */
    private void deprecateIndexesInDeprecatedClasses() {
        model.getClassesAsList().stream()
                .filter(modelCLass -> modelCLass.isDeprecated() && !modelCLass.getIndices().isEmpty())
                .flatMap(modelClass -> modelClass.getIndices().stream())
                .forEach(XmlIndex::setDeprecated);
    }

    private void checkIndexWithDeprecatedProperties() {
        List<Property> indexDeprecatedProperties = model.getClassesAsList().stream()
                .filter(xmlModelClass -> !xmlModelClass.isDeprecated())
                .flatMap(modelClass -> modelClass.getIndices().stream())
                .filter(xmlIndex -> !xmlIndex.isDeprecated())
                .flatMap(xmlIndex -> xmlIndex.getProperties().stream())
                .filter(prop -> prop.getProperty().isDeprecated()
                        || prop.getPropertyOwner() != null && prop.getPropertyOwner().isDeprecated()
                )
                .collect(Collectors.toList());

        if (!indexDeprecatedProperties.isEmpty()) {
            throw new UnsupportedPropertyIndexException(indexDeprecatedProperties);
        }
    }

    private void initIndexNames() {
        if (pdmModel != null) {
            copyOldIndexNames();
        }
        reinitModelIndexNames(model);
        defineIndexNames(model, pluginParameters);
        checkIndexNameUnique(model);
    }

    /**
     * Copies old index names into the new model when fields match (considering order) and the uniqueness attribute
     */
    private void copyOldIndexNames() {
        XmlModel oldModel = pdmModel.getModel();
        List<XmlIndex> oldIndexes = pdmModel == null ? Collections.emptyList()
                : oldModel.getClassesAsList().stream()
                .flatMap(oldClass -> oldClass.getIndices().stream())
                .collect(Collectors.toList());

        this.model.getClassesAsList().stream()
                .forEach(classInNew -> {
                    // Copying the name of the pk-index
                    XmlModelClass classInOld = oldModel.getClassNullable(classInNew.getName());
                    if (classInOld == null || classInNew.getStrategy() != classInOld.getStrategy()) {
                        // Here we can't generate a new name until we transfer and remember all the names from the old model
                        return;
                    }

                    Optional<XmlIndex> newPkIndex = classInNew.getIndices().stream().filter(index -> index.isPrimaryKey()).findAny();
                    // This condition cannot be moved to the top of the stream, as abstract classes may contain
                    // primitively-collection fields
                    // The second condition checks that the PK index does not have a name explicitly set
                    if (isClassNeedPkIndex.test(classInNew) && (!newPkIndex.isPresent() || !newPkIndex.get().isManualName())) {
                        classInNew.setPkIndexName(classInOld.getPkIndexName());
                    }

                    // Copying the names of composite indexes
                    classInNew.getIndices().stream()
                            // If the index name is set in the model, we should not change it, even by the name from pdm
                            .filter(xmlIndex -> !xmlIndex.isManualName())
                            .forEach(newIndex -> {
                                Optional<XmlIndex> oldIndexOpt = oldIndexes.stream().filter(oldIndex -> oldIndex.equalsIgnoreName(newIndex)).findAny();
                                if (oldIndexOpt.isPresent()) {
                                    newIndex.setIndexName(oldIndexOpt.get().getIndexName());
                                    newIndex.getProperties().stream()
                                            .filter(property -> Objects.isNull(property.getXmlIndex()))
                                            .forEach(property -> property.setXmlIndex(newIndex));
                                }
                            });

// Copying index names to collection fields
                    classInNew.getPropertiesAsList().stream()
                            .filter(indexedCollectionPredicate)
                            .forEach(newProperty -> {
                                XmlModelClassProperty oldProperty = classInOld.getPropertyNullable(newProperty.getName());
                                if (oldProperty != null && !StringUtils.isEmpty(oldProperty.getCollectionPkIndexName())) {
                                    newProperty.setCollectionPkIndexName(oldProperty.getCollectionPkIndexName());
                                }
                            });
                });
    }

    public static void reinitModelIndexNames(XmlModel model) {
        model.clearIndexNames();

        model.getClassesAsList()
                .forEach(xmlModelClass -> {
                    if (xmlModelClass.getPkIndexName() != null
                        //For basic classes there are complex PK indexes, but for descendants there are none, so we add
                        // only for descendants, and for basics, the name will be added from the complex name
                            && !xmlModelClass.isBaseClassMark()
                            // SINGLE TABLE classes have one PK index on all descendants, it makes no sense to add it several times
                            && xmlModelClass.getStrategy() != ClassStrategy.SINGLE_TABLE) {
                        model.addIndexName(xmlModelClass.getPkIndexName());
                    }

                    xmlModelClass.getIndices().stream()
                            .filter(xmlIndex -> !StringUtils.isEmpty(xmlIndex.getIndexName()))
                            .map(xmlIndex -> xmlIndex.getIndexName())
                            .forEach(model::addIndexName);

                    xmlModelClass.getPropertiesAsList().stream()
                            .filter(xmlModelClassProperty -> !StringUtils.isEmpty(xmlModelClassProperty.getCollectionPkIndexName()))
                            .map(xmlModelClassProperty -> xmlModelClassProperty.getCollectionPkIndexName())
                            .forEach(model::addIndexName);
                });
    }

    private static String addIndexNameToModel(XmlModelClass modelClass, String indexName) {
        Set<String> indexNames = modelClass.getModel().getIndexNames();
//        if (indexNames.contains(indexName) && modelClass.getStrategy() != ClassStrategy.SINGLE_TABLE) {
//            throw new IndexNameAlreadyDefinedException(indexName);
//        }
        indexNames.add(indexName);
        return indexName;
    }

    private static void defineIndexNames(XmlModel model, PluginParameters pluginParameters) {
        model.getClassesAsList().forEach(modelClass -> {
            // For descendant classes, there should also be an index on the key (objectId)
            // If in the index there is no field objectId on the descendant, it won't be possible to make it composite.
            // to set a link to a field from the base class, we can break during checking of the absence in the index of fields
            // from parent classes when using JOIND strategy. Or to make an exception in the check for PK fields.
            if (isClassNeedPkIndex.test(modelClass) && StringUtils.isEmpty(modelClass.getPkIndexName())) {
                modelClass.setPkIndexName(addIndexNameToModel(
                        modelClass,
                        NameHelper.getPkIndexName(modelClass, pluginParameters.getMaxDBObjectNameLength())
                ));
            }

            // Generate names for indices without a name
            modelClass.getIndices().stream()
                    .filter(index -> StringUtils.isEmpty(index.getIndexName()))
                    .forEach(index -> {
                        String indexName = index.isPrimaryKey()
                                ? NameHelper.getPkIndexName(index.getModelClass(), pluginParameters.getMaxDBObjectNameLength())
                                : NameHelper.makeIndexName(
                                index,
                                pluginParameters.getMaxDBObjectNameLength(),
                                pluginParameters.getMinCroppedClassNameLength(),
                                pluginParameters.getMinCroppedPropertyNameLength()
                        );
                        index.setIndexName(indexName);
                        model.addIndexName(indexName);
                    });

            // Set the name of the PK index for collection fields
            modelClass.getPropertiesAsList().stream()
                    .filter(modelClassProperty -> indexedCollectionPredicate.test(modelClassProperty)
                            && StringUtils.isEmpty(modelClassProperty.getCollectionPkIndexName()))
                    .forEach(modelClassProperty ->
                            modelClassProperty.setCollectionPkIndexName(
                                    addIndexNameToModel(modelClass,
                                            NameHelper.getPkIndexNameForCollectionProperty(
                                                    modelClassProperty,
                                                    pluginParameters.getMaxDBObjectNameLength()
                                            )
                                    )
                            )
                    );
        });
    }

    /**
     * Transfers attributes marked as indexable to a comprehensive index.
     * In dependence on setting checks whether there are absorbing indexes, then does not create
     * For unique indexes, if there is a simple composite one - then an exception
     * Does not generate indices for deprecated fields and classes
     */
    private void createComplexIndexFromProperties() {
        model.getClassesAsList().stream()
                // no longer generate indices for deprecated classes
                .filter(modelClass -> !modelClass.isDeprecated())
                .forEach(modelClass -> {
                            modelClass.getPropertiesAsList().stream()
                                    .filter(property -> property.isAnyIndex()
                                            // Do not generate indexes for deprecated fields
                                            && !property.isDeprecated())
                                    .forEach(this::propertyToIndex);

                            modelClass.getReferencesAsList().stream()
                                    .filter(reference -> Boolean.TRUE.equals(reference.getIndex()) &&
                                            !reference.isDeprecated())
                                    .forEach(this::referenceToIndex);
                        }
                );
    }

    private void propertyToIndex(XmlModelClassProperty property) {
        ModelHelper.addComplexIndexToClass(
                property.getModelClass(),
                property.isUnique(),
                false,
                Collections.singletonList(property),
                true
        );

        //The indexes are created. The flags are no longer needed in the fields.
        property.setIndex(null);
        property.setUnique(null);
    }

    private void referenceToIndex(XmlModelClassReference reference) {
        ModelHelper.addComplexIndexToClass(
                reference.getModelClass(),
                Collections.singletonList(reference),
                true
        );

        //The indexes are created. The flags are no longer needed in the fields.
        reference.setIndex(null);
    }

    /**
     * Checking that there are no indices on abstract classes, but some strange
     */
    private void checkAbstractClassesDoesNotHaveIndices() {
        model.getClassesAsList().stream()
                // TODO rework into normal logic processing indices on abstract classes
                .filter(modelClass -> modelClass.isAbstract() && !modelClass.isHistoryClass())
                .forEach(modelClass -> {
                    if (!modelClass.getIndices().isEmpty()) {
                        throw new AbstractClassIndexException(modelClass);
                    }
                    boolean hasUserIndexedProps = modelClass.getPropertiesAsList().stream()
                            .anyMatch(property ->
                                    property.isAnyIndex()
// Check if the field is custom (not sure why SYSTEM isn't excluded, but oh well).
                                            && Changeable.READ_ONLY != property.getModelClass().getClassAccess()
// Next, all "system" fields are excluded, which the user cannot manually set an index for - somewhere earlier it should be checked???
                                            && !"Status".equals(property.getType())
                                            && Objects.isNull(property.getObjectLinks())
                                            && !property.isExternalLink());
                    if (hasUserIndexedProps) {
                        throw new AbstractClassIndexException(modelClass);
                    }
                });
    }

    public static void checkIndexNameUnique(XmlModel model) {
        List<XmlIndex> allIndexes = model.getClassesAsList().stream()
                .flatMap(xmlClass -> xmlClass.getIndices().stream())
// We skip processing deprecated indices as they are subject to immediate deletion.
                .filter(index -> !index.isDeprecated())
                .collect(Collectors.toList());

        List<String> names = new ArrayList<>();
        allIndexes.stream()
                .map(it -> it.getIndexName())
                .forEach(name -> names.add(name));

        List<String> childClassPkIndexNames = model.getClassesAsList().stream()
                .filter(xmlModelClass -> !xmlModelClass.isAbstract() && !xmlModelClass.isEmbeddable()
                                // exclude descendants of SingleTable, as they have the PK index name duplicated
                                // TODO it would be necessary to check if the PK index is trying to be created several times at the same time?
                                && xmlModelClass.getStrategy() != ClassStrategy.SINGLE_TABLE
                                //Excluding base classes, as there are complex PK indexes for them
                                && !xmlModelClass.isBaseClassMark()
//                        && !xmlModelClass.isDeprecated()
                )
                .map(xmlModelClass -> xmlModelClass.getPkIndexName())
                .collect(Collectors.toList());

        names.addAll(childClassPkIndexNames);

// Add the index names of the collection fields to the general collection of index names
        List<String> collectionPropertyIndexNames = model.getClassesAsList().stream()
//                .filter(xmlModelClass -> !xmlModelClass.isDeprecated())
                .flatMap(modelClass -> modelClass.getPropertiesAsList().stream())
                .filter(indexedCollectionPredicate)
                .filter(xmlModelClassProperty -> xmlModelClassProperty.getCollectionPkIndexName() != null)
                .map(xmlModelClassProperty -> xmlModelClassProperty.getCollectionPkIndexName())
                .collect(Collectors.toList());

        names.addAll(collectionPropertyIndexNames);

        //Checking complex indexes, PK indexes of child classes will be checked below
        Set<XmlIndex> indexesWithDuplicatedNames = new HashSet<>();
        for (XmlIndex index : allIndexes) {
            if (Collections.frequency(names, index.getIndexName()) > 1) {
                indexesWithDuplicatedNames.add(index);
            }
        }

        if (!indexesWithDuplicatedNames.isEmpty()) {
            throw new IndexNameAlreadyDefinedException(indexesWithDuplicatedNames);
        }

        List<String> pkIndexNamesFromProps = new ArrayList(childClassPkIndexNames);
        pkIndexNamesFromProps.addAll(collectionPropertyIndexNames);

        Set<String> pkIndexesWithDuplicatedNames = pkIndexNamesFromProps.stream()
                .filter(pkIndexName -> Collections.frequency(names, pkIndexName) > 1)
                .collect(Collectors.toSet());

        if (!pkIndexesWithDuplicatedNames.isEmpty()) {
            throw IndexNameAlreadyDefinedException.ofIndexNames(pkIndexesWithDuplicatedNames);
        }
    }
}
