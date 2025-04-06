package com.sbt.liquibase.diff.handler.hclass;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.liquibase.diff.handler.HandlerCommonMethods;
import com.sbt.mg.ElementState;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.ClassStrategy;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sbt.liquibase.helper.Helper.createRemarks;
import static com.sbt.mg.Helper.getTemplate;
import static com.sbt.mg.ModelHelper.getChildClasses;
import static com.sbt.mg.jpa.JpaConstants.OBJECT_ID;

public class ClassHandlerUpdate extends ClassHandlerBase {
    private static final String TABLE_REMARKS_TEMPLATE = getTemplate("/templates/changelog/tableRemarks.changelog.template");
    private static final String MIGRATE_FROM_JOIN_TO_SINGLE_TABLE_STRATEGY_SQL_CHANGELOG_TEMPLATE = getTemplate("/templates/changelog/migrateJoinToSingleTable.sql.dml.changelog.template");
    private static final String MIGRATE_FROM_SINGLE_TABLE_TO_JOINED_STRATEGY_SQL_CHANGELOG_TEMPLATE = getTemplate("/templates/changelog/migrateSingleTableToJoined.sql.dml.changelog.template");


    public ClassHandlerUpdate(MutableLong index, MutableInt indexCollection, MutableInt indexIndex) {
        super(index, indexCollection, indexIndex);
    }

    @Override
    public void handle(StringBuilder changesSB, MutableLong index, XmlModelClass modelClass, ModelParameters modelDiff, PluginParameters pluginParameters) {
        if (!modelDiff.containsObjectInDiff(ElementState.UPDATED, modelClass)) {
            return;
        }

        if (modelClass.propertyChanged(XmlModelClass.ABSTRACT_TAG)) {
            changeAbstract(changesSB, index, modelClass, modelDiff, pluginParameters);
            return;
        }

        if(Boolean.TRUE.equals(modelClass.isAbstract())) {
            return;
        }

        // There is no physics for an embedded label
        if (modelClass.propertyChanged(XmlModelClass.LABEL_TAG) && !modelClass.isEmbeddable()) {
            labelChanged(changesSB, index, modelClass, modelDiff);
        }

        if (modelClass.propertyChanged(XmlModelClass.STRATEGY_TAG)) {
            changeStrategy(changesSB, index, modelClass, modelDiff, pluginParameters);
        }

        if (modelClass.propertyChanged(XmlModelClass.EMBEDDED_TAG)) {
            if (modelClass.isEmbeddable()) {
            // means that earlier the class was not embeddable
                String tableName = modelClass.getOldValueChangedProperty(XmlModelClass.TABLE_NAME_TAG);
                if (!Objects.isNull(tableName)) {
                    new HandlerCommonMethods().dropTable(changesSB, index, tableName, "", modelDiff);
                }
            } else {
                createClass(changesSB, index, modelClass, modelDiff, pluginParameters);
            }
        }
    }

    private void changeAbstract(StringBuilder changesSB, MutableLong index, XmlModelClass modelClass, ModelParameters modelDiff, PluginParameters pluginParameters) {
        Boolean oldValueAbstractTag = modelClass.getOldValueChangedProperty(XmlModelClass.ABSTRACT_TAG);
        if (Boolean.TRUE.equals(oldValueAbstractTag)) {
            //became not abstract

            List<XmlModelClass> inheritedClasses = modelClass.getModel().getClassesAsList().stream().filter(xmlModelClass -> modelClass.getName().equals(xmlModelClass.getExtendedClassName())).collect(Collectors.toList());
            inheritedClasses.stream().forEach(xmlModelClass -> {
                filterIndexByElementState(ElementState.UPDATED, modelDiff, xmlModelClass).forEach(xmlIndex -> {
                    index.increment();
                    dropIndexForRemoved(changesSB, xmlIndex, modelDiff.getVersion(), index.toString(),
                            xmlModelClass.getModel().getModelName(), xmlModelClass.getTableName(), pluginParameters);
                });
            });

            // createTable (with indexes)
            createClass(changesSB, index, modelClass, modelDiff, pluginParameters);

            //dml migrate
            //todo: it is not clear whether it is possible to perform migration in this case. The main thing is how?

            //dropColumns
            inheritedClasses.forEach(xmlModelClass -> {
                xmlModelClass.getPropertiesWithIncome().stream()
                        .filter(property -> modelClass.getName().equals(property.getModelClass().getName()))
                        .filter(property -> Objects.nonNull(property.getColumnName()))
                        .filter(property -> Objects.nonNull(property.getModelClass().getTableName()))
                        .filter(property -> !OBJECT_ID.equals(property.getName()))
                        .forEach(property ->
                            new HandlerCommonMethods().dropColumn(changesSB, index, modelDiff, xmlModelClass.getTableName(), property.getColumnName(), "", "")
                        );
            });

        } else {
            //became abstract


        }
    }

    private void changeStrategy(StringBuilder changesSB, MutableLong index, XmlModelClass modelClass, ModelParameters modelDiff, PluginParameters pluginParameters) {
        ClassStrategy oldStrategy = modelClass.getOldValueChangedProperty(XmlModelClass.STRATEGY_TAG);
        if (Objects.isNull(oldStrategy) || oldStrategy == ClassStrategy.JOINED) {

            if (ModelHelper.isModelClassBaseMark(modelClass)) {

            } else {
                if (modelClass.propertyChanged(XmlModelClass.TABLE_NAME_TAG)) {
                    String oldTableName = modelClass.getOldValueChangedProperty(XmlModelClass.TABLE_NAME_TAG);
                    String tableName = modelClass.getTableName();
                    StringBuilder migrateSql = new StringBuilder();

                    modelClass.getPropertiesWithIncome().stream().filter(XmlModelClassProperty::isUserProperty).forEach(property -> {
                        String columnName = property.getColumnName();
                        migrateSql.append(getMigrateFromJoinedToSingleTableStrategySql(tableName, oldTableName, columnName));
                    });

                    new HandlerCommonMethods().dropTable(changesSB, index, oldTableName, migrateSql.toString(), modelDiff);

                }
            }

        } else {
            if (modelClass.propertyChanged(XmlModelClass.TABLE_NAME_TAG)) {
                String oldTableName = modelClass.getOldValueChangedProperty(XmlModelClass.TABLE_NAME_TAG);
                String tableName = modelClass.getTableName();

                filterIndexByElementState(ElementState.UPDATED, modelDiff, modelClass).forEach(xmlIndex -> {
                    index.increment();
                    String tableNameForDrop = Objects.nonNull(oldTableName) ? oldTableName : modelClass.getTableName();
                    dropIndexForRemoved(changesSB,
                            xmlIndex,
                            modelDiff.getVersion(),
                            index.toString(),
                            modelClass.getModel().getModelName(),
                            tableNameForDrop,
                            pluginParameters);
                });

                // createClass creates both the table and indexes
                createClass(changesSB, index, modelClass, modelDiff, pluginParameters);

                //dml insert
                String columnNames = modelClass.getPropertiesWithIncome().stream()
                        .filter(property -> Objects.nonNull(property.getColumnName()))
                        .map(XmlModelClassProperty::getColumnName).collect(Collectors.joining(","));

                if (StringUtils.isNotBlank(columnNames)) {
                    migrateFromSingleTableToJoinedStrategySql(changesSB, index, modelClass, tableName, oldTableName, columnNames, modelDiff);
                }

                //dropColumns
                // determining if the class is a descendant of an abstract and it is the last in order,
                // only then do we delete the fields of the abstract class from the base.
                if (isLastInOrderChildOfAbstractClass(modelClass)) {
                    modelClass.getPropertiesWithIncome().stream()
                            .filter(property -> Objects.nonNull(property.getColumnName()))
                            .filter(property -> !OBJECT_ID.equals(property.getName()))
                            .forEach(property ->
                                    new HandlerCommonMethods().dropColumn(changesSB, index, modelDiff, oldTableName, property.getColumnName(), "", "")
                            );
                } else {
                    modelClass.getPropertiesAsList().stream()
                            .filter(property -> Objects.nonNull(property.getColumnName()))
                            .forEach(property ->
                                    new HandlerCommonMethods().dropColumn(changesSB, index, modelDiff, oldTableName, property.getColumnName(), "", "")
                            );
                }
            }
        }
    }


    private boolean isLastInOrderChildOfAbstractClass(XmlModelClass modelClass) {
        XmlModelClass extendedClass = modelClass.getExtendedClass();
        if (Boolean.TRUE.equals(extendedClass.isAbstract())) {
            List<XmlModelClass> childClasses = new ArrayList<>(getChildClasses(extendedClass));
            Optional<String> optionalNameModelClass = childClasses.stream()
                    .map(XmlModelClass::getName)
                    .min(Comparator.reverseOrder());
            if (optionalNameModelClass.isPresent() && modelClass.getName().equals(optionalNameModelClass.get())) {
                return true;
            }
        }
        return false;
    }

    private String getMigrateFromJoinedToSingleTableStrategySql(String tableName, String oldTableName, String columnName) {
        return MIGRATE_FROM_JOIN_TO_SINGLE_TABLE_STRATEGY_SQL_CHANGELOG_TEMPLATE
                .replace("${tableName}", tableName)
                .replace("${oldTableName}", oldTableName)
                .replace("${columnName}", columnName);
    }

    private void migrateFromSingleTableToJoinedStrategySql(StringBuilder changesSB, MutableLong index, XmlModelClass modelClass, String tableName, String oldTableName, String columnNames, ModelParameters modelDiff) {
        changesSB.append(MIGRATE_FROM_SINGLE_TABLE_TO_JOINED_STRATEGY_SQL_CHANGELOG_TEMPLATE
                .replace("${modelName}", modelDiff.getModel().getModelName())
                .replace("${version}", modelDiff.getVersion())
                .replace("${index}", String.valueOf(index.incrementAndGet()))
                .replace("${tableName}", tableName)
                .replace("${oldTableName}", oldTableName)
                .replace("${columnNames}", columnNames)
                .replace("${nameEntity}", modelClass.getName())
        );
    }

    private void labelChanged(StringBuilder changesSB, MutableLong index, XmlModelClass modelClass, ModelParameters modelDiff){
        String name = modelClass.getName();
        String label = modelClass.getLabel();
        String oldLabel = modelClass.getOldValueChangedProperty(XmlModelClass.LABEL_TAG);
        changesSB.append(TABLE_REMARKS_TEMPLATE
                .replace("${modelName}", modelDiff.getModel().getModelName())
                .replace("${version}", modelDiff.getVersion())
                .replace("${index}", String.valueOf(index.incrementAndGet()))
                .replace("${remarks2}", createRemarks(label, name, false))
                .replace("${remarks_back_2}", createRemarks(oldLabel, name, false))
                .replace("${tableName}", modelClass.getTableName()));
    }

}
