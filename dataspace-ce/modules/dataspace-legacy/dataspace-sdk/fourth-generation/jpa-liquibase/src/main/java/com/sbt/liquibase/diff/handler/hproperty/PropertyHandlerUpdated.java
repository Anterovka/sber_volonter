package com.sbt.liquibase.diff.handler.hproperty;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.liquibase.diff.handler.HandlerCommonMethods;
import com.sbt.liquibase.diff.handler.PropertyHandler;
import com.sbt.liquibase.diff.handler.hclass.ClassHandlerNew;
import com.sbt.mg.ElementState;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.CollectionType;
import com.sbt.mg.data.model.PropertyType;
import com.sbt.mg.data.model.TypeInfo;
import com.sbt.mg.data.model.XmlEmbeddedList;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.NoFoundEmbeddedPropertyException;
import com.sbt.mg.exception.common.EmbeddedListNotFoundException;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.mg.utils.LiquibasePropertyUtils;
import com.sbt.model.utils.Models;
import com.sbt.parameters.enums.DBMS;
import com.sbt.reference.ExternalReferenceGenerator;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sbt.liquibase.diff.handler.HandlerCommonMethods.prepareDbmsReplacer;
import static com.sbt.liquibase.helper.Helper.createRemarks;
import static com.sbt.mg.ModelHelper.getOldPropertyDbType;
import static com.sbt.mg.ModelHelper.getOldPropertyDbType4IntermediaryBuild;
import static com.sbt.mg.ModelHelper.getPropertyDbType;
import static com.sbt.mg.Helper.getTemplate;
import static com.sbt.mg.ModelHelper.isBinaryType;
import static com.sbt.mg.ModelHelper.isBooleanType;
import static com.sbt.mg.ModelHelper.isPrimitiveType;
import static com.sbt.mg.utils.ClassUtils.isBaseClass;

public class PropertyHandlerUpdated implements PropertyHandler {

    private static final List<String> ALLOWED_LENGTH_CHANGE_TYPES = Arrays.asList("string", "unicodestring", "bigdecimal", "localdatetime", "offsetdatetime");

    private static final String ADD_NOTNULL_CONSTRAINT_TEMPLATE = getTemplate("/templates/changelog/addNotNullConstraint.changelog.template");
    private static final String DROP_NOTNULL_CONSTRAINT_TEMPLATE = getTemplate("/templates/changelog/dropNotNullConstraint.changelog.template");
    private static final String MODIFICATE_TYPE_TEMPLATE = getTemplate("/templates/changelog/modifyDataType.changelog.template");
    private static final String MODIFICATE_TYPE_WITHOUT_ROLLBACK_TEMPLATE = getTemplate("/templates/changelog/modifyDataType.withoutRollback.changelog.template");
    private static final String MODIFICATE_TYPE_PRECONDITIONS_TEMPLATE = getTemplate("/templates/changelog/preconditions.modifyDataType.changelog.template");
    private static final String RENAME_COLUMN_TEMPLATE = getTemplate("/templates/changelog/renameColumn.changelog.template");
    private static final String DROP_DEFAULT_VALUE_TEMPLATE = getTemplate("/templates/changelog/dropDefaultValue.changelog.template");
    private static final String ADD_DEFAULT_VALUE_ROLLBACK_TEMPLATE = getTemplate("/templates/changelog/addDefaultValue.rollback.changelog.template");
    private static final String COLUMN_REMARKS_TEMPLATE = getTemplate("/templates/changelog/columnRemarks.changelog.template");

    private static final String MIGRATE_TO_COLLECTION_TABLE_SQL_CHANGELOG_TEMPLATE = getTemplate("/templates/changelog/migrateToCollectTable.sql.dml.changelog.template");
    private static final String PRECONDITIONS_DROP_COLUMN_CHANGELOG_TEMPLATE = getTemplate("/templates/changelog/preconditions.dropColumn.changelog.template");

    private static final String MIGRATE_FROM_COLLECTION_TABLE_SQL_CHANGELOG_TEMPLATE = getTemplate("/templates/changelog/migrateFromCollectTable.sql.dml.changelog.template");

    private static final String MIGRATE_FROM_REFERENCE_ROOT_TO_MAPPEDBY_SQL_CHANGELOG_TEMPLATE = getTemplate("/templates/changelog/migrateFromReferenceRootToMappedBy.sql.dml.changelog.template");

    // templates without scripts oracle
    private static final String ADD_NOTNULL_CONSTRAINT_WITHOUT_ORACLE_TEMPLATE = getTemplate("/templates/changelog/withoutOracle/addNotNullConstraint.changelog.withoutOracle.template");
    private static final String DROP_DEFAULT_VALUE_WITHOUT_ORACLE_TEMPLATE = getTemplate("/templates/changelog/withoutOracle/dropDefaultValue.changelog.withoutOracle.template");
    private static final String DROP_NOTNULL_CONSTRAINT_WITHOUT_ORACLE_TEMPLATE = getTemplate("/templates/changelog/withoutOracle/dropNotNullConstraint.changelog.withoutOracle.template");
    //

    private final MutableInt collectionIndex;
    private final MutableInt indexIndex;

    public PropertyHandlerUpdated(MutableInt collectionIndex, MutableInt indexIndex) {
        this.collectionIndex = collectionIndex;
        this.indexIndex = indexIndex;
    }

    @Override
    public void handle(StringBuilder changesSB, MutableLong index, XmlModelClassProperty modelClassProperty,
                       XmlModelClass handledClass, ModelParameters modelParameters, PluginParameters pluginParameters) {
        if (!modelParameters.containsObjectInDiff(ElementState.UPDATED, modelClassProperty)) {
            return;
        }

        XmlModelClass prevClass = modelParameters.getImmutablePreviousPdmModel().getModel().getClass(handledClass.getName());

// There is a case when two changes in the model occur simultaneously:
//1 - class was ordinary, but became embeddable
// 2 - some change occurred to a property in BaseEntity
//The algorithm breaks down in this case because it treats the change from step 2 as something that should be
// reflected in embeddedPropertyList. But in fact, these changes do not depend on each other, since it becomes embeddable,
// class loses all connection with BaseEntity.
// The condition below checks these two conditions.
        if (handledClass.isEmbeddable() &&
            !prevClass.isEmbeddable() &&
            isBaseClass(modelClassProperty.getModelClass().getName())
        ) {
            return;
        }


        if (handledClass.isEmbeddable()) {
            if (modelClassProperty.isDeprecated()) {
                if (modelClassProperty.propertyChanged(XmlModelClassProperty.DEPRECATED_TAG)) {
                    changeDeprecated(changesSB, index, modelClassProperty, modelParameters, handledClass);
                }
            } else {
                List<XmlModelClass> classesWithEmbeddedTypes = modelParameters.getModel().getClassesAsList().stream()
                    .filter(clazz -> clazz.getPropertiesWithIncome().stream()
                        .anyMatch(it -> it.getType().equals(handledClass.getName())))
                    .collect(Collectors.toList());

//todo: filter(property -> !property.isDeprecated()) - workaround for intermediate releases.
                classesWithEmbeddedTypes.stream()
                    .filter(it -> !it.isAbstract())
                    .forEach(clazz ->
                        clazz.getPropertiesWithIncome().stream()
                            .filter(it -> it.getType().equals(handledClass.getName()))
                            .filter(property -> !property.isDeprecated())
                            .forEach(property -> {
                                String columnName = property.getModelClass().getEmbeddedPropertyList().stream()
                                    .filter(it -> Objects.equals(it.getName(), property.getName()))
                                    .findFirst()
                                    .orElseThrow(() -> new EmbeddedListNotFoundException(
                                        property.getName(),
                                        clazz.getName())
                                    )
                                    .getEmbeddedPropertyList().stream()
                                    .filter(it -> Objects.equals(it.getName(), modelClassProperty.getName()))
                                    .findFirst()
                                    .orElseThrow(() -> new NoFoundEmbeddedPropertyException(
                                        modelClassProperty.getName(),
                                        clazz)
                                    )
                                    .getColumnName();
                                handleChanges(changesSB, index, modelClassProperty, clazz, modelParameters, columnName, pluginParameters);
                            }));
            }

        } else {
            handleChanges(changesSB, index, modelClassProperty, handledClass, modelParameters, modelClassProperty.getColumnName(), pluginParameters);
        }
    }

    private void handleChanges(StringBuilder changesSB,
                               MutableLong index,
                               XmlModelClassProperty modelClassProperty,
                               XmlModelClass handledClass,
                               ModelParameters modelParameters,
                               String columnName,
                               PluginParameters pluginParameters) {

        XmlModel previousModel = modelParameters.getImmutablePreviousPdmModel().getModel();
        XmlModelClassProperty previousModelClassProperty = previousModel.getClass(modelClassProperty.getModelClass().getName()).getProperty(modelClassProperty.getName());
        Models.fillCategoryAndTypeInfo(previousModelClassProperty);

        if (modelClassProperty.propertyChanged(XmlModelClassProperty.TYPE_TAG)) {
            typeChanged(changesSB, index, modelClassProperty, modelParameters, handledClass, previousModelClassProperty, pluginParameters);
        }

        if (modelClassProperty.propertyChanged(XmlModelClassProperty.MANDATORY_TAG)) {
// If primitive or enum -> as before
// Otherwise, we do not respond
// If the obligation is lifted - it can always be done.
            if (isPrimitiveType(modelClassProperty.getType()) ||
                modelClassProperty.isEnum() ||
                Objects.equals(Boolean.TRUE, modelClassProperty.getOldValueChangedProperty(XmlModelClassProperty.MANDATORY_TAG))) {
                mandatoryChanged(changesSB, index, modelClassProperty, modelParameters, handledClass, columnName, pluginParameters);
            }
        }

        if (modelClassProperty.propertyChanged(XmlModelClassProperty.LENGTH_TAG)
            || modelClassProperty.propertyChanged(XmlModelClassProperty.PRECISION_TAG)
            || modelClassProperty.propertyChanged(XmlModelClassProperty.UNICODE_TAG)) {
            lengthOrPrecisionChanged(changesSB, index, modelClassProperty, modelParameters, handledClass, columnName, pluginParameters);
        }

        if (modelClassProperty.propertyChanged((XmlModelClassProperty.DEFAULT_VALUE_TAG))) {
            defaultValueChange(changesSB, index, modelClassProperty, modelParameters, handledClass, columnName, pluginParameters);
        }

        if (modelClassProperty.propertyChanged(XmlModelClassProperty.LABEL_TAG)) {
            labelChanged(changesSB, index, modelClassProperty, modelParameters, handledClass, columnName);
        }

        if (modelClassProperty.propertyChanged(XmlModelClassProperty.DEPRECATED_TAG) &&
//that case, if besides removing the column we are changing some other features, then the property
// mark as deprecated 2 times"
            !modelParameters.containsObjectInDiff(ElementState.DEPRECATED, modelClassProperty)) {
            changeDeprecated(changesSB, index, modelClassProperty, modelParameters, handledClass);
        }

        if (modelClassProperty.propertyChanged(XmlModelClassProperty.COLLECTION_TAG)) {
            collectionChanged(changesSB, index, modelClassProperty, modelParameters, handledClass, pluginParameters);
        }

        if (modelClassProperty.propertyChanged(XmlModelClassProperty.EMBEDDED_TAG)) {
            String tableName = modelClassProperty.getModelClass().getTableName();
            if (modelClassProperty.propertyChanged(XmlModelClassProperty.COLUMN_NAME_TAG)) {
                if (Objects.isNull(modelClassProperty.getColumnName())) {
//from the received changes, we assume that the property has become embedded,
//Therefore, the column was deleted.
                    String oldColumnName = modelClassProperty.getOldValueChangedProperty(XmlModelClassProperty.COLUMN_NAME_TAG);

                    String preconditions = PRECONDITIONS_DROP_COLUMN_CHANGELOG_TEMPLATE
                        .replace("${columnName}", oldColumnName)
                        .replace("${tableName}", tableName);


                    if (!modelClassProperty.isExternalLink() ||
                        (modelClassProperty.propertyChanged(XmlModelClassProperty.EXTERNAL_LINK_TAG) &&
                            (modelClassProperty.propertyChanged(XmlModelClassProperty.ORIGINAL_TYPE_TAG)
                                && Objects.nonNull(modelClassProperty.getOldValueChangedProperty(XmlModelClassProperty.ORIGINAL_TYPE_TAG))))) {
                        new HandlerCommonMethods().dropColumn(changesSB,
                            index,
                            modelParameters,
                            tableName,
                            oldColumnName,
                            preconditions,
                            "");
                    }
                }
            }

            if (modelClassProperty.isEmbedded()) {
                XmlModelClass modelClass =
                    modelClassProperty.getModelClass().getModel().getClass(modelClassProperty.getType());

                modelClass.getPropertiesAsList().stream().filter(property -> !property.isDeprecated()).forEach(property -> {

                    if (property.getName().equals(ExternalReferenceGenerator.ENTITY_ID) &&
                        (modelClassProperty.propertyChanged(XmlModelClassProperty.ORIGINAL_TYPE_TAG) &&
                            Objects.isNull(modelClassProperty.getOldValueChangedProperty(XmlModelClassProperty.ORIGINAL_TYPE_TAG)))) {
                        changesSB.append(RENAME_COLUMN_TEMPLATE
                            .replace("${modelName}", modelParameters.getModel().getModelName())
                            .replace("${version}", modelParameters.getVersion())
                            .replace("${index}", String.valueOf(index.incrementAndGet()))
                            .replace("${tableName}", handledClass.getTableName())
                            .replace("${columnName}", XmlModelClass.getEmbeddedProperty(modelClassProperty, property.getName()).getColumnName())
                            .replace("${oldColumnName}", previousModelClassProperty.getColumnName())
                            .replace("${dataType}", getPropertyDbType(modelClassProperty))
                            .replace("${remarks1}", createRemarks(modelClassProperty, modelClassProperty.isDeprecated())));
                    } else {

                        new HandlerCommonMethods(indexIndex).addSimpleColumn(
                            changesSB,
                            index,
                            XmlModelClass.getEmbeddedProperty(
                                modelClassProperty,
                                property.getName()).getColumnName(),
                            property,
                            handledClass,
                            modelParameters,
                            "",
                            "MARK_RAN",
                            pluginParameters);
                    }
                });
            } else {

                if (modelClassProperty.propertyChanged(XmlModelClassProperty.ORIGINAL_TYPE_TAG)
                    && modelClassProperty.getType().equals(modelClassProperty.getOldValueChangedProperty(XmlModelClassProperty.ORIGINAL_TYPE_TAG))
                    && modelClassProperty.getMappedBy() == null) {

                    changesSB.append(RENAME_COLUMN_TEMPLATE
                        .replace("${modelName}", modelParameters.getModel().getModelName())
                        .replace("${version}", modelParameters.getVersion())
                        .replace("${index}", String.valueOf(index.incrementAndGet()))
                        .replace("${tableName}", handledClass.getTableName())
                        .replace("${columnName}", modelClassProperty.getColumnName())
                        .replace("${oldColumnName}", XmlModelClass.getEmbeddedProperty(modelClassProperty, ExternalReferenceGenerator.ENTITY_ID).getColumnName())
                        .replace("${dataType}", getPropertyDbType(modelClassProperty))
                        .replace("${remarks1}", createRemarks(modelClassProperty, modelClassProperty.isDeprecated())));

                } else {

                    List<XmlEmbeddedList> embeddedPropertyList = modelClassProperty.getModelClass().getEmbeddedPropertyList();
                    Optional<XmlEmbeddedList> optionalXmlEmbeddedList = embeddedPropertyList.stream().filter(xmlEmbeddedList -> xmlEmbeddedList.getName().equals(modelClassProperty.getName())).findFirst();
                    if (optionalXmlEmbeddedList.isPresent()) {
                        XmlEmbeddedList xmlEmbeddedList = optionalXmlEmbeddedList.get();
                        xmlEmbeddedList.getEmbeddedPropertyList().forEach(embeddedProperty -> {
                            String preconditions = PRECONDITIONS_DROP_COLUMN_CHANGELOG_TEMPLATE
                                .replace("${columnName}", embeddedProperty.getColumnName())
                                .replace("${tableName}", tableName);

                            new HandlerCommonMethods().dropColumn(changesSB,
                                index,
                                modelParameters,
                                tableName,
                                embeddedProperty.getColumnName(),
                                preconditions,
                                "");
                            index.increment();
                        });
                        if (Objects.isNull(modelClassProperty.getMappedBy()) && Objects.nonNull(modelClassProperty.getColumnName())) {
                            new HandlerCommonMethods(indexIndex).addSimpleColumnWithoutRollback(
                                changesSB,
                                index,
                                modelClassProperty.getColumnName(),
                                modelClassProperty,
                                handledClass,
                                modelParameters,
                                "",
                                "MARK_RAN",
                                pluginParameters);
                        }
                        modelClassProperty.getModelClass().removeEmbeddedList(xmlEmbeddedList);
                    }
                }

            }
        }
    }


    private void collectionChanged(StringBuilder changesSB,
                                   MutableLong index,
                                   XmlModelClassProperty modelClassProperty,
                                   ModelParameters modelParameters,
                                   XmlModelClass handledClass,
                                   PluginParameters pluginParameters) {

        CollectionType oldCollectionType = modelClassProperty.getOldValueChangedProperty(XmlModelClassProperty.COLLECTION_TAG);
        index.increment();

        if (oldCollectionType == null) {

            ClassHandlerNew.addCollectionTable(changesSB, modelClassProperty, modelParameters, collectionIndex, index, indexIndex, pluginParameters);

            String migrateSql = MIGRATE_TO_COLLECTION_TABLE_SQL_CHANGELOG_TEMPLATE
                .replace("${collectionIndex}", collectionIndex.getValue().toString())
                .replace("${collectionTableName}", modelClassProperty.getCollectionTableName())
                .replace("${tableName}", modelClassProperty.getModelClass().getTableName())
                .replace("${keyColumnName}", modelClassProperty.getKeyColumnName())
                .replace("${columnName}", modelClassProperty.getColumnName());

            new HandlerCommonMethods().dropColumn(changesSB,
                index,
                modelParameters,
                modelClassProperty.getModelClass().getTableName(),
                modelClassProperty.getColumnName(),
                "",
                migrateSql);

        } else {

            XmlModel previousModel = modelParameters.getImmutablePreviousPdmModel().getModel();
            XmlModelClassProperty previousModelClassProperty = previousModel.getClass(modelClassProperty.getModelClass().getName()).getProperty(modelClassProperty.getName());

            if (Objects.isNull(modelClassProperty.getCollectionType())) {
                HandlerCommonMethods handlerCommonMethods = new HandlerCommonMethods(indexIndex);

                String collectionTableName = previousModelClassProperty.getCollectionTableName();

                //createColumn

                handlerCommonMethods.addSimpleColumn(changesSB,
                    index,
                    modelClassProperty.getColumnName(),
                    modelClassProperty,
                    handledClass,
                    modelParameters,
                    "",
                    "MARK_RAN",
                    pluginParameters);

                //dml

                String migrateSql = MIGRATE_FROM_COLLECTION_TABLE_SQL_CHANGELOG_TEMPLATE
                    .replace("${collectionTableName}", previousModelClassProperty.getCollectionTableName())
                    .replace("${tableName}", modelClassProperty.getModelClass().getTableName())
                    .replace("${keyColumnName}", previousModelClassProperty.getKeyColumnName())
                    .replace("${columnName}", modelClassProperty.getColumnName());

                //dropTable
                handlerCommonMethods.dropCollectionTable(changesSB, index, modelClassProperty.getModelClass(), previousModelClassProperty, collectionTableName, migrateSql, modelParameters);

            } else {
//todo: handle collection type change?
                throw new UnsupportedOperationException(String.format("The type of collection has been changed in the property [%s] of the class [%s]. Collection type change is not supported!",
                    handledClass.getName(), modelClassProperty.getName()));
            }

        }


    }

    private void typeChanged(StringBuilder changesSB,
                             MutableLong index,
                             XmlModelClassProperty modelClassProperty,
                             ModelParameters modelDiff,
                             XmlModelClass handledClass,
                             XmlModelClassProperty previousModelClassProperty,
                             PluginParameters pluginParameters) {
        String oldNameType = modelClassProperty.getOldValueChangedProperty(XmlModelClassProperty.TYPE_TAG);

        //primitive -> ...
        if (ModelHelper.isPrimitiveType(oldNameType)) {
            //... -> primitive
            if (modelClassProperty.getCategory() == PropertyType.PRIMITIVE) {
                if (previousModelClassProperty.getCollectionType() != null) {
                    //from collection primitive
                    changesSB.append(MODIFICATE_TYPE_WITHOUT_ROLLBACK_TEMPLATE
                        .replace("${modelName}", modelDiff.getModel().getModelName())
                        .replace("${version}", modelDiff.getVersion())
                        .replace("${index}", String.valueOf(index.incrementAndGet()))
                        .replace("${tableName}", previousModelClassProperty.getCollectionTableName())
                        .replace("${columnName}", modelClassProperty.getColumnName())
                        .replace("${dataType}", getPropertyDbType(modelClassProperty))
                        .replace("${oldDataType}", getOldPropertyDbType4IntermediaryBuild(previousModelClassProperty))
                        .replace("${additionalPrecondition}", "")
                    );
                } else {
                    changesSB.append(MODIFICATE_TYPE_TEMPLATE
                        .replace("${modelName}", modelDiff.getModel().getModelName())
                        .replace("${version}", modelDiff.getVersion())
                        .replace("${index}", String.valueOf(index.incrementAndGet()))
                        .replace("${tableName}", handledClass.getTableName())
                        .replace("${columnName}", modelClassProperty.getColumnName())
                        .replace("${dataType}", getPropertyDbType(modelClassProperty))
                        .replace("${oldDataType}", getOldPropertyDbType4IntermediaryBuild(previousModelClassProperty))
                        .replace("${additionalPrecondition}", "")
                    );
                }

            } else {
                //... -> not primitive
                if (modelClassProperty.getCollectionType() == null) {
                    String previousType = getOldPropertyDbType4IntermediaryBuild(previousModelClassProperty);
                    String newType = getPropertyDbType(modelClassProperty);
                    if (!previousType.equals(newType)) {
                        changesSB.append(MODIFICATE_TYPE_TEMPLATE
                            .replace("${modelName}", modelDiff.getModel().getModelName())
                            .replace("${version}", modelDiff.getVersion())
                            .replace("${index}", String.valueOf(index.incrementAndGet()))
                            .replace("${tableName}", handledClass.getTableName())
                            .replace("${columnName}", previousModelClassProperty.getColumnName())
                            .replace("${dataType}", newType)
                            .replace("${oldDataType}", previousType)
                            .replace("${additionalPrecondition}", "")
                        );
                    }

                    if (!modelClassProperty.isEmbedded()) {
                        if (modelClassProperty.propertyChanged(XmlModelClassProperty.COLUMN_NAME_TAG)) {
                            changesSB.append(RENAME_COLUMN_TEMPLATE
                                .replace("${modelName}", modelDiff.getModel().getModelName())
                                .replace("${version}", modelDiff.getVersion())
                                .replace("${index}", String.valueOf(index.incrementAndGet()))
                                .replace("${tableName}", handledClass.getTableName())
                                .replace("${columnName}", modelClassProperty.getColumnName())
                                .replace("${oldColumnName}", previousModelClassProperty.getColumnName())
                                .replace("${dataType}", getPropertyDbType(modelClassProperty))
                                .replace("${remarks1}", createRemarks(modelClassProperty, modelClassProperty.isDeprecated()))
                            );
                        }
                    }

                } else {
                    //collection not primitive
                }
            }
        } else {
            //not primitive -> ...
            //
            //... -> primitive
            if (modelClassProperty.getCategory() == PropertyType.PRIMITIVE) {
                if (modelClassProperty.getCollectionType() == null) {
                    String previousType = getOldPropertyDbType4IntermediaryBuild(previousModelClassProperty);
                    String newType = getPropertyDbType(modelClassProperty);
                    if (!previousType.equals(newType)) {
                        changesSB.append(MODIFICATE_TYPE_TEMPLATE
                            .replace("${modelName}", modelDiff.getModel().getModelName())
                            .replace("${version}", modelDiff.getVersion())
                            .replace("${index}", String.valueOf(index.incrementAndGet()))
                            .replace("${tableName}", handledClass.getTableName())
                            .replace("${columnName}", previousModelClassProperty.getColumnName())
                            .replace("${dataType}", newType)
                            .replace("${oldDataType}", previousType)
                            .replace("${additionalPrecondition}", "")
                        );
                    }

// null can be in cases: field was embedded (reference), field mappedBy or collection field
                    if (!Objects.isNull(previousModelClassProperty.getColumnName())) {
                        changesSB.append(RENAME_COLUMN_TEMPLATE
                            .replace("${modelName}", modelDiff.getModel().getModelName())
                            .replace("${version}", modelDiff.getVersion())
                            .replace("${index}", String.valueOf(index.incrementAndGet()))
                            .replace("${tableName}", handledClass.getTableName())
                            .replace("${columnName}", modelClassProperty.getColumnName())
                            .replace("${oldColumnName}", previousModelClassProperty.getColumnName())
                            .replace("${dataType}", getPropertyDbType(modelClassProperty))
                            .replace("${remarks1}", createRemarks(modelClassProperty, modelClassProperty.isDeprecated()))
                        );
                    }

                } else {
                    //collection primitive
                }
            } else {
                //... -> not primitive
                if (modelClassProperty.getCollectionType() == null) {

                    if (!modelClassProperty.isEmbedded()) {

                        if (!modelClassProperty.propertyChanged(XmlModelClassProperty.EXTERNAL_LINK_TAG)) {

                            new HandlerCommonMethods().dropColumn(changesSB,
                                index,
                                modelDiff,
                                handledClass.getTableName(),
                                modelClassProperty.getColumnName(),
                                "",
                                "");

                            if (modelClassProperty.getMappedBy() == null) {
                                new HandlerCommonMethods(indexIndex).addSimpleColumn(
                                    changesSB,
                                    index,
                                    modelClassProperty.getColumnName(),
                                    modelClassProperty,
                                    handledClass,
                                    modelDiff,
                                    "",
                                    "MARK_RAN",
                                    pluginParameters);
                            }
                        }
                    }

                } else {
                    //collection not primitive
                }
            }
        }
    }

    private void labelChanged(StringBuilder changesSB,
                              MutableLong index,
                              XmlModelClassProperty modelClassProperty,
                              ModelParameters modelDiff,
                              XmlModelClass handledClass,
                              String columnName) {
// collections do not have columns in the object
        if (modelClassProperty.getCollectionType() != null) {
            return;
        }
// general case where there is no column
        if (Objects.isNull(modelClassProperty.getColumnName())) {
            return;
        }
        appendColumnRemarks(changesSB, index, modelClassProperty, modelDiff, columnName, handledClass);
    }

    private void appendColumnRemarks(StringBuilder changesSB,
                                     MutableLong index,
                                     XmlModelClassProperty modelClassProperty,
                                     ModelParameters modelDiff,
                                     String columnName,
                                     XmlModelClass handledClass) {
        String oldLabel = modelClassProperty.getOldValueChangedProperty(XmlModelClassProperty.LABEL_TAG);
        changesSB.append(COLUMN_REMARKS_TEMPLATE
            .replace("${modelName}", modelDiff.getModel().getModelName())
            .replace("${version}", modelDiff.getVersion())
            .replace("${index}", String.valueOf(index.incrementAndGet()))
            .replace("${remarks1}", createRemarks(modelClassProperty, modelClassProperty.isDeprecated()))
            .replace("${remarks_back_1}", createRemarks(oldLabel, modelClassProperty.getName(), modelClassProperty.isDeprecated()))
            .replace("${columnName}", columnName)
            .replace("${tableName}", handledClass.getTableName()));
    }


    private void changeDeprecated(StringBuilder changesSB,
                                  MutableLong index,
                                  XmlModelClassProperty modelClassProperty,
                                  ModelParameters modelDiff,
                                  XmlModelClass handledClass) {

// collections do not have columns in the object
        if (modelClassProperty.getCollectionType() != null) {
            return;
        }

        if (modelClassProperty.isEmbedded() ||
            (modelClassProperty.propertyChanged(XmlModelClassProperty.EMBEDDED_TAG) && !modelClassProperty.isEmbedded())) {
            XmlModelClass.getEmbeddedList(modelClassProperty).getEmbeddedPropertyList()
                .forEach(it ->
                    changeDeprecatedRemarks(changesSB, index, modelClassProperty, modelDiff, it.getColumnName(), handledClass)
                );
        } else {
//that case, if we converted the class to embedded, we do not set deprecation remarks on columns, as the entire table will be deleted
//this transformation is possible only with an intermediate release (intermediaryBuild)
// TODO  (PII) And do we need to put it in the previous transformation too?
            if (!handledClass.propertyChanged(XmlModelClass.EMBEDDED_TAG)) {
                changeDeprecatedRemarks(changesSB, index, modelClassProperty, modelDiff, modelClassProperty.getColumnName(), handledClass);
            }
        }
    }

    private void changeDeprecatedRemarks(StringBuilder changesSB,
                                         MutableLong index,
                                         XmlModelClassProperty modelClassProperty,
                                         ModelParameters modelDiff,
                                         String columnName, XmlModelClass handledClass) {
        Boolean oldDeprecatedValue = modelClassProperty.getOldValueChangedProperty(XmlModelClassProperty.DEPRECATED_TAG);
        changesSB.append(COLUMN_REMARKS_TEMPLATE
            .replace("${modelName}", modelDiff.getModel().getModelName())
            .replace("${version}", modelDiff.getVersion())
            .replace("${index}", String.valueOf(index.incrementAndGet()))
            .replace("${remarks1}", createRemarks(modelClassProperty, modelClassProperty.isDeprecated()))
            .replace("${remarks_back_1}", createRemarks(modelClassProperty, oldDeprecatedValue))
            .replace("${columnName}", columnName)
            .replace("${tableName}", handledClass.getTableName()));
    }


    private void defaultValueChange(
        StringBuilder changesSB,
        MutableLong index,
        XmlModelClassProperty property,
        ModelParameters modelDiff,
        XmlModelClass handledClass,
        String columnName,
        PluginParameters pluginParameters) {

        String oldDefaultValue = property.getOldValueChangedProperty(XmlModelClassProperty.DEFAULT_VALUE_TAG);

        index.increment();
        if (oldDefaultValue != null) {
            if (isBinaryType(property)) {
                if (!pluginParameters.isDisableGenerateOracleLiquibase()) {
                    addDropDefaultValueTo(changesSB, index, property, modelDiff, handledClass, DBMS.ORACLE, columnName, pluginParameters);
                    index.increment();
                }
                addDropDefaultValueTo(changesSB, index, property, modelDiff, handledClass, DBMS.H2, columnName, pluginParameters);
                index.increment();
                addDropDefaultValueTo(changesSB, index, property, modelDiff, handledClass, DBMS.POSTGRES, columnName, pluginParameters);
            } else if (isBooleanType(property.getType())) {
                if (!pluginParameters.isDisableGenerateOracleLiquibase()) {
                    addDropDefaultValueTo(changesSB, index, property, modelDiff, handledClass, DBMS.ORACLE, columnName, pluginParameters);
                    index.increment();
                }
                addDropDefaultValueTo(changesSB, index, property, modelDiff, handledClass, DBMS.NO_ORACLE, columnName, pluginParameters);
            } else {
                addDropDefaultValueTo(changesSB, index, property, modelDiff, handledClass, DBMS.ANY, columnName, pluginParameters);
            }
        }
    }

    private void addDropDefaultValueTo(StringBuilder changesSB,
                                       MutableLong index,
                                       XmlModelClassProperty property,
                                       ModelParameters modelDiff,
                                       XmlModelClass handledClass,
                                       DBMS dbms,
                                       String columnName,
                                       PluginParameters pluginParameters) {

        String templateDropDefValue = DROP_DEFAULT_VALUE_TEMPLATE;

        if (pluginParameters.isDisableGenerateOracleLiquibase()) {
            templateDropDefValue = DROP_DEFAULT_VALUE_WITHOUT_ORACLE_TEMPLATE;
        }

        changesSB.append(templateDropDefValue
            .replace("${modelName}", modelDiff.getModel().getModelName())
            .replace("${dbms}", prepareDbmsReplacer(dbms))
            .replace("${version}", modelDiff.getVersion())
            .replace("${index}", String.valueOf(index.getValue()))
            .replace("${dataType}", getPropertyDbType(property))
            .replace("${columnName}", columnName)
            .replace("${tableName}", handledClass.getTableName())
            .replace("${defValueRollback}", pluginParameters.isDisableCompatibilityCheck() ? "<rollback/>" : getDefaultValueRollback(property, handledClass, dbms))
        );
    }

    private String getDefaultValueRollback(XmlModelClassProperty property, XmlModelClass handledClass, DBMS dbms) {
        return ADD_DEFAULT_VALUE_ROLLBACK_TEMPLATE
            .replace("${dataType}", getPropertyDbType(property))
            .replace("${columnName}", property.getColumnName())
            .replace("${tableName}", handledClass.getTableName())
            .replace("${oldDefaultValue}", LiquibasePropertyUtils.computeOldDefaultValue(property, dbms));
    }

    private void mandatoryChanged(StringBuilder changesSB,
                                  MutableLong index,
                                  XmlModelClassProperty property,
                                  ModelParameters modelDiff,
                                  XmlModelClass handledClass,
                                  String columnName,
                                  PluginParameters pluginParameters) {
        index.increment();
        if (property.isMandatory()) {
            String addNotNullTemplate = ADD_NOTNULL_CONSTRAINT_TEMPLATE;
            if (pluginParameters.isDisableGenerateOracleLiquibase()) {
                addNotNullTemplate = ADD_NOTNULL_CONSTRAINT_WITHOUT_ORACLE_TEMPLATE;
            }

            changesSB.append(addNotNullTemplate
                .replace("${modelName}", modelDiff.getModel().getModelName())
                .replace("${version}", modelDiff.getVersion())
                .replace("${index}", String.valueOf(index.getValue()))
                .replace("${tableName}", handledClass.getTableName())
                .replace("${columnName}", columnName)
                .replace("${type}", getPropertyDbType(property))
                .replace("${oracleValue}", oracleDefaultMandatoryValue(property))
                .replace("${postgreValue}", postgreDefaultMandatoryValue(property))
                .replace("${h2Value}", h2DefaultMandatoryValue(property))
            );
        } else {
            String dropNotNullTemplate = DROP_NOTNULL_CONSTRAINT_TEMPLATE;
            if (pluginParameters.isDisableGenerateOracleLiquibase()) {
                dropNotNullTemplate = DROP_NOTNULL_CONSTRAINT_WITHOUT_ORACLE_TEMPLATE;
            }
            changesSB.append(dropNotNullTemplate
                .replace("${modelName}", modelDiff.getModel().getModelName())
                .replace("${version}", modelDiff.getVersion())
                .replace("${index}", String.valueOf(index.getValue()))
                .replace("${tableName}", handledClass.getTableName())
                .replace("${columnName}", property.getColumnName())
                .replace("${type}", getPropertyDbType(property))
                .replace("${rollback}", pluginParameters.isDisableCompatibilityCheck() ? "\n\t\t<rollback/>" : "")
            );
        }
    }

    private CharSequence h2DefaultMandatoryValue(XmlModelClassProperty property) {
        TypeInfo typeInfo = property.getTypeInfo();
        String defaultValue = property.getDefaultValue();
        switch (typeInfo.getJavaName()) {
            case "String":
            case "Character":
                return '\'' + defaultValue + '\'';
            case "Boolean":
            case "BigDecimal":
            case "Integer":
            case "Short":
            case "Long":
            case "Byte":
            case "Float":
            case "Double":
                return defaultValue;
            case "byte[]":
                return LiquibasePropertyUtils.h2Base64DecodeFunction(property.getDefaultValue());
            case "LocalDate":
                if (Objects.equals("now", defaultValue)) {
                    return "CURRENT_DATE";
                }
                return "TO_DATE('" + defaultValue + "', 'YYYY-MM-DD')";
            case "Date":
            case "OffsetDateTime":
            case "LocalDateTime":
                if (Objects.equals("now", defaultValue)) {
                    return "CURRENT_TIMESTAMP(3)";
                }
                return "TO_TIMESTAMP('" + defaultValue + "', 'YYYY-MM-DD HH24:MI:SS')";
            default:
                return "";
        }
    }

    private CharSequence postgreDefaultMandatoryValue(XmlModelClassProperty property) {
        TypeInfo typeInfo = property.getTypeInfo();
        String defaultValue = property.getDefaultValue();
        switch (typeInfo.getJavaName()) {
            case "String":
            case "Character":
                return '\'' + defaultValue + '\'';
            case "Boolean":
            case "BigDecimal":
            case "Integer":
            case "Short":
            case "Long":
            case "Byte":
            case "Float":
            case "Double":
                return defaultValue;
            case "byte[]":
                return LiquibasePropertyUtils.postgresBase64DecodeFunction(property.getDefaultValue());
            case "LocalDate":
                if (Objects.equals("now", defaultValue)) {
                    return "CURRENT_DATE";
                }
                return "TO_DATE('" + defaultValue + "', 'YYYY-MM-DD')";
            case "Date":
            case "LocalDateTime":
            case "OffsetDateTime":
                if (Objects.equals("now", defaultValue)) {
                    return "CURRENT_TIMESTAMP(3)";
                }
                return "TO_TIMESTAMP('" + defaultValue + "', 'YYYY-MM-DD HH24:MI:SS')";
            default:
                return "";
        }
    }

    private CharSequence oracleDefaultMandatoryValue(XmlModelClassProperty property) {
        TypeInfo typeInfo = property.getTypeInfo();
        String defaultValue = property.getDefaultValue();
        switch (typeInfo.getJavaName()) {
            case "String":
            case "Character":
                return '\'' + defaultValue + '\'';
            case "Boolean":
                return "true".equalsIgnoreCase(property.getDefaultValue()) ? "1" : "0";
            case "BigDecimal":
            case "Integer":
            case "Short":
            case "Long":
            case "Byte":
            case "Float":
            case "Double":
                return defaultValue;
            case "byte[]":
                return LiquibasePropertyUtils.oracleBase64DecodeFunction(property.getDefaultValue());
            case "LocalDate":
                if (Objects.equals("now", defaultValue)) {
                    return "CURRENT_DATE";
                }
                return "TO_DATE('" + defaultValue + "', 'YYYY-MM-DD')";
            case "Date":
            case "LocalDateTime":
            case "OffsetDateTime":
                if (Objects.equals("now", defaultValue)) {
                    return "CURRENT_TIMESTAMP(3)";
                }
                return "TO_TIMESTAMP('" + defaultValue + "', 'YYYY-MM-DD HH24:MI:SS')";
            default:
                return "";
        }
    }

    private void lengthOrPrecisionChanged(StringBuilder changesSB,
                                          MutableLong index,
                                          XmlModelClassProperty modelClassProperty,
                                          ModelParameters modelDiff,
                                          XmlModelClass handledClass,
                                          String columnName,
                                          PluginParameters pluginParameters) {
        String tableName;
        if (modelClassProperty.getCollectionType() != null) {
            tableName = modelClassProperty.getCollectionTableName();
        } else {
            tableName = handledClass.getTableName();
        }

        boolean allowedLengthChange = modelClassProperty.getCategory() == PropertyType.PRIMITIVE &&
            ALLOWED_LENGTH_CHANGE_TYPES.contains(modelClassProperty.getType().toLowerCase(Locale.ENGLISH));

        String oraclePreconditionTemplate = "\n\t\t\t\t<and><dbms type=\"oracle\"/><sqlCheck expectedResult=\"0\">%s</sqlCheck></and>";
        String additionalPreconditions = allowedLengthChange ?
            MODIFICATE_TYPE_PRECONDITIONS_TEMPLATE
                .replace("${oraclePrecondition}", pluginParameters.isDisableGenerateOracleLiquibase() ?
                    "" :
                    String.format(oraclePreconditionTemplate, getModifyTypePrecondition(tableName, columnName, modelClassProperty, DBMS.ORACLE)))
                .replace("${postgresqlSQL}", getModifyTypePrecondition(tableName, columnName, modelClassProperty, DBMS.POSTGRES))
                .replace("${h2SQL}", getModifyTypePrecondition(tableName, columnName, modelClassProperty, DBMS.H2)) :
            "";

        changesSB.append(MODIFICATE_TYPE_WITHOUT_ROLLBACK_TEMPLATE
            .replace("${modelName}", modelDiff.getModel().getModelName())
            .replace("${version}", modelDiff.getVersion())
            .replace("${index}", String.valueOf(index.incrementAndGet()))
            .replace("${tableName}", tableName)
            .replace("${columnName}", columnName)
            .replace("${dataType}", getPropertyDbType(modelClassProperty))
            .replace("${oldDataType}", getOldPropertyDbType(modelClassProperty))
            .replace("${additionalPrecondition}", additionalPreconditions)
        );
    }

    private String getModifyTypePrecondition(String tableName, String columnName, XmlModelClassProperty modelClassProperty, DBMS dbmsType) {
        String baseDMLOracle = String.format("select count(1) from sys.user_tab_columns where table_name=upper('%s') and column_name=upper('%s') and ", tableName, columnName);
        String baseDMLPostgresql = String.format("select count(1) from INFORMATION_SCHEMA.COLUMNS where table_schema='${defaultSchemaName}' and table_name=lower('%s') and column_name=lower('%s') and ", tableName, columnName);
        String baseDMLH2 = String.format("select count(1) FROM INFORMATION_SCHEMA.COLUMNS where table_name=upper('%s') and column_name=upper('%s') and ", tableName, columnName);

        if (dbmsType == DBMS.ORACLE) {
            if (modelClassProperty.getType().toLowerCase(Locale.ENGLISH).equals("localdatetime") ||
                modelClassProperty.getType().toLowerCase(Locale.ENGLISH).equals("offsetdatetime")) {

                return baseDMLOracle + String.format("DATA_SCALE > %s", modelClassProperty.getLength());

            } else if (modelClassProperty.getType().toLowerCase(Locale.ENGLISH).equals("bigdecimal")) {
                return baseDMLOracle + String.format("DATA_PRECISION > %s and DATA_SCALE > %s", modelClassProperty.getLength(), modelClassProperty.getScale());

            } else /*if (modelClassProperty.getType().toLowerCase(Locale.ENGLISH).equals("string") ||
                    modelClassProperty.getType().toLowerCase(Locale.ENGLISH).equals("unicodestring"))*/ {

                return baseDMLOracle + String.format("CHAR_LENGTH > %s", modelClassProperty.getLength());
            }

        } else if ((dbmsType == DBMS.POSTGRES) ||
            (dbmsType == DBMS.H2)) {
            StringBuilder result = new StringBuilder();
            result.append((dbmsType == DBMS.POSTGRES) ? baseDMLPostgresql : baseDMLH2);
            if (modelClassProperty.getType().toLowerCase(Locale.ENGLISH).equals("localdatetime") ||
                modelClassProperty.getType().toLowerCase(Locale.ENGLISH).equals("offsetdatetime")) {
                result.append(String.format("datetime_precision > %s", modelClassProperty.getLength()));
            } else if (modelClassProperty.getType().toLowerCase(Locale.ENGLISH).equals("bigdecimal")) {
                result.append(String.format("numeric_precision > %s and numeric_scale > %s", modelClassProperty.getLength(), modelClassProperty.getScale()));
            } else /*if (modelClassProperty.getType().toLowerCase(Locale.ENGLISH).equals("string") ||
                    modelClassProperty.getType().toLowerCase(Locale.ENGLISH).equals("unicodestring"))*/ {
                result.append(String.format("character_maximum_length > %s", modelClassProperty.getLength()));
            }
            return result.toString();
        } else {
            throw new UnsupportedOperationException("Unknown DB type - " + dbmsType);
        }
    }
}
