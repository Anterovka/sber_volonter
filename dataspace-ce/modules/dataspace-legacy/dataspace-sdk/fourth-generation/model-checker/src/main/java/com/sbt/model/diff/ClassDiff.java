package com.sbt.model.diff;

import com.google.common.collect.Sets;
import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.ParameterContext;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.mg.ElementState;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.ClassStrategy;
import com.sbt.mg.data.model.XmlEmbeddedList;
import com.sbt.mg.data.model.XmlEmbeddedProperty;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.event.XmlEvent;
import com.sbt.mg.data.model.id.XmlId;
import com.sbt.mg.data.model.usermodel.UserXmlModelClass;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;
import com.sbt.mg.exception.checkmodel.UseIdPrefixChangingException;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.model.checker.Checker;
import com.sbt.model.exception.InheritanceStrategyChangeException;
import com.sbt.model.exception.SimpleMandatoryFieldMustBeFilledException;
import com.sbt.model.exception.diff.AddParentException;
import com.sbt.model.exception.diff.ChangeClassAbstractFlagException;
import com.sbt.model.exception.diff.ChangedExtendedClassException;
import com.sbt.model.exception.diff.RemoveParentException;
import com.sbt.parameters.enums.Changeable;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.sbt.dataspace.pdm.ModelGenerateUtils.getModelVersion;
import static com.sbt.model.diff.MessageHandler.getMessageChangeAbstractClass;
import static com.sbt.model.diff.MessageHandler.getMessageChangeEmbeddableClass;
import static com.sbt.model.diff.MessageHandler.getMessageChangeStrategy;
import static com.sbt.model.foreignkey.ForeignKeyHelper.isEnableCreateForeignKeys;
import static com.sbt.model.foreignkey.ForeignKeyHelper.prepareDropForeignKey;
import static com.sbt.model.foreignkey.ForeignKeyHelper.prepareGenerationForeignKey;

public class ClassDiff implements DiffHandler {
    private static final Logger LOGGER = Logger.getLogger(ClassDiff.class.getName());

    private final boolean disableCompatibilityCheck;

    public ClassDiff(boolean disableCompatibilityCheck) {
        this.disableCompatibilityCheck = disableCompatibilityCheck;
    }

    @Override
    public void handler(XmlModel newModel, XmlModel baseModel, ParameterContext parameterContext) {
        ModelParameters modelDiff = parameterContext.getModelParameters();
        PluginParameters pluginParameters = parameterContext.getPluginParameters();
        LOGGER.info("\n\n  Checking classes phase (start)");

        checkCorrectExtends(newModel, baseModel);
        checkAbstractFlagChange(newModel, baseModel, modelDiff);
        checkRemoveParentProperty(newModel, baseModel);
        if(!disableCompatibilityCheck) {
            checkAddParentProperty(newModel, baseModel);
        }
        swapNewCreatedClassesAndProperties(newModel, baseModel, modelDiff, pluginParameters);
        updateCciIndexes(newModel, baseModel);
        deleteClasses(newModel, baseModel, modelDiff, pluginParameters.isDeprecateDeletedItems());
        checkChangeLabel(newModel, baseModel, modelDiff);
        mergeImplementedInterfaces(newModel, baseModel, modelDiff);
        mergePreviousAndCurrentEmbeddedProperties(newModel, baseModel);
        inventoryPreviousAndCurrentEmbeddedProperties(newModel, baseModel, parameterContext.getPluginParameters().getMaxDBObjectNameLength());
        checkInheritanceStrategyChange(newModel, baseModel, modelDiff);
        takeNewLocks(newModel, baseModel, modelDiff);
        checkIdChange(newModel, baseModel, modelDiff);
        checkRootType(newModel, baseModel, modelDiff);
        checkClassAccess(newModel, baseModel, modelDiff);
        checkHistoryClassTag(newModel, baseModel, modelDiff);
        checkUseIdPrefix(newModel, baseModel, modelDiff);
        checkCloneable(newModel, baseModel, modelDiff);
        checkPartitionKeyRegexChange(newModel, baseModel, modelDiff);
        checkPartitionKeyChange(newModel, baseModel, modelDiff);
        checkSysInterfaces(newModel, baseModel);
        checkImportModelName(newModel, baseModel);
        checkIdempotenceExclude(newModel, baseModel);
        if (disableCompatibilityCheck) {
            checkEmbeddable(newModel, baseModel, modelDiff);
            // TODO непонятно зачем это?
            cloneProperty(newModel, baseModel);
        }
        LOGGER.info("\n\n  Checking classes phase (end)");
    }

    private void cloneProperty(XmlModel newModel, XmlModel baseModel) {
        newModel.getClassesAsList().forEach(newModelClass -> {
            XmlModelClass baseModelClass = baseModel.getClassNullable(newModelClass.getName());
            newModelClass.getPropertiesAsList().forEach(newModelClassProperty -> {
                XmlModelClassProperty baseModelClassProperty = baseModelClass.getProperty(newModelClassProperty.getName());
                if ((!baseModelClassProperty.getAllChanges().isEmpty())) {
                    cloneProperty(baseModelClassProperty, newModelClassProperty);
                }
            });
        });
    }

    private void checkEmbeddable(XmlModel newModel, XmlModel baseModel, ModelParameters modelDiff) {
        newModel.getClassesAsList().forEach(newModelClass -> {
            XmlModelClass prevModelClass = baseModel.getClassNullable(newModelClass.getName());

            // If there is no class in the old model, or the embeddable flag on the class has not been changed, then we exit
            if (prevModelClass == null || prevModelClass.isEmbeddable() == newModelClass.isEmbeddable()) {
                return;
            }

            modelDiff.addDiffObject(ElementState.UPDATED, prevModelClass);
            prevModelClass.addChangedProperty(XmlModelClass.EMBEDDED_TAG, prevModelClass.isEmbeddable());
            prevModelClass.setEmbeddable(newModelClass.isEmbeddable());
            prevModelClass.addChangedProperty(XmlModelClass.TABLE_NAME_TAG, prevModelClass.getTableName());
            prevModelClass.setTableName(newModelClass.getTableName());

            prevModelClass.addChangedProperty(XmlModelClass.PK_INDEX_NAME_TAG, prevModelClass.getPkIndexName());
            prevModelClass.setPkIndexName(newModelClass.getPkIndexName());

            prevModelClass.addChangedProperty(XmlModelClass.AFFINITY_TAG, prevModelClass.getAffinity());
            prevModelClass.setAffinity(newModelClass.getAffinity());

//                prevModelClass.addChangedProperty(XmlModelClass.STRATEGY_TAG, prevModelClass.getStrategy());
            prevModelClass.setStrategy(newModelClass.getStrategy());

            prevModelClass.addChangedProperty(XmlModelClass.EXTENDS_TAG, prevModelClass.getExtendedClassName());
            prevModelClass.setExtendedClassName(newModelClass.getExtendedClassName());

            prevModelClass.setBaseClassMark(newModelClass.isBaseClassMark());

            modelDiff.addBackwardCompatibilityViolationMessage(getMessageChangeEmbeddableClass(prevModelClass));

            //Properties that have this type are processed in PropertyDiff.checkEmbedded and checkColumName
        });
    }

    private void mergePreviousAndCurrentEmbeddedProperties(XmlModel newModel, XmlModel baseModel) {
        newModel.getClassesAsList().forEach(newModelClass -> {
            XmlModelClass prevModelClass = baseModel.getClassNullable(newModelClass.getName());

            if (prevModelClass == null) {
                return;
            }
            HashSet<XmlEmbeddedList> newEmbeddedLists = Sets.newHashSet(newModelClass.getEmbeddedPropertyList());
            List<XmlEmbeddedList> prevEmbeddedPropertyList = prevModelClass.getEmbeddedPropertyList();
            Sets.SetView<XmlEmbeddedList> embeddedListDifference =
                    Sets.difference(newEmbeddedLists, Sets.newHashSet(prevEmbeddedPropertyList));

            prevModelClass.addEmbeddedPropertyList(embeddedListDifference);

            newEmbeddedLists.forEach(newEmbeddedList -> {
                int indexOf = prevEmbeddedPropertyList.indexOf(newEmbeddedList);

                XmlEmbeddedList prevEmbeddedList = prevEmbeddedPropertyList.get(indexOf);

                Sets.SetView<XmlEmbeddedProperty> embeddedPropertyDifference = Sets.difference(
                        Sets.newHashSet(newEmbeddedList.getEmbeddedPropertyList()),
                        Sets.newHashSet(prevEmbeddedList.getEmbeddedPropertyList()));

                prevEmbeddedList.getEmbeddedPropertyList().addAll(embeddedPropertyDifference);
            });
        });
    }

    /**
     * If a new embedded property and embedded class are added simultaneously, the "lost" field
     */
    private void inventoryPreviousAndCurrentEmbeddedProperties(XmlModel newModel, XmlModel baseModel, int maxColumnNameLength) {
        List<String> changedEmbeddedClassNames = newModel.getClassesAsList().stream()
                .filter(XmlModelClass::isEmbeddable)
                .filter(newClass -> {
                    XmlModelClass prevClass = baseModel.getClass(newClass.getName());
                    // the old class may not be embeddable. This is about incompatible changes
                    return prevClass.isEmbeddable() &&
                            newClass.getPropertiesAsList().size() != prevClass.getPropertiesAsList().size();
                })
                .map(XmlModelClass::getName)
                .collect(Collectors.toList());

        changedEmbeddedClassNames.forEach(className -> {
            List<XmlModelClass> prevClassesWithChangedEmbeddedProperty = baseModel.getClassesAsList().stream()
                    .filter(it -> it.getPropertiesAsList().stream()
                            .anyMatch(property -> Objects.equals(property.getType(), className))
                    )
                    .collect(Collectors.toList());

            prevClassesWithChangedEmbeddedProperty.forEach(modelClass -> Checker.initEmbeddedList(modelClass, maxColumnNameLength));
        });
    }

    private void checkChangeLabel(XmlModel newModel, XmlModel baseModel, ModelParameters modelDiff) {
        newModel.getClassesAsList().forEach(xmlModelClass -> {
            XmlModelClass modelClass = baseModel.getClassNullable(xmlModelClass.getName());

            if (modelClass == null) {
                return;
            }

            if (!Objects.equals(xmlModelClass.getLabel(), modelClass.getLabel())) {
                modelDiff.addDiffObject(ElementState.UPDATED, modelClass);
                modelClass.addChangedProperty(XmlModelClass.LABEL_TAG, modelClass.getLabel());
                modelClass.setLabel(xmlModelClass.getLabel());
            }
        });
    }

    private void mergeImplementedInterfaces(XmlModel newModel, XmlModel baseModel, ModelParameters modelDiff) {
        newModel.getClassesAsList().forEach(xmlModelClass -> {
            XmlModelClass modelClass = baseModel.getClassNullable(xmlModelClass.getName());

            if (modelClass == null) {
                return;
            }

            if (!Objects.equals(xmlModelClass.getImplementedInterfacesAsString(), modelClass.getImplementedInterfacesAsString())) {
                modelDiff.addDiffObject(ElementState.UPDATED, modelClass);
                modelClass.addChangedProperty(XmlModelClass.IMPLEMENTS_TAG, modelClass.getImplementedInterfacesAsString());
                modelClass.setImplementedInterfacesAsString(xmlModelClass.getImplementedInterfacesAsString());
            }
        });

    }

    private void checkCorrectExtends(XmlModel newModel, XmlModel baseModel) {
        newModel.getClassesAsList().forEach(curClass -> {
            XmlModelClass prevClass = baseModel.getClassNullable(curClass.getName());

            if (prevClass == null) {
                return;
            }

            String prevClassParent =
                    Objects.equals(prevClass.getExtendedClassName(), JpaConstants.ENTITY_CLASS_NAME) ? null :
                            prevClass.getExtendedClassName();
            String curClassParent =
                    Objects.equals(curClass.getExtendedClassName(), JpaConstants.ENTITY_CLASS_NAME) ? null :
                            curClass.getExtendedClassName();
            if (!Objects.equals(prevClassParent, curClassParent)) {
                throw new ChangedExtendedClassException(prevClass, curClass);
            }
        });
    }

    private void checkAbstractFlagChange(XmlModel newModel, XmlModel baseModel, ModelParameters modelDiff) {
        newModel.getClassesAsList().forEach(curClass -> {
            XmlModelClass prevClass = baseModel.getClassNullable(curClass.getName());

            if (prevClass == null) {
                return;
            }

            if (!Objects.equals(prevClass.isAbstract(), curClass.isAbstract())) {
                if (disableCompatibilityCheck) {
                    if (Boolean.TRUE.equals(curClass.isAbstract())) {
                        modelDiff.addDiffObject(ElementState.UPDATED, curClass);

                    } else {
                        modelDiff.addDiffObject(ElementState.UPDATED, curClass);

                        //TODO  (PII) here is a bug - it does not take into account that there may be previous abstract classes with their own fields
                        // Where is the result of setting such *incomeProperties* used?
                        List<XmlModelClassProperty> prevAbstractProperties = prevClass.getPropertiesAsList();
                        List<XmlModelClass> inheritedClasses = baseModel.getClassesAsList().stream()
                                .filter(xmlModelClass -> prevClass.getName().equals(xmlModelClass.getExtendedClassName()))
                                .collect(Collectors.toList());
                        inheritedClasses.forEach(xmlModelClass -> prevAbstractProperties.forEach(xmlModelClass::addIncomePropertyWithoutCheck));

                        prevClass.addChangedProperty(XmlModelClass.ABSTRACT_TAG, prevClass.isAbstract());
                        prevClass.setAbstract(curClass.isAbstract());

                        modelDiff.addBackwardCompatibilityViolationMessage(getMessageChangeAbstractClass(prevClass));

                        prevClass.addChangedProperty(XmlModelClass.TABLE_NAME_TAG, prevClass.getTableName());
                        prevClass.setTableName(curClass.getTableName());

                        prevClass.addChangedProperty(XmlModelClass.PK_INDEX_NAME_TAG, prevClass.getPkIndexName());
                        prevClass.setPkIndexName(curClass.getPkIndexName());

                        prevClass.getIndices().stream()
                                .filter(xmlIndex -> !xmlIndex.isPrimaryKey())
                                .forEach(xmlIndex -> modelDiff.addDiffObject(ElementState.UPDATED, xmlIndex));
                    }
                } else {
                    throw new ChangeClassAbstractFlagException(prevClass.isAbstract(), curClass.isAbstract());
                }
            }
        });
    }

    private void swapNewCreatedClassesAndProperties(
            XmlModel newModel,
            XmlModel baseModel,
            ModelParameters modelDiff,
            PluginParameters pluginParameters
    ) {
        createFieldsForSingleTable(newModel, baseModel, modelDiff);
        newModel.getClassesAsList().forEach(newModelClass -> {
            // If the class is new
            if (!baseModel.containsClass(newModelClass.getName())) {
                modelDiff.addDiffObject(ElementState.NEW, newModelClass);
                newModelClass.getPropertiesAsList().forEach(xmlModelClassProperty -> {
                    modelDiff.addDiffObject(ElementState.NEW, xmlModelClassProperty);
                    if (isEnableCreateForeignKeys(pluginParameters)) {
                        prepareGenerationForeignKey(xmlModelClassProperty, pluginParameters.isEnableDeleteCascade());
                    }
                });
                baseModel.addClassWithoutCheck(newModelClass);
            } else {
                // If the class is old
                XmlModelClass baseModelClass = baseModel.getClass(newModelClass.getName());
                newModelClass.getPropertiesAsList().forEach(newModelClassProperty -> {
                    if (!baseModelClass.containsProperty(newModelClassProperty.getName())) {
                        if (newModelClassProperty.isMandatory() &&
                                newModelClassProperty.getDefaultValue() == null &&
                                !newModel.containsClass(newModelClassProperty.getType())) {
                            //possible dangerous condition (intentionally left to allow adding a leaf to an aggregate)
                            if (!disableCompatibilityCheck) {
                                throw new SimpleMandatoryFieldMustBeFilledException(newModelClassProperty);
                            }
                        }
                        if (
                                !baseModelClass.propertyChanged(XmlModelClass.ABSTRACT_TAG) &&
                                (baseModelClass.isEmbeddable() == newModelClass.isEmbeddable()) ||
                                ((baseModelClass.isEmbeddable() != newModelClass.isEmbeddable()) && newModelClass.isEmbeddable())
                        ) {
                            modelDiff.addDiffObject(ElementState.NEW, newModelClassProperty);
                        }
                        baseModelClass.addProperty(newModelClassProperty);
                    }
                });
            }
        });
    }

    private void createFieldsForSingleTable(XmlModel newModel, XmlModel baseModel, ModelParameters modelDiff) {
        newModel.getClassesAsList().stream()
                .filter(modelClass -> !baseModel.containsClass(modelClass.getName()))
                .filter(modelClass -> !modelClass.isAbstract())
                .forEach(newModelClass -> {
                    //If the class is SingleTable, then it should be checked whether it has abstract parents.
                    //If an abstract ancestor has no descendants other than this class, then it is necessary to create abstract class fields.
                    // otherwise, the fields should already be created and there is no need to create them
            if (newModelClass.getStrategy() == ClassStrategy.SINGLE_TABLE
                    && !newModelClass.isBaseClassMark()
                    && newModelClass.getExtendedClass().isAbstract()
            ) {
                XmlModelClass modelClass = newModelClass;
                while (modelClass.getExtendedClass().isAbstract()) {
                    XmlModelClass abstractClass = modelClass.getExtendedClass();
                    // Check if this class has old descendants. Also need to check that the fields of this class
                    // Children are no longer created within the scope of another offspring.
                    boolean physicalClassExists = ModelHelper.getAllChildClasses(abstractClass).stream()
                            .anyMatch(it -> !it.isAbstract() && baseModel.containsClass(it.getName()));
                    if (!physicalClassExists &&
                        // It is necessary to check whether this field has already been created within the already processed new classes
                        // Check the first encountered property, as if properties are created, they are all created.
                            !modelDiff.containsObjectInDiff(ElementState.NEW, abstractClass.getPropertiesAsList().get(0))
                    ) {
                        abstractClass.getPropertiesAsList().forEach(prop ->
                                modelDiff.addDiffObject(
                                        ElementState.NEW,
                                        // in the properties of diff, we specify within which class we create the property
                                        prop.addChangedProperty("implementClass", newModelClass)
                                )
                        );
                    }
                    modelClass = abstractClass;
                }
            }
        });
    }

    private void updateCciIndexes(XmlModel newModel, XmlModel baseModel) {
        newModel.getClassesAsList().forEach(newClass -> {
            if (baseModel.containsClass(newClass.getName())) {
                baseModel.getClass(newClass.getName()).setCciIndices(new ArrayList<>(newClass.getCciIndices()));
            }
        });
    }

    private void checkRemoveParentProperty(XmlModel newModel, XmlModel baseModel) {
        baseModel.getClassesAsList()
                .forEach(baseClass -> {
                    if (newModel.containsClass(baseClass.getName())) {
                        baseClass.getPropertiesAsList().stream()
                                .filter(property -> property.isParent()
                                        && !newModel.getClass(baseClass.getName()).containsProperty(property.getName())
                                        && !Objects.equals(property.getType(), baseClass.getName()))
                                .findFirst()
                                .ifPresent(property -> {
                                    if (disableCompatibilityCheck) {
                                        XmlModelClass newModelClass = newModel.getClass(baseClass.getName());
                                        baseClass.setAffinity(newModelClass.getAffinity());
                                    } else {
                                        throw new RemoveParentException(property);
                                    }
                                });
                    }
                });
    }

    private void checkAddParentProperty(XmlModel newModel, XmlModel baseModel) {
        newModel.getClassesAsList()
                .forEach(newClass -> {
                    if (baseModel.containsClass(newClass.getName())) {
                        newClass.getPropertiesAsList().stream()
                                .filter(it -> it.isParent()
                                        && !baseModel.getClass(newClass.getName()).containsProperty(it.getName())
                                        && !Objects.equals(it.getType(), newClass.getName())
                                )
                                .findFirst()
                                .ifPresent(it -> {
                                    throw new AddParentException(it);
                                });
                    }
                });
    }

    private void deleteClasses(XmlModel newModel, XmlModel baseModel, ModelParameters modelDiff, boolean deprecateDeletedItems) {
        baseModel.getClassesAsList().forEach(oldClass -> {
            if (!newModel.containsClass(oldClass.getName())) {
                if(!Boolean.TRUE.equals(oldClass.isDeprecated())) {
                    oldClass.addChangedProperty(XmlModelClass.DEPRECATED_TAG, oldClass.isDeprecated());
                    modelDiff.addDiffObject(ElementState.DEPRECATED, oldClass);
                    oldClass.setDeprecated(Boolean.TRUE);
                    // in our new deletion logic, we don't move deleted objects to a new model,
                    // since we are performing comparison of models for unused schema items in addDeletedItemsToXmlUnusedSchemaItems again
                    // for further transfer to the <unusedSchemaItems> section
                    if (deprecateDeletedItems) {
                        newModel.addClassWithoutCheck(oldClass);
                    }
                    setIndexesDeprecated(oldClass, modelDiff);
                    setRelationPropertyDeprecated(oldClass, modelDiff);
                }
            } else {
                XmlModelClass newClass = newModel.getClass(oldClass.getName());
                if (!Objects.equals(newClass.isDeprecated(), oldClass.isDeprecated())) {
                    oldClass.addChangedProperty(XmlModelClass.DEPRECATED_TAG, oldClass.isDeprecated());
                    modelDiff.addDiffObject(ElementState.DEPRECATED, oldClass);
                    oldClass.setDeprecated(newClass.isDeprecated());
                    if (oldClass.isEvent()) {
                        final XmlEvent oldEvent = baseModel.getEvent(oldClass.getName());
                        final XmlEvent newEvent = newModel.getEvent(oldClass.getName());
                        if (oldEvent != null && newEvent != null) {
                            oldEvent.setDeprecated(newEvent.isDeprecated());
                            oldEvent.setVersionDeprecated(getModelVersion(modelDiff, newModel));
                        }
                    }
                }
                if (oldClass.isDeprecated()) {
                    // TODO this logic is not for processing classes, but for processing indexes IndexDiff.
                    // The same index stopped being generated in deprecated classes, which led to the situation where the same index
                    // removed here and in IndexDiff, as IndexDiff started seeing that the index was deleted
                    // This led to the appearance of two drops in liquibase and two rollbacks,
                    // The original text was not provided, so it's impossible to give an accurate answer. Please provide the original text for translation and replacement.
                    // So far we have closed the control of duplicates at the level of addition to the diff. But we need to remove this logic from here.
                    setIndexesDeprecated(oldClass, modelDiff);
                    // TODO need to control it on the model and throw an exception, not deprecate under the hood
                    setRelationPropertyDeprecated(oldClass, modelDiff);
                }
                XmlModelClass extendedClass = oldClass.getExtendedClass();
                oldClass.getPropertiesAsList().stream()
                        .filter(property ->
                            //The given condition is required for the case when we change the class type from abstract to non-abstract.
                            //The original text does not contain any Russian words or phrases to be translated into English. Therefore, no replacements are needed.
                                !(JpaConstants.OBJECT_ID.equals(property.getName()) &&
                                        (extendedClass != null
                                                && extendedClass.propertyChanged(XmlModelClass.ABSTRACT_TAG)
                                                && !extendedClass.isAbstract()))
                        ).forEach(xmlModelClassProperty -> {
                    if (!newClass.containsProperty(xmlModelClassProperty.getName())) {
                        if (!xmlModelClassProperty.isDeprecated()) {
                            xmlModelClassProperty.addChangedProperty(XmlModelClassProperty.DEPRECATED_TAG, xmlModelClassProperty.isDeprecated());
                            xmlModelClassProperty.setDeprecated(Boolean.TRUE);
                            if (xmlModelClassProperty.getMappedBy() == null) {
                                modelDiff.addDiffObject(ElementState.DEPRECATED, xmlModelClassProperty);
                            }
                        }
                        if (xmlModelClassProperty.isMandatory()) {
                            xmlModelClassProperty.addChangedProperty(XmlModelClassProperty.MANDATORY_TAG, xmlModelClassProperty.isMandatory());
                            xmlModelClassProperty.setMandatory(Boolean.FALSE);
                            modelDiff.addDiffObject(ElementState.UPDATED, xmlModelClassProperty);
                        }
                        prepareDropForeignKey(xmlModelClassProperty);
                        if (StringUtils.isNotBlank(xmlModelClassProperty.getDefaultValue())) {
                            xmlModelClassProperty.addChangedProperty(XmlModelClassProperty.DEFAULT_VALUE_TAG, xmlModelClassProperty.getDefaultValue());
                            xmlModelClassProperty.setDefaultValue(null);
                        }

                    } else {
                        if (oldClass.isDeprecated()) {
                            xmlModelClassProperty.setDeprecated();
                        } else {
                            xmlModelClassProperty.setDeprecated(newClass.getProperty(xmlModelClassProperty.getName()).isDeprecated());
                        }
                    }
                });
            }
        });
    }

    private void setIndexesDeprecated(XmlModelClass deprecatedXmlModelClass, ModelParameters modelDiff) {
        deprecatedXmlModelClass.getIndices().stream()
                .filter(xmlIndex -> !xmlIndex.isPrimaryKey())
                .filter(xmlIndex -> !xmlIndex.isDeprecated() && !modelDiff.containsObjectInDiff(ElementState.REMOVED, xmlIndex))
                .forEach(xmlIndex -> {
                    modelDiff.addDiffObject(ElementState.REMOVED, xmlIndex);
                    xmlIndex.setDeprecated(Boolean.TRUE);
                    xmlIndex.setDeprecatedWithClass(true);
                });
    }

    private void setRelationPropertyDeprecated(XmlModelClass deprecatedXmlModelClass, ModelParameters modelDiff) {
        deprecatedXmlModelClass.getModel().getClassesAsList().stream()
                .filter(xmlModelClass -> !xmlModelClass.isDeprecated())
                .forEach(xmlModelClass -> xmlModelClass.getPropertiesAsList().stream()
                        .filter(property -> property.getType().equals(deprecatedXmlModelClass.getName()))
                        .filter(property -> !property.isDeprecated())
                        .forEach(property -> {
                            if(Objects.isNull(property.getVersionDeprecated()) && Objects.isNull(property.getMappedBy())) {
                                modelDiff.addDiffObject(ElementState.DEPRECATED, property);
                            }
                            property.setDeprecated(true);
                            xmlModelClass.getIndices().stream()
                                    .filter(xmlIndex -> xmlIndex.getProperties().stream()
                                            .anyMatch(indexProperty -> {
                                                String indexPropertyName = indexProperty.getName();
                                                if (indexPropertyName.contains(".")) {
                                                    String[] names = indexPropertyName.split("\\.");
                                                    return property.getName().equals(names[0]);
                                                } else {
                                                    return property.getName().equals(indexPropertyName);
                                                }

                                            }))
                                    .forEach(xmlIndex -> {
                                        modelDiff.addDiffObject(ElementState.REMOVED, xmlIndex);
                                        xmlIndex.setDeprecated(true);
                                        xmlIndex.setDeprecatedWithClass(true);
                                    });
                        })
                );
    }

    private void checkInheritanceStrategyChange(XmlModel newModel, XmlModel baseModel, ModelParameters modelDiff) {

        if (disableCompatibilityCheck) {
            newModel.getClassesAsList().stream()
//                    .filter(modelClass -> !modelClass.isAbstract())
                    .filter(modelClass -> !modelClass.isEmbeddable())
                    .filter(modelClass -> !(modelClass.getClassAccess() == Changeable.SYSTEM))
                    .forEach(modelClass -> {
                        if (baseModel.containsClass(modelClass.getName())) {
                            XmlModelClass baseClass = baseModel.getClass(modelClass.getName());
                            if (baseClass.getStrategy() == null) {
                                baseClass.setStrategy(ClassStrategy.JOINED);
                            }
                            ClassStrategy prevStrategy = baseClass.getStrategy();
                            //The class may be deprecated. Thus, it does not initialize the strategy by default through the Checker
                            ClassStrategy newStrategy = (modelClass.getStrategy() == null) ?
                                    ClassStrategy.JOINED : modelClass.getStrategy();
                            if (!Objects.equals(newStrategy, prevStrategy)) {
                                modelDiff.addDiffObject(ElementState.UPDATED, modelClass);
                                baseClass.addChangedProperty(XmlModelClass.STRATEGY_TAG, baseClass.getStrategy());
                                baseClass.setStrategy(modelClass.getStrategy());
                                modelDiff.addBackwardCompatibilityViolationMessage(getMessageChangeStrategy(baseClass));

                                if (newStrategy == ClassStrategy.SINGLE_TABLE) {
                                    if (!ModelHelper.isModelClassBaseMark(modelClass)) {
                                        String oldTableName = baseClass.getTableName();

                                        baseClass.addChangedProperty(XmlModelClass.TABLE_NAME_TAG, oldTableName);
                                        baseClass.setTableName(modelClass.getTableName());

                                        baseClass.getPropertiesAsList().forEach(property -> modelDiff.addDiffObject(ElementState.NEW, property));

                                        baseClass.addChangedProperty(XmlModelClass.PK_INDEX_NAME_TAG, baseClass.getPkIndexName());
                                        baseClass.setPkIndexName(modelClass.getPkIndexName());

                                        baseClass.getIndices().stream()
                                                .filter(xmlIndex -> !xmlIndex.isPrimaryKey())
                                                .forEach(xmlIndex -> modelDiff.addDiffObject(ElementState.UPDATED, xmlIndex));
                                    }
                                } else {

                                    if (!ModelHelper.isModelClassBaseMark(modelClass)) {
                                        String oldTableName = baseClass.getTableName();

                                        baseClass.addChangedProperty(XmlModelClass.TABLE_NAME_TAG, oldTableName);
                                        baseClass.setTableName(modelClass.getTableName());

                                        baseClass.addChangedProperty(XmlModelClass.PK_INDEX_NAME_TAG, baseClass.getPkIndexName());
                                        baseClass.setPkIndexName(modelClass.getPkIndexName());

                                        baseClass.getIndices().stream()
                                                .filter(xmlIndex -> !xmlIndex.isPrimaryKey())
                                                .forEach(xmlIndex -> modelDiff.addDiffObject(ElementState.UPDATED, xmlIndex));
                                    }

                                }
                            }
                        }
                    });
        } else {
            newModel.getClassesAsList().stream()
                    .filter(modelClass -> !modelClass.isAbstract())
                    .filter(modelClass -> !modelClass.isEmbeddable())
                    .filter(modelClass -> !(modelClass.getClassAccess() == Changeable.SYSTEM))
                    .forEach(modelClass -> {
                        if (baseModel.containsClass(modelClass.getName())) {
                            XmlModelClass baseClass = baseModel.getClass(modelClass.getName());
                            if (baseClass.getStrategy() == null) {
                                baseClass.setStrategy(ClassStrategy.JOINED);
                            }
                            ClassStrategy prevStrategy = baseClass.getStrategy();
                            //The class may be deprecated. Thus, it does not initialize the strategy by default through the Checker
                            ClassStrategy newStrategy = (modelClass.isDeprecated() && modelClass.getStrategy() == null) ?
                                    ClassStrategy.JOINED : modelClass.getStrategy();
                            if (!Objects.equals(newStrategy, prevStrategy)) {
                                throw new InheritanceStrategyChangeException(modelClass, newStrategy, prevStrategy);
                            }
                        }
                    });
        }

    }

    private void takeNewLocks(XmlModel newModel, XmlModel baseModel, ModelParameters modelDiff) {
        baseModel.getClassesAsList().forEach(it -> {
            XmlModelClass newClass = newModel.getClassNullable(it.getName());
            if (newClass != null && !Objects.equals(it.getLockable(), newClass.getLockable())) {
                modelDiff.addDiffObject(ElementState.UPDATED, it);
                it.addChangedProperty(UserXmlModelClass.LOCKABLE_TAG, it.getLockable());
                it.setLockable(newClass.getLockable());
            }
        });
    }

    private void checkIdChange(XmlModel newModel, XmlModel baseModel, ModelParameters modelDiff) {
        baseModel.getClassesAsList().forEach(it -> {
            XmlModelClass newClass = newModel.getClassNullable(it.getName());
            XmlId newId = null;
            if (newClass != null && !Objects.equals(it.getId().getIdCategory(), newClass.getId().getIdCategory())) {
                modelDiff.addDiffObject(ElementState.UPDATED, it);
                it.addChangedProperty("idCategory", it.getId().getIdCategory());
                newId = newClass.getId();
            }
            if (newClass != null && !Objects.equals(it.getId().getLength(), newClass.getId().getLength())) {
                if (it.getId().getLength() != null
                        && newClass.getId().getLength() != null
                        && it.getId().getLength() > newClass.getId().getLength()) {
                    throw new CheckXmlModelException("Reduced " + it.getName() + " class identifier length");
                }
                modelDiff.addDiffObject(ElementState.UPDATED, it);
                it.addChangedProperty("idLength", it.getId().getLength());
                newId = newClass.getId();
            }

            if (newId != null) {
                it.setId(newId);
            }
        });
    }

    private void checkRootType(XmlModel newModel, XmlModel baseModel, ModelParameters modelDiff) {
        baseModel.getClassesAsList().forEach(it -> {
            XmlModelClass newClass = newModel.getClassNullable(it.getName());
            if (newClass != null && !Objects.equals(it.getRootType(), newClass.getRootType())) {
                modelDiff.addDiffObject(ElementState.UPDATED, it);
                it.addChangedProperty(XmlModelClass.ROOT_TYPE_TAG, it.getRootType());
                it.setRootType(newClass.getRootType());
            }
        });
    }

    private void checkClassAccess(XmlModel newModel, XmlModel baseModel, ModelParameters modelDiff) {
        baseModel.getClassesAsList().forEach(it -> {
            XmlModelClass newClass = newModel.getClassNullable(it.getName());
            if (newClass != null && !Objects.equals(it.getClassAccess(), newClass.getClassAccess())) {
                modelDiff.addDiffObject(ElementState.UPDATED, it);
                it.addChangedProperty(XmlModelClass.CLASS_ACCESS_TAG, it.getClassAccess());
                it.setClassAccess(newClass.getClassAccess());
            }
        });
    }

    private void checkHistoryClassTag(XmlModel newModel, XmlModel baseModel, ModelParameters modelDiff) {
        baseModel.getClassesAsList().forEach(it -> {
            XmlModelClass newClass = newModel.getClassNullable(it.getName());
            if (newClass != null && !Objects.equals(it.getHistoryClass(), newClass.getHistoryClass())) {
                modelDiff.addDiffObject(ElementState.UPDATED, it);
                it.addChangedProperty(XmlModelClass.HISTORY_CLASS_TAG, it.getHistoryClass());
                it.setHistoryClass(newClass.getHistoryClass());
            }
        });
    }

    private void checkUseIdPrefix(XmlModel newModel, XmlModel baseModel, ModelParameters modelDiff) {
        baseModel.getClassesAsList().forEach(it -> {
            XmlModelClass newClass = newModel.getClassNullable(it.getName());
            if (newClass != null && !Objects.equals(it.isUseIdPrefix(), newClass.isUseIdPrefix())) {
                modelDiff.addDiffObject(ElementState.UPDATED, it);
                it.addChangedProperty(UserXmlModelClass.USE_ID_PREFIX_TAG, it.isUseIdPrefix());

                if (it.isUseIdPrefix()) {
                    throw new UseIdPrefixChangingException(it);
                }

                it.setUseIdPrefix(newClass.isUseIdPrefix());
            }
        });
    }

    private void checkCloneable(XmlModel newModel, XmlModel baseModel, ModelParameters modelDiff) {
        baseModel.getClassesAsList().forEach(it -> {
            XmlModelClass newClass = newModel.getClassNullable(it.getName());
            if (newClass != null && !Objects.equals(it.isCloneable(), newClass.isCloneable())) {
                modelDiff.addDiffObject(ElementState.UPDATED, it);
                it.addChangedProperty(UserXmlModelClass.CLONEABLE_TAG, it.isCloneable());
                it.setCloneable(newClass.isCloneable());
            }
        });
    }

    private void checkPartitionKeyRegexChange(XmlModel newModel, XmlModel baseModel, ModelParameters modelDiff) {
        baseModel.getClassesAsList().forEach(it -> {
            XmlModelClass newClass = newModel.getClassNullable(it.getName());
            if (newClass != null && !Objects.equals(it.getPartitionKeyRegex(), newClass.getPartitionKeyRegex())) {
                modelDiff.addDiffObject(ElementState.UPDATED, it);
                it.addChangedProperty(UserXmlModelClass.PARTITION_KEY_REGEX_TAG, it.getPartitionKeyRegex());
                it.setPartitionKeyRegex(newClass.getPartitionKeyRegex());
            }
        });
    }

    private void checkPartitionKeyChange(XmlModel newModel, XmlModel baseModel, ModelParameters modelDiff) {
        baseModel.getClassesAsList().forEach(it -> {
            XmlModelClass newClass = newModel.getClassNullable(it.getName());
            if (newClass != null && !Objects.equals(it.getPartitionKey(), newClass.getPartitionKey())) {

                modelDiff.addDiffObject(ElementState.UPDATED, it);
                it.addChangedProperty(XmlModelClass.PARTITION_KEY_TAG, it.getPartitionKey());
                it.setPartitionKey(newClass.getPartitionKey());

            }
        });
    }

    private void checkSysInterfaces(XmlModel newModel, XmlModel baseModel) {
        baseModel.getClassesAsList().forEach(it -> {
            XmlModelClass newClass = newModel.getClassNullable(it.getName());
            if (newClass != null && !it.getSystemInterfaces().containsAll(newClass.getSystemInterfaces())) {

                newClass.getSystemInterfaces().forEach(newInterface -> {
                    if (!it.getSystemInterfaces().contains(newInterface)) {
                        it.getSystemInterfaces().add(newInterface);
                    }
                });

            }
        });
    }

    private void checkImportModelName(XmlModel newModel, XmlModel baseModel) {
        baseModel.getClassesAsList().forEach(it -> {
            XmlModelClass newClass = newModel.getClassNullable(it.getName());
            if (newClass != null && !Objects.equals(it.getImportModelName(), newClass.getImportModelName())) {
                it.addChangedProperty(XmlModelClass.IMPORT_MODEL_NAME_TAG, it.getImportModelName());

                it.setImportModelName(newClass.getImportModelName());
            }
        });
    }

    private void checkIdempotenceExclude(XmlModel newModel, XmlModel baseModel) {
        baseModel.getClassesAsList().forEach(it -> {
            XmlModelClass newClass = newModel.getClassNullable(it.getName());
            if (newClass != null) {
                it.setIdempotenceExclude(newClass.getIdempotenceExclude());
            }
        });
    }
}
