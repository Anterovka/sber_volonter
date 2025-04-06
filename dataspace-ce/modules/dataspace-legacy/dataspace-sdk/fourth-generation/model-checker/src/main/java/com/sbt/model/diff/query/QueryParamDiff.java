package com.sbt.model.diff.query;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.ParameterContext;
import com.sbt.mg.ElementState;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlQuery;
import com.sbt.mg.data.model.XmlQueryParam;
import com.sbt.model.diff.DiffHandler;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class QueryParamDiff implements DiffHandler {
    private static final Logger LOGGER = Logger.getLogger(QueryParamDiff.class.getName());

    @Override
    public void handler(XmlModel newModel, XmlModel baseModel, ParameterContext parameterContext) {
        ModelParameters modelDiff = parameterContext.getModelParameters();
        LOGGER.info("\n\n  The phase of checking the parameters of the user request (beginning)");
// processes changes in parameters of the user's query
        checkDiff(newModel, baseModel, modelDiff);
        LOGGER.info("\n\n  Phase of properties checking (completion)");
    }

    /**
     * Handles changes to unremoved properties
     */
    private void checkDiff(XmlModel newModel, XmlModel baseModel, ModelParameters modelDiff) {
        baseModel.getQueriesAsList().forEach(xmlQuery -> {
// If the class is deleted, then we skip it
            if (!newModel.containsQuery(xmlQuery.getName())) {
                return;
            }

            XmlQuery newXmlQuery = newModel.getQuery(xmlQuery.getName());

            xmlQuery.getParams().forEach(prevParam -> {
                Optional<XmlQueryParam> optionalNewParam = newXmlQuery.getParams().stream().filter(param -> param.getName().equals(prevParam.getName())).findAny();
// If the parameter is deleted, we skip
                if (!optionalNewParam.isPresent()) {
                    return;
                }

                XmlQueryParam newParam = optionalNewParam.get();

                AtomicBoolean paramUpdated = new AtomicBoolean(false);

                checkType(prevParam, newParam, paramUpdated);
                checkLabelAndDescription(prevParam, newParam, paramUpdated);
                checkDefaultValue(prevParam, newParam, paramUpdated);
                checkCollection(prevParam, newParam, paramUpdated);
                checkMask(prevParam, newParam, paramUpdated);
                checkLength(prevParam, newParam, paramUpdated);

                if (paramUpdated.get()) {
                    modelDiff.addDiffObject(ElementState.UPDATED, prevParam);
                }
            });

        });
    }

    private void checkType(XmlQueryParam prevParam,
                           XmlQueryParam newParam,
                           AtomicBoolean paramUpdated) {

        if (!prevParam.getType().equals(newParam.getType())) {
            paramUpdated.set(true);
            prevParam.addChangedProperty(XmlQueryParam.TYPE_TAG, prevParam.getType());
            prevParam.setType(newParam.getType());
        }
    }

    private void checkLabelAndDescription(XmlQueryParam prevParam,
                            XmlQueryParam newParam,
                            AtomicBoolean paramUpdated) {
        if (!Objects.equals(newParam.getLabel(), prevParam.getLabel())) {
            prevParam.addChangedProperty(XmlQueryParam.LABEL_TAG, prevParam.getLabel());
            prevParam.setLabel(newParam.getLabel());
            paramUpdated.set(true);
        }

        if (!Objects.equals(newParam.getDescription(), prevParam.getDescription())) {
            prevParam.addChangedProperty(XmlQueryParam.DESCRIPTION_TAG, prevParam.getDescription());
            prevParam.setDescription(newParam.getDescription());
            paramUpdated.set(true);
        }
    }

    private void checkDefaultValue(XmlQueryParam prevParam,
                                   XmlQueryParam newParam,
                                   AtomicBoolean paramUpdated) {
        if (!Objects.equals(newParam.getDefaultValue(), prevParam.getDefaultValue())) {
            prevParam.addChangedProperty(XmlQueryParam.DEFAULT_VALUE_TAG, prevParam.getDefaultValue());
            prevParam.setDefaultValue(newParam.getDefaultValue());
            paramUpdated.set(true);
        }
    }

    private void checkMask(XmlQueryParam prevParam,
                           XmlQueryParam newParam,
                           AtomicBoolean paramUpdated) {
        if (!Objects.equals(prevParam.getMask(), newParam.getMask())) {
            prevParam.addChangedProperty(XmlQueryParam.MASK_TAG, prevParam.getMask());
            prevParam.setMask(newParam.getMask());
            paramUpdated.set(true);
        }
    }

    private void checkLength(XmlQueryParam prevParam,
                             XmlQueryParam newParam,
                             AtomicBoolean paramUpdated) {
        if (!Objects.equals(prevParam.getLength(), newParam.getLength())) {
            prevParam.addChangedProperty(XmlQueryParam.LENGTH_TAG, prevParam.getLength());
            prevParam.setLength(newParam.getLength());
            paramUpdated.set(true);
        }
    }

    private void checkCollection(XmlQueryParam prevParam,
                                 XmlQueryParam newParam,
                                 AtomicBoolean paramUpdated) {
        Boolean prevIsCollection = prevParam.getCollection();
        if (!Objects.equals(newParam.getCollection(), prevIsCollection)) {
            prevParam.setCollection(newParam.getCollection());
            prevParam.addChangedProperty(XmlQueryParam.COLLECTION_TAG, prevIsCollection);
            paramUpdated.set(true);
        }
    }
}
