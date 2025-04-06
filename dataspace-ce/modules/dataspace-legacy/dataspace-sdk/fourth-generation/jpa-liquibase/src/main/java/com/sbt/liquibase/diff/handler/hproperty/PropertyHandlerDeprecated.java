package com.sbt.liquibase.diff.handler.hproperty;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.liquibase.diff.handler.PropertyHandler;
import com.sbt.mg.ElementState;
import com.sbt.mg.data.model.XmlEmbeddedList;
import com.sbt.mg.data.model.XmlEmbeddedProperty;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import org.apache.commons.lang3.mutable.MutableLong;

import java.util.Optional;

import static com.sbt.liquibase.helper.Helper.createRemarks;
import static com.sbt.mg.Helper.getTemplate;

public class PropertyHandlerDeprecated implements PropertyHandler {
    private static final String COLUMN_REMARKS_WITHOUT_ROLLBACK_TEMPLATE = getTemplate("/templates/changelog/columnRemarks.withoutRollback.changelog.template");

    @Override
    public void handle(StringBuilder changesSB, MutableLong index, XmlModelClassProperty modelClassProperty,
                       XmlModelClass handledClass, ModelParameters modelDiff, PluginParameters pluginParameters) {
        if (!modelDiff.containsObjectInDiff(ElementState.DEPRECATED, modelClassProperty)) {
            return;
        }

        if (modelClassProperty.isExternalLink()) {
            addRemarkForRemovedProperty(changesSB, index, modelClassProperty, null, handledClass, modelDiff);
        } else {
            if(handledClass.isEmbeddable()) {
                addRemarkForRemovedEmbeddedClassProperty(changesSB, index, modelClassProperty, handledClass, modelDiff);
            } else {
                addRemarkForRemovedProperty(changesSB, index, modelClassProperty, handledClass, modelDiff);
            }
        }
    }

    private void addRemarkForRemovedEmbeddedClassProperty(StringBuilder changesSB,
                                                          MutableLong index,
                                                          XmlModelClassProperty modelClassProperty,
                                                          XmlModelClass handledClass,
                                                          ModelParameters modelDiff) {
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

                                    if (optionalXmlEmbeddedProperty.isPresent()) {
                                        XmlEmbeddedProperty xmlEmbeddedProperty = optionalXmlEmbeddedProperty.get();

                                        changesSB.append(COLUMN_REMARKS_WITHOUT_ROLLBACK_TEMPLATE
                                                .replace("${modelName}", modelDiff.getModel().getModelName())
                                                .replace("${version}", modelDiff.getVersion())
                                                .replace("${index}", String.valueOf(index.incrementAndGet()))
                                                .replace("${remarks1}", createRemarks(modelClassProperty, modelClassProperty.isDeprecated()))
                                                .replace("${remarks_back_1}", createRemarks(modelClassProperty, false))
                                                .replace("${columnName}", xmlEmbeddedProperty.getColumnName())
                                                .replace("${tableName}", xmlModelClass.getTableName()));
                                    }
                                }
                            });

                });
    }

    private void addRemarkForRemovedProperty(StringBuilder changesSB,
                                             MutableLong index,
                                             XmlModelClassProperty modelClassProperty,
                                             XmlModelClass propertyModelClass,
                                             ModelParameters modelDiff) {
        addRemarkForRemovedProperty(changesSB, index, modelClassProperty, null, propertyModelClass, modelDiff);
    }

    private void addRemarkForRemovedProperty(StringBuilder changesSB,
                                             MutableLong index,
                                             XmlModelClassProperty modelClassProperty,
                                             String columnName,
                                             XmlModelClass propertyModelClass,
                                             ModelParameters modelDiff) {
// collections do not have columns in the object
        if (modelClassProperty.getCollectionType() != null) {
            return;
        }
        if (modelClassProperty.isEmbedded()) {
            XmlModelClass.getEmbeddedList(modelClassProperty).getEmbeddedPropertyList()
                    .forEach(xmlEmbeddedProperty -> {
                        changesSB.append(COLUMN_REMARKS_WITHOUT_ROLLBACK_TEMPLATE
                                .replace("${modelName}", modelDiff.getModel().getModelName())
                                .replace("${version}", modelDiff.getVersion())
                                .replace("${index}", String.valueOf(index.incrementAndGet()))
                                .replace("${remarks1}", createRemarks(modelClassProperty, modelClassProperty.isDeprecated()))
                                .replace("${remarks_back_1}", createRemarks(modelClassProperty, false))
                                .replace("${columnName}", xmlEmbeddedProperty.getColumnName())
                                .replace("${tableName}", propertyModelClass.getTableName()));
                    });
        } else {
            String curColumnName = columnName == null ? modelClassProperty.getColumnName() : columnName;
            changesSB.append(COLUMN_REMARKS_WITHOUT_ROLLBACK_TEMPLATE
                    .replace("${modelName}", modelDiff.getModel().getModelName())
                    .replace("${version}", modelDiff.getVersion())
                    .replace("${index}", String.valueOf(index.incrementAndGet()))
                    .replace("${remarks1}", createRemarks(modelClassProperty, modelClassProperty.isDeprecated()))
                    .replace("${remarks_back_1}", createRemarks(modelClassProperty, false))
                    .replace("${columnName}", curColumnName)
                    .replace("${tableName}", propertyModelClass.getTableName()));
        }
    }
}
