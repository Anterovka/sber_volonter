package com.sbt.model.diff.query;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.ParameterContext;
import com.sbt.mg.ElementState;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlQuery;
import com.sbt.mg.data.model.XmlQueryId;
import com.sbt.mg.data.model.XmlQueryProperty;
import com.sbt.model.diff.DiffHandler;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class QueryPropertyDiff implements DiffHandler {
    private static final Logger LOGGER = Logger.getLogger(QueryPropertyDiff.class.getName());

    @Override
    public void handler(XmlModel newModel, XmlModel baseModel, ParameterContext parameterContext) {
        ModelParameters modelDiff = parameterContext.getModelParameters();
        LOGGER.info("\n\n  The phase of checking the properties of user requests (beginning)");
// handles changes to undeleted custom query properties
        checkDiff(newModel, baseModel, modelDiff);
        checkIdDiff(newModel, baseModel, modelDiff);
        LOGGER.info("\n\n  Phase of checking properties of user requests (completion)");
    }

    /**
     * Handles changes to unremoved properties
     */
    private void checkDiff(XmlModel newModel, XmlModel baseModel, ModelParameters modelDiff) {
        baseModel.getQueriesAsList().forEach(xmlQuery -> {
// If the request is deleted, then we skip it
            if (!newModel.containsQuery(xmlQuery.getName())) {
                return;
            }

            XmlQuery newQuery = newModel.getQuery(xmlQuery.getName());

            xmlQuery.getPropertiesAsList().forEach(prevProperty -> {
// If the property is deleted, then we skip
                Optional<XmlQueryProperty> optionalNewProperty = newQuery.getProperties().stream().filter(prop -> prop.getName().equals(prevProperty.getName())).findAny();
                if (!optionalNewProperty.isPresent()) {
                    return;
                }

                XmlQueryProperty newProperty = optionalNewProperty.get();

                AtomicBoolean propertyUpdated = new AtomicBoolean(false);

                checkType(prevProperty, newProperty, propertyUpdated);
                checkLabelAndDescription(prevProperty, newProperty, propertyUpdated);

                if (propertyUpdated.get()) {
                    modelDiff.addDiffObject(ElementState.UPDATED, prevProperty);
                }
            });
        });
    }

    private void checkType(XmlQueryProperty prevProperty,
                           XmlQueryProperty newProperty,
                           AtomicBoolean propertyUpdated) {

        if (!prevProperty.getType().equals(newProperty.getType())) {
            propertyUpdated.set(true);

            String oldType = prevProperty.getType();
            prevProperty.addChangedProperty(XmlQueryProperty.TYPE_TAG, oldType);
            prevProperty.setType(newProperty.getType());
        }
    }

    private void checkLabelAndDescription(XmlQueryProperty prevProperty,
                            XmlQueryProperty newProperty,
                            AtomicBoolean propertyUpdated) {
        if (!Objects.equals(newProperty.getLabel(), prevProperty.getLabel())) {
            propertyUpdated.set(true);
            prevProperty.addChangedProperty(XmlQueryProperty.LABEL_TAG, prevProperty.getLabel());
            prevProperty.setLabel(newProperty.getLabel());
        }

        if (!Objects.equals(newProperty.getDescription(), prevProperty.getDescription())) {
            propertyUpdated.set(true);
            prevProperty.addChangedProperty(XmlQueryProperty.DESCRIPTION_TAG, prevProperty.getDescription());
            prevProperty.setDescription(newProperty.getDescription());
        }
    }

    private void checkIdDiff(XmlModel newModel, XmlModel baseModel, ModelParameters modelDiff) {
        baseModel.getQueriesAsList().forEach(xmlQuery -> {
            // Skipping deleted requests
            if (!newModel.containsQuery(xmlQuery.getName())) {
                return;
            }

            XmlQueryId curId = xmlQuery.getId();
            XmlQueryId prevId = newModel.getQuery(xmlQuery.getName()).getId();
            boolean changed = false;

            if (curId == null && prevId == null) {
                return;
            }

            if (curId != null && prevId == null) {
                modelDiff.addDiffObject(ElementState.UPDATED, curId);
                return;
            }

            if (curId == null) {
                modelDiff.addDiffObject(ElementState.UPDATED, prevId);
                return;
            }

            if (!Objects.equals(curId.getName(), prevId.getName())) {
                curId.addChangedProperty(XmlQueryId.NAME_TAG, prevId.getName());
                changed = true;
            }

            if (!Objects.equals(curId.getDescription(), prevId.getDescription())) {
                curId.addChangedProperty(XmlQueryId.DESCRIPTION_TAG, prevId.getDescription());
                changed = true;
            }

            if (!Objects.equals(curId.getLabel(), prevId.getLabel())) {
                curId.addChangedProperty(XmlQueryId.LABEL_TAG, prevId.getLabel());
                changed = true;
            }

            if (changed) {
                modelDiff.addDiffObject(ElementState.UPDATED, curId);
            }
        });
    }
}
