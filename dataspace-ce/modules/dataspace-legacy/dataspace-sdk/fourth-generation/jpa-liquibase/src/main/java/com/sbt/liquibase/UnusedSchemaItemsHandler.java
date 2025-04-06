package com.sbt.liquibase;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.unusedschemaItems.XmlUnusedColumn;
import com.sbt.mg.data.model.unusedschemaItems.XmlUnusedSchemaItems;
import com.sbt.mg.data.model.unusedschemaItems.XmlUnusedTable;
import org.apache.commons.lang3.mutable.MutableLong;

import java.util.List;
import java.util.Objects;

import static com.sbt.dataspace.pdm.ModelGenerateUtils.getModelVersion;
import static com.sbt.mg.Helper.getTemplate;
import static com.sbt.mg.Helper.isSnapshotVersion;
import static com.sbt.model.checker.CheckerUtils.isCorrectVersionToDelete;

public class UnusedSchemaItemsHandler {

    private static final String DROP_TABLE_WITHOUT_ROLLBACK_TEMPLATE = getTemplate("/templates/changelog/dropTable.withoutRollback.changelog.template");
    private static final String DROP_COLUMN_WITHOUT_ROLLBACK_TEMPLATE = getTemplate("/templates/changelog/dropColumn.withoutRollback.changelog.template");

    private UnusedSchemaItemsHandler() {
    }

    public static void handle(StringBuilder changesSB, MutableLong index, ModelParameters modelParameters, PluginParameters pluginParameters) {

        boolean dropDeletedItemsImmediately = pluginParameters.isDropDeletedItemsImmediately();

        XmlModel model = modelParameters.getModel();
        XmlUnusedSchemaItems unusedSchemaItems = model.getUnusedSchemaItems();

        String modelVersion = getModelVersion(modelParameters, model);
        if (isSnapshotVersion(modelVersion)) {
            return;
        }

        if (Objects.nonNull(unusedSchemaItems)) {
            List<XmlUnusedColumn> unusedColumns = unusedSchemaItems.getUnusedColumns();
            List<XmlUnusedTable> unusedTables = unusedSchemaItems.getUnusedTables();

            unusedColumns.stream()
                    .filter(unusedColumn -> isCorrectVersionToDelete(modelVersion, unusedColumn.getDeletedInVersion(), dropDeletedItemsImmediately))
                    .forEach(xmlUnusedColumn -> dropColumn(changesSB, modelParameters, index, xmlUnusedColumn, modelVersion));

            unusedTables.stream()
                    .filter(unusedTable -> isCorrectVersionToDelete(modelVersion, unusedTable.getDeletedInVersion(), dropDeletedItemsImmediately))
                    .forEach(xmlUnusedTable -> dropTable(changesSB, modelParameters, index, xmlUnusedTable, modelVersion));
        }

    }

    private static void dropColumn(StringBuilder changesSB,
                                   ModelParameters modelParameters,
                                   MutableLong index,
                                   XmlUnusedColumn unusedColumn,
                                   String modelVersion) {
        XmlModel model = modelParameters.getModel();
        changesSB.append(
                DROP_COLUMN_WITHOUT_ROLLBACK_TEMPLATE
                        .replace("${modelName}", model.getModelName())
                        .replace("${version}", modelVersion)
                        .replace("${index}", String.valueOf(index.getAndIncrement()))
                        .replace("${tableName}", unusedColumn.getTableName())
                        .replace("${columnName}", unusedColumn.getName()));
    }

    private static void dropTable(StringBuilder changesSB,
                                  ModelParameters modelParameters,
                                  MutableLong index,
                                  XmlUnusedTable unusedTable,
                                  String modelVersion) {
        XmlModel model = modelParameters.getModel();
        changesSB.append(
                DROP_TABLE_WITHOUT_ROLLBACK_TEMPLATE
                        .replace("${modelName}", model.getModelName())
                        .replace("${version}", modelVersion)
                        .replace("${index}", String.valueOf(index.getAndIncrement()))
                        .replace("${tableName}", unusedTable.getName()));
    }

}
