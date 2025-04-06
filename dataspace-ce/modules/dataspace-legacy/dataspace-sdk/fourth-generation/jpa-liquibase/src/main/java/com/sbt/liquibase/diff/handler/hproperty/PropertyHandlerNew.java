package com.sbt.liquibase.diff.handler.hproperty;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.liquibase.diff.handler.HandlerCommonMethods;
import com.sbt.liquibase.diff.handler.PropertyHandler;
import com.sbt.liquibase.diff.handler.hclass.ClassHandlerNew;
import com.sbt.mg.ElementState;
import com.sbt.mg.data.model.ClassStrategy;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.parameters.enums.ObjectLinks;
import com.sbt.reference.ExternalReferenceGenerator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;

import static com.sbt.mg.Helper.getTemplate;

public class PropertyHandlerNew implements PropertyHandler {
    private static final String ON_FAIL_ACTION = "MARK_RAN";

    private static final String MIGRATE_FROM_MAPPEDBY_TO_REFERENCE_ROOT_SQL_CHANGELOG_TEMPLATE = getTemplate("/templates/changelog/migrateFromMappedByToReferenceRoot.sql.dml.changelog.template");

    private final MutableInt collectionIndex;
    private final MutableInt indexIndex;

    public PropertyHandlerNew(MutableInt collectionIndex, MutableInt indexIndex) {
        this.collectionIndex = collectionIndex;
        this.indexIndex = indexIndex;
    }

    @Override
    public void handle(StringBuilder changesSB, MutableLong index, XmlModelClassProperty modelClassProperty,
                       XmlModelClass handledClass, ModelParameters modelDiff, PluginParameters pluginParameters) {

        if (handledClass.isEmbeddable()
                || (modelClassProperty.getModelClass().getStrategy() == ClassStrategy.SINGLE_TABLE
                    && modelClassProperty.getOldValueChangedProperty("implementClass") != null
                    && !modelClassProperty.getOldValueChangedProperty("implementClass").equals(handledClass)
                )
        ) {
            return;
        }

// logic of adding the field of embedded property, if this property has registered a type change, is implemented in PropertyHandlerUpdated.typeChanged
// (primitive -> not primitive (isEmbedded) and not primitive -> not primitive (isEmbedded))
        if (modelClassProperty.isEmbedded() && modelClassProperty.propertyChanged(XmlModelClassProperty.TYPE_TAG)
                && !Boolean.TRUE.equals(modelClassProperty.isExternalLink())) {
            return;
        }

        if (modelDiff.containsObjectInDiff(ElementState.NEW, modelClassProperty)) {
            addNewProperty(
                    changesSB,
                    index,
                    modelClassProperty,
                    handledClass,
                    modelDiff,
                    null,
                    pluginParameters
            );
        } else if (modelClassProperty.isEmbedded() && !modelClassProperty.propertyChanged(XmlModelClassProperty.EMBEDDED_TAG)) {
// if a change of type is recorded on the property, it will be processed in the PropertyHandlerUpdate
            if (!modelClassProperty.propertyChanged(XmlModelClassProperty.TYPE_TAG)) {
                modelDiff.getObjectByType(ElementState.NEW, XmlModelClassProperty.class).stream()
                        .filter(it -> modelClassProperty.getType().equals(it.getModelClass().getName()))
                        .forEach(newEmbeddedProperty -> addNewProperty(
                                changesSB,
                                index,
                                newEmbeddedProperty,
                                handledClass,
                                modelDiff,
                                modelClassProperty.getName(),
                                pluginParameters)
                        );
            }

        }
    }

    private void addNewProperty(
            StringBuilder changesSB,
            MutableLong index,
            XmlModelClassProperty newClassProperty,
            XmlModelClass handledClass,
            ModelParameters modelDiff,
            String noEmbeddedPropertyName,
            PluginParameters pluginParameters) {

        if (newClassProperty.getCollectionType() == null) {
            if (newClassProperty.getModelClass().isEmbeddable() && !handledClass.isEmbeddable()) {
                // partial adding of embedded class properties
                new HandlerCommonMethods(indexIndex).addSimpleColumn(
                        changesSB,
                        index,
                        XmlModelClass.getEmbeddedProperty(
                                handledClass.getPropertyWithHierarchyNullable(noEmbeddedPropertyName),
                                newClassProperty.getName()).getColumnName(),
                        newClassProperty,
                        handledClass,
                        modelDiff,
                        "",
                        ON_FAIL_ACTION,
                        pluginParameters);
            } else if (newClassProperty.isEmbedded()) {
                XmlModelClass xmlModelClassByType =
                        newClassProperty.getModelClass().getModel().getClass(newClassProperty.getType());

//todo: filter(property -> !property.isDeprecated()) - workaround for intermediate releases.
                //In theory, situations where a column with deprecated = true should not occur.
                xmlModelClassByType.getPropertiesAsList().stream()
                        .filter(property -> !property.isDeprecated())
                        .forEach(property -> {

                            String embeddedColumnName = XmlModelClass.getEmbeddedProperty(
                                    newClassProperty,
                                    property.getName()).getColumnName();

                            new HandlerCommonMethods(indexIndex).addSimpleColumn(
                                    changesSB,
                                    index,
                                    embeddedColumnName,
                                    property,
                                    handledClass,
                                    modelDiff,
                                    "",
                                    ON_FAIL_ACTION,
                                    pluginParameters);

                            if (property.getName().equals(ExternalReferenceGenerator.ENTITY_ID)) {
                                addMappedByToReferenceRootMigrationSQL(changesSB, modelDiff, index, newClassProperty, embeddedColumnName);
                            }

                        });
            } else {
                if (newClassProperty.getObjectLinks() == ObjectLinks.O2O && newClassProperty.getMappedBy() != null) {
                    return;
                }

                new HandlerCommonMethods(indexIndex).addSimpleColumn(changesSB,
                        index,
                        newClassProperty.getColumnName(),
                        newClassProperty,
                        handledClass,
                        modelDiff,
                        "",
                        ON_FAIL_ACTION,
                        pluginParameters);
            }
        } else {
            index.increment();
            ClassHandlerNew.addCollectionTable(
                    changesSB,
                    newClassProperty,
                    modelDiff,
                    collectionIndex,
                    index,
                    indexIndex,
                    pluginParameters);
        }
    }

    private void addMappedByToReferenceRootMigrationSQL(StringBuilder changesSB, ModelParameters modelDiff, MutableLong index, XmlModelClassProperty newClassProperty, String columnName) {
        if (newClassProperty.propertyChanged(XmlModelClassProperty.MAPPED_BY_TAG)) {
            String oldMappedBy = newClassProperty.getOldValueChangedProperty(XmlModelClassProperty.MAPPED_BY_TAG);
            if (StringUtils.isNotBlank(oldMappedBy)) {
                String originalType = newClassProperty.getOriginalType();
                if (StringUtils.isNotBlank(originalType)) {
                    if (newClassProperty.propertyChanged(XmlModelClassProperty.TYPE_TAG) &&
                            !originalType.equals(newClassProperty.getOldValueChangedProperty(XmlModelClassProperty.TYPE_TAG))) {
                        return;
                    }
                    XmlModelClass xmlModelClassByOriginalType = newClassProperty.getModelClass().getModel().getClass(originalType);
                    XmlModelClassProperty parentProperty = xmlModelClassByOriginalType.getProperty(oldMappedBy);
                    String parentPropertyColumnName = parentProperty.getColumnName();
                    if (StringUtils.isNotBlank(parentPropertyColumnName)) {

                        changesSB.append(MIGRATE_FROM_MAPPEDBY_TO_REFERENCE_ROOT_SQL_CHANGELOG_TEMPLATE
                                .replace("${version}", modelDiff.getVersion())
                                .replace("${index}", String.valueOf(index.incrementAndGet()))
                                .replace("${modelName}", modelDiff.getModel().getModelName())
                                .replace("${tableName}", newClassProperty.getModelClass().getTableName())
                                .replace("${columnName}", columnName)
                                .replace("${tableName2}", xmlModelClassByOriginalType.getTableName())
                                .replace("${parentColumnName}", parentPropertyColumnName));
                    }
                }
            }
        }
    }

}
