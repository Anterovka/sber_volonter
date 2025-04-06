package com.sbt.model.diff.query;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.ParameterContext;
import com.sbt.mg.ElementState;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlQuery;
import com.sbt.mg.data.model.XmlQuerySql;
import com.sbt.model.diff.DiffHandler;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class QuerySqlDiff implements DiffHandler {
    private static final Logger LOGGER = Logger.getLogger(QuerySqlDiff.class.getName());

    @Override
    public void handler(XmlModel newModel, XmlModel baseModel, ParameterContext parameterContext) {
        ModelParameters modelDiff = parameterContext.getModelParameters();
        LOGGER.info("\n\n  The phase of checking the parameters of the user request (beginning)");
// processes changes in parameters of the user request
        checkDiff(newModel, baseModel, modelDiff);
        LOGGER.info("\n\n  Phase of properties checking (ending)");
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

            xmlQuery.getImplementations().forEach(prevSql -> {
                Optional<XmlQuerySql> optionalnewSql = newXmlQuery.getImplementations().stream()
                    .filter(param -> Objects.equals(param.getDbmsString(), prevSql.getDbmsString())).findAny();
                // If SQL is deleted, then we skip
                if (!optionalnewSql.isPresent()) {
                    return;
                }

                XmlQuerySql newSql = optionalnewSql.get();

                AtomicBoolean paramUpdated = new AtomicBoolean(false);

                checkDbmsString(prevSql, newSql, paramUpdated);
                checkValue(prevSql, newSql, paramUpdated);

                if (paramUpdated.get()) {
                    modelDiff.addDiffObject(ElementState.UPDATED, prevSql);
                }
            });

        });
    }

    private void checkDbmsString(XmlQuerySql prevSql,
                            XmlQuerySql newSql,
                            AtomicBoolean paramUpdated) {
        if (!Objects.equals(newSql.getDbmsString(), prevSql.getDbmsString())) {
            prevSql.addChangedProperty(XmlQuerySql.DBMS_TAG, prevSql.getDbmsString());
            prevSql.setDbmsString(newSql.getDbmsString());
            paramUpdated.set(true);
        }
    }

    private void checkValue(XmlQuerySql prevSql,
                                   XmlQuerySql newSql,
                                   AtomicBoolean paramUpdated) {
        if (!Objects.equals(newSql.getValue(), prevSql.getValue())) {
            prevSql.addChangedProperty("value", prevSql.getValue());
            prevSql.setValue(newSql.getValue());
            paramUpdated.set(true);
        }
    }
}
