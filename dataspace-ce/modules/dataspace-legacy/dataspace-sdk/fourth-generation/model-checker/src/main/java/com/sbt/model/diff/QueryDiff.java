package com.sbt.model.diff;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.ParameterContext;
import com.sbt.mg.ElementState;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.model.diff.query.QueryParamDiff;
import com.sbt.model.diff.query.QueryPropertyDiff;
import com.sbt.model.diff.query.QuerySqlDiff;

import java.util.logging.Logger;

public class QueryDiff implements DiffHandler {
    private static final Logger LOGGER = Logger.getLogger(QueryDiff.class.getName());

    private QueryParamDiff queryParamDiff = new QueryParamDiff();
    private QueryPropertyDiff queryPropertyDiff = new QueryPropertyDiff();
    private QuerySqlDiff querySqlDiff = new QuerySqlDiff();

    @Override
    public void handler(XmlModel newModel, XmlModel baseModel, ParameterContext parameterContext) {
        LOGGER.info("\n\n  The verification request phase (beginning)");
        swapNewCreatedQueries(newModel, baseModel, parameterContext.getModelParameters());
        queryParamDiff.handler(newModel, baseModel, parameterContext);
        queryPropertyDiff.handler(newModel, baseModel, parameterContext);
        querySqlDiff.handler(newModel, baseModel, parameterContext);
        LOGGER.info("\n\n  The verification request phase (end)");
    }

    private void swapNewCreatedQueries(XmlModel newModel, XmlModel baseModel, ModelParameters modelDiff) {
        newModel.getQueriesAsList().forEach(newQuery -> {
            // If the request is new
            if (!baseModel.containsQuery(newQuery.getName())) {
                modelDiff.addDiffObject(ElementState.NEW, newQuery);
                newQuery.getPropertiesAsList().forEach(xmlModelClassProperty ->
                        modelDiff.addDiffObject(ElementState.NEW, xmlModelClassProperty));
                newQuery.getImplementations().forEach(impl ->
                        modelDiff.addDiffObject(ElementState.NEW, impl));
                newQuery.getParams().forEach(param ->
                        modelDiff.addDiffObject(ElementState.NEW, param));
                baseModel.addQueryWithoutCheck(newQuery.copy());
            }
        });
    }
}
