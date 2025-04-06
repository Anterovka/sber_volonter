package com.sbt.liquibase.diff.handler.hclass;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.mg.ElementState;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.ClassStrategy;
import com.sbt.mg.data.model.XmlModelClass;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class ClassHandlerNon extends ClassHandlerBase {

    private final MutableLong classIndex;

    public ClassHandlerNon(MutableLong classIndex, MutableInt indexIndex, MutableInt indexCollection) {
        super(indexCollection, indexIndex);
        this.classIndex = classIndex;
    }

    @Override
    public void handle(StringBuilder changesSB, MutableLong index, XmlModelClass modelClass, ModelParameters modelDiff, PluginParameters pluginParameters) {
        if (modelClass.isAbstract()) {
            return;
        }

        // it is necessary to disable the logic of general processing of the class if its strategy has changed to JOINED,
        // т.к. логика обработки будет в ClassHandlerUpdate
        // (specifically index handling: deletion and creation after table creation)
        // analogously when changing the class abstraction property
        //todo: in this case, we will not be able to apply changes to the properties of the transformed class in the current release!!!
        //Although in the cases listed below, the table and indexes will be created from scratch in the ClassHandlerUpdate...
        if ((modelClass.propertyChanged(XmlModelClass.STRATEGY_TAG) && modelClass.getStrategy() == ClassStrategy.JOINED) ||
                (modelClass.propertyChanged(XmlModelClass.ABSTRACT_TAG) && !modelClass.isAbstract()) ||
                (modelClass.propertyChanged(XmlModelClass.EMBEDDED_TAG) && !modelClass.isEmbeddable())) {
            return;
        }

        String oldTableName = modelClass.getOldValueChangedProperty(XmlModelClass.TABLE_NAME_TAG);
        String tableName = Objects.nonNull(oldTableName) ? oldTableName : modelClass.getTableName();

        //During intermediate releases, when switching types, it is possible to delete a column.
        // In this regard, updating (deleting and creating a new) index is separated because
        // there may be a situation where the field will be deleted, and the index on this field will be deleted already after that.
        filterIndexByElementState(ElementState.UPDATED, modelDiff, modelClass).forEach(xmlIndex -> {
            if (modelClass.propertyChanged(XmlModelClass.STRATEGY_TAG) && modelClass.getStrategy() == ClassStrategy.SINGLE_TABLE) {
                dropIndexForRemoved(changesSB,
                        xmlIndex,
                        modelDiff.getVersion(),
                        classIndex.getValue().toString(),
                        modelClass.getModel().getModelName(),
                        tableName,
                        pluginParameters);
            } else {
                dropIndexForUpdate(changesSB,
                        xmlIndex,
                        modelDiff.getVersion(),
                        classIndex.getValue().toString(),
                        modelClass.getModel().getModelName(),
                        tableName,
                        pluginParameters);
            }
        });

        // ** deletion is moved before modifying properties for the same reasons as separating index update (described above)
        filterIndexByElementState(ElementState.REMOVED, modelDiff, modelClass).forEach(xmlIndex ->
                dropIndexForRemoved(changesSB, xmlIndex, modelDiff.getVersion(), classIndex.getValue().toString(),
                        modelClass.getModel().getModelName(), tableName, pluginParameters));

        XmlModelClass extendedClass = modelClass.getExtendedClass();
        boolean strategyChangedFromJoined = modelClass.propertyChanged(XmlModelClass.STRATEGY_TAG)
                && modelClass.getOldValueChangedProperty(XmlModelClass.STRATEGY_TAG) == ClassStrategy.JOINED;

        boolean extendedClassIsAbstractAndStrategyChangedFromJoined = Objects.nonNull(extendedClass) && extendedClass.isAbstract() &&
                (extendedClass.propertyChanged(XmlModelClass.STRATEGY_TAG)
                        && extendedClass.getOldValueChangedProperty(XmlModelClass.STRATEGY_TAG) == ClassStrategy.JOINED
                );

        if (strategyChangedFromJoined && extendedClassIsAbstractAndStrategyChangedFromJoined) {
            // This branch should only be executed in case of converting inheritance strategy from JOINED to SINGLE_TABLE for a class.
            //and if this class has an ancestor, it is abstract and its strategy has also changed.
            // Example: tests LiquibaseRollbackTest.changeStrategyJoinedToSingleTest and LiquibaseGenTest.changeStrategyJoinedToSingleTest
            //The purpose of this modification:
            //when converting the inheritance strategy from JOINED to SINGLE_TABLE, we need to move all columns to the root ancestor,
            // everything works fine except when there are abstract classes among them and they have descendants.
            // In this case, the columns from the abstract class, located on the descendants of this class,
            // transferred to the root as many times as there are descendant classes.
            // This is not an issue for adding a column (as there is a precondition for addition that such a column does not exist).
            //However, the problem for rollback is that it will try to execute as many times as columns were added.
            // This will cause a rollback error with Liquibase and make a rollback impossible.
            //Algorithm meaning:
            //We each time receive descendants of the higher class, sort them, and look whether the current class is
            // first in the list of descendants. If so, we add scripts for creating columns of the abstract class in the root class,
            // if not - accordingly abstract columns are not added.
            // At the same time, we add all the columns that do not belong to the abstract class

            Set<XmlModelClass> childClasses = new TreeSet<>(Comparator.comparing(XmlModelClass::getName));
            childClasses.addAll(ModelHelper.getChildClasses(extendedClass, false));
            XmlModelClass xmlModelClass = childClasses.stream().findFirst().get();

            modelClass.getPropertiesWithIncome().forEach(modelClassProperty -> {
                if ((modelClass.getName().equals(xmlModelClass.getName()) &&
                        modelClassProperty.getModelClass().getName().equals(extendedClass.getName())) ||
                        !modelClassProperty.getModelClass().getName().equals(extendedClass.getName())) {
                    propertyHandlerMap.get(ElementState.NEW).handle(changesSB, index, modelClassProperty, modelClass, modelDiff, pluginParameters);
                }
                propertyHandlerMap.get(ElementState.UPDATED).handle(changesSB, index, modelClassProperty, modelClass, modelDiff, pluginParameters);
                propertyHandlerMap.get(ElementState.DEPRECATED).handle(changesSB, index, modelClassProperty, modelClass, modelDiff, pluginParameters);
                propertyHandlerMap.get(ElementState.REMOVED).handle(changesSB, index, modelClassProperty, modelClass, modelDiff, pluginParameters);
            });

        } else {
            modelClass.getPropertiesWithIncome().forEach(modelClassProperty -> {
                propertyHandlerMap.get(ElementState.NEW).handle(changesSB, index, modelClassProperty, modelClass, modelDiff, pluginParameters);
                propertyHandlerMap.get(ElementState.UPDATED).handle(changesSB, index, modelClassProperty, modelClass, modelDiff, pluginParameters);
                propertyHandlerMap.get(ElementState.DEPRECATED).handle(changesSB, index, modelClassProperty, modelClass, modelDiff, pluginParameters);
                propertyHandlerMap.get(ElementState.REMOVED).handle(changesSB, index, modelClassProperty, modelClass, modelDiff, pluginParameters);
            });
        }

        filterIndexByElementState(ElementState.NEW, modelDiff, modelClass).forEach(xmlIndex ->
                createIndex(changesSB, classIndex, modelClass, xmlIndex, modelDiff, pluginParameters));

        filterIndexByElementState(ElementState.UPDATED, modelDiff, modelClass).forEach(xmlIndex ->
                createIndex(changesSB, classIndex, modelClass, xmlIndex, modelDiff, pluginParameters)
        );

    }


}
