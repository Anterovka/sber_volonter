package com.sbt.liquibase.diff.handler.hproperty;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.liquibase.diff.handler.HandlerCommonMethods;
import com.sbt.liquibase.diff.handler.PropertyHandler;
import com.sbt.mg.ElementState;
import com.sbt.mg.data.model.PropertyType;
import com.sbt.mg.data.model.XmlEmbeddedList;
import com.sbt.mg.data.model.XmlEmbeddedProperty;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableLong;

import java.util.Optional;

public class PropertyHandlerRemoved implements PropertyHandler {
//todo to the dropColumn template: Think about how to add <changeLogPropertyDefined name="enableDropRemovedItems" value="true"/> so that 1) when the column is missing, the changeset is marked MARK_RAN, and when the enableDropRemovedItems = true property is missing, the change set can be executed again the next time

    @Override
    public void handle(StringBuilder changesSB, MutableLong index, XmlModelClassProperty modelClassProperty,
                       XmlModelClass handledClass, ModelParameters modelDiff, PluginParameters pluginParameters) {
        if (!modelDiff.containsObjectInDiff(ElementState.REMOVED, modelClassProperty)) {
            return;
        }

        if (modelClassProperty.isExternalLink()) {
            addDropForRemovedProperty(changesSB, index, modelClassProperty, null, handledClass, modelDiff, pluginParameters);
        } else {
            if(handledClass.isEmbeddable()) {
                addDropForRemovedEmbeddedClassProperty(changesSB, index, modelClassProperty, handledClass, modelDiff, pluginParameters);
            } else {
                addDropForRemovedProperty(changesSB, index, modelClassProperty, handledClass, modelDiff, pluginParameters);
            }
        }
    }

    private void addDropForRemovedEmbeddedClassProperty(StringBuilder changesSB,
                                                        MutableLong index,
                                                        XmlModelClassProperty modelClassProperty,
                                                        XmlModelClass handledClass,
                                                        ModelParameters modelDiff,
                                                        PluginParameters pluginParameters) {
        XmlModel model = modelDiff.getModel();
        model.getClassesAsList().stream()
//get class that has property which type is embedded class (handledClass)
                .filter(xmlModelClass -> xmlModelClass.getPropertiesAsList().stream()
                        .anyMatch(property -> property.getType().equals(handledClass.getName())))
                .forEach(xmlModelClass -> {

                    xmlModelClass.getPropertiesAsList().stream()
                            .filter(property -> property.getType().equals(handledClass.getName()))
                            .forEach(xmlModelClassProperty -> {

// Retrieves an embedded list related to the found property
                                Optional<XmlEmbeddedList> optionalXmlEmbeddedList = xmlModelClass.getEmbeddedPropertyList().stream()
                                        .filter(xmlEmbeddedList1 -> xmlEmbeddedList1.getName().equals(xmlModelClassProperty.getName()))
                                        .findFirst();
                                if (optionalXmlEmbeddedList.isPresent()) {
                                    XmlEmbeddedList xmlEmbeddedList = optionalXmlEmbeddedList.get();
// we get an element from the embedded list that relates to the property of the embedded class (handledClass)
                                    Optional<XmlEmbeddedProperty> optionalXmlEmbeddedProperty = xmlEmbeddedList.getEmbeddedPropertyList().stream()
                                            .filter(xmlEmbeddedProperty1 -> xmlEmbeddedProperty1.getName().equals(modelClassProperty.getName()))
                                            .findFirst();
                                    if(optionalXmlEmbeddedProperty.isPresent()) {
                                        XmlEmbeddedProperty xmlEmbeddedProperty = optionalXmlEmbeddedProperty.get();
                                        new HandlerCommonMethods().dropColumn(changesSB,
                                                index,
                                                modelDiff,
                                                xmlModelClass.getTableName(),
                                                xmlEmbeddedProperty.getColumnName(),
                                                "",
                                                "");
                                    }
                                }

                            });
                });

    }

    private void addDropForRemovedProperty(StringBuilder changesSB,
                                           MutableLong index,
                                           XmlModelClassProperty modelClassProperty,
                                           XmlModelClass propertyModelClass,
                                           ModelParameters modelDiff,
                                           PluginParameters pluginParameters) {
        addDropForRemovedProperty(changesSB, index, modelClassProperty, null, propertyModelClass, modelDiff, pluginParameters);
    }

    private void addDropForRemovedProperty(StringBuilder changesSB,
                                           MutableLong index,
                                           XmlModelClassProperty modelClassProperty,
                                           String columnName,
                                           XmlModelClass propertyModelClass,
                                           ModelParameters modelDiff,
                                           PluginParameters pluginParameters) {
        if (modelClassProperty.getCollectionType() != null) {
            String tableName = modelClassProperty.getCollectionTableName();
            if (modelClassProperty.getCategory().equals(PropertyType.PRIMITIVE)
                    && StringUtils.isNotBlank(tableName)) {
                new HandlerCommonMethods().dropCollectionTable(changesSB, index, propertyModelClass, modelClassProperty, tableName, "", modelDiff);
            }
        } else {
            dropColumn(changesSB, index, modelClassProperty, columnName, propertyModelClass, modelDiff, pluginParameters);
        }
    }

    private void dropColumn(StringBuilder changesSB,
                            MutableLong index,
                            XmlModelClassProperty modelClassProperty,
                            String columnName,
                            XmlModelClass propertyModelClass,
                            ModelParameters modelDiff,
                            PluginParameters pluginParameters) {

        if (modelClassProperty.isEmbedded()) {
            XmlModelClass.getEmbeddedList(modelClassProperty).getEmbeddedPropertyList()
                    .forEach(xmlEmbeddedProperty -> {
                        new HandlerCommonMethods().dropColumn(changesSB,
                                index,
                                modelDiff,
                                propertyModelClass.getTableName(),
                                xmlEmbeddedProperty.getColumnName(),
                                "",
                                "");

                    });
        } else {
            new HandlerCommonMethods().dropColumn(changesSB,
                    index,
                    modelDiff,
                    propertyModelClass.getTableName(),
                    columnName == null ? modelClassProperty.getColumnName() : columnName,
                    "",
                    "");
        }
    }

}
