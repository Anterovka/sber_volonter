package com.sbt.liquibase.diff.handler.other;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.mg.data.model.XmlModelClassProperty;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;

import java.util.Objects;

import static com.sbt.mg.Helper.getTemplate;

public class ForeignKeyHandlerRemoved {
    private static final String DROP_FOREIGNKEY_CONSTRAINT_TEMPLATE = getTemplate("/templates/changelog/dropForeignKeyConstraint.changelog.template");
    private static final String DROP_FOREIGNKEY_CONSTRAINT_WITHOUT_ORACLE_TEMPLATE = getTemplate("/templates/changelog/withoutOracle/dropForeignKeyConstraint.changelog.withoutOracle.template");

    public void handle(StringBuilder changesSB, MutableLong index, XmlModelClassProperty xmlModelClassProperty,
                       ModelParameters modelParameters, PluginParameters pluginParameters, MutableInt indexFk) {

        String template = pluginParameters.isDisableGenerateOracleLiquibase() ? DROP_FOREIGNKEY_CONSTRAINT_WITHOUT_ORACLE_TEMPLATE : DROP_FOREIGNKEY_CONSTRAINT_TEMPLATE;

        String oldFkName = Objects.nonNull(xmlModelClassProperty.getOldValueChangedProperty(XmlModelClassProperty.FK_NAME_TAG)) ?
                xmlModelClassProperty.getOldValueChangedProperty(XmlModelClassProperty.FK_NAME_TAG) :
                xmlModelClassProperty.getFkName();

        if (Objects.nonNull(oldFkName)) {
            changesSB.append(template
                    .replace("${modelName}", modelParameters.getModel().getModelName())
                    .replace("${version}", modelParameters.getVersion())
                    .replace("${index}", index.toString())
                    .replace("${indexFk}", String.valueOf(indexFk.incrementAndGet()))
                    .replace("${constraintName}", oldFkName)
                    .replace("${tableName}", xmlModelClassProperty.getModelClass().getTableName())
            );
        }
    }
}
