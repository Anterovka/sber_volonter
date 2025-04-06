package com.sbt.liquibase.diff.handler.hclass;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.mg.data.model.XmlModelClass;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;

import static com.sbt.liquibase.helper.Helper.createRemarks;
import static com.sbt.mg.Helper.getTemplate;

public class ClassHandlerDeprecated extends ClassHandlerBase{

    private static final String TABLE_REMARKS_WITHOUT_ROLLBACK_TEMPLATE = getTemplate("/templates/changelog/tableRemarks.withoutRollback.changelog.template");

    public ClassHandlerDeprecated(MutableInt indexCollection, MutableInt indexIndex) {
        super(indexCollection, indexIndex);
    }

    @Override
    public void handle(StringBuilder changesSB, MutableLong index, XmlModelClass modelClass, ModelParameters modelDiff, PluginParameters pluginParameters) {
        if (modelClass.isEmbeddable() || modelClass.isAbstract()) {
            return;
        }
        changesSB.append(TABLE_REMARKS_WITHOUT_ROLLBACK_TEMPLATE
                .replace("${modelName}", modelDiff.getModel().getModelName())
                .replace("${version}", modelDiff.getVersion())
                .replace("${index}", String.valueOf(index.incrementAndGet()))
                .replace("${remarks2}", createRemarks(modelClass, modelClass.isDeprecated()))
                .replace("${remarks_back_2}", createRemarks(modelClass, !modelClass.isDeprecated()))
                .replace("${tableName}", modelClass.getTableName()));
    }
}
