package com.sbt.liquibase.diff.handler.other;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;

import java.util.Objects;
import java.util.Optional;

import static com.sbt.mg.Helper.getTemplate;

public class ForeignKeyHandlerNew {
    private static final String ADD_FOREIGNKEY_CONSTRAINT_TEMPLATE = getTemplate("/templates/changelog/addForeignKeyConstraint.changelog.template");
    private static final String ADD_FOREIGNKEY_CONSTRAINT_WITHOUT_ORACLE_TEMPLATE = getTemplate("/templates/changelog/withoutOracle/addForeignKeyConstraint.changelog.withoutOracle.template");

    public void handle(StringBuilder changesSB, MutableLong index, XmlModelClassProperty xmlModelClassProperty,
                       ModelParameters modelParameters, PluginParameters pluginParameters, MutableInt indexFk) {

        XmlModelClass modelClass = xmlModelClassProperty.getModelClass();

        String template = pluginParameters.isDisableGenerateOracleLiquibase() ? ADD_FOREIGNKEY_CONSTRAINT_WITHOUT_ORACLE_TEMPLATE : ADD_FOREIGNKEY_CONSTRAINT_TEMPLATE;

        XmlModelClass referenceModelClass = modelClass.getModel().getClass(xmlModelClassProperty.getType());
        Optional<XmlModelClassProperty> optionalReferenceIdProperty = referenceModelClass.getPropertiesAsList().stream().filter(XmlModelClassProperty::isId).findFirst();
        if (optionalReferenceIdProperty.isPresent() && Objects.nonNull(referenceModelClass.getTableName())) {
            changesSB.append(template
                    .replace("${modelName}", modelParameters.getModel().getModelName())
                    .replace("${version}", modelParameters.getVersion())
                    .replace("${index}", index.toString())
                    .replace("${indexFk}", String.valueOf(indexFk.incrementAndGet()))
                    .replace("${constraintName}", xmlModelClassProperty.getFkName())
                    .replace("${tableName}", xmlModelClassProperty.getModelClass().getTableName())
                    .replace("${columnName}", xmlModelClassProperty.getColumnName())
                    .replace("${deleteCascade}", xmlModelClassProperty.isFkDeleteCascade() ? "\n\t\t\tonDelete=\"CASCADE\"" :"")
                    .replace("${referenceTableName}", referenceModelClass.getTableName())
                    .replace("${referenceColumnName}", optionalReferenceIdProperty.get().getColumnName())
            );
        }
    }
}
