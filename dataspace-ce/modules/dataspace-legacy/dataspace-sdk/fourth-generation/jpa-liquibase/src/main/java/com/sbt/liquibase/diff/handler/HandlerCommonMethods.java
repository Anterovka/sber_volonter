package com.sbt.liquibase.diff.handler;

import com.sbt.computed.expression.parser.CheckExpression;
import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.liquibase.exception.PrimaryKeyPropertyNotFoundException;
import com.sbt.mg.data.model.ClassStrategy;
import com.sbt.mg.data.model.CollectionType;
import com.sbt.mg.data.model.PropertyType;
import com.sbt.mg.data.model.XmlEmbeddedProperty;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.utils.LiquibasePropertyUtils;
import com.sbt.model.utils.Models;
import com.sbt.parameters.enums.DBMS;
import com.sbt.parameters.enums.ObjectLinks;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;

import java.util.stream.Collectors;

import static com.sbt.liquibase.helper.Helper.createRemarks;
import static com.sbt.mg.ModelHelper.getPropertyDbType;
import static com.sbt.mg.Helper.getTemplate;
import static com.sbt.mg.ModelHelper.isBinaryType;
import static com.sbt.mg.ModelHelper.isBooleanType;

/** Generating liquibase script */
public class HandlerCommonMethods {
    private MutableInt indexIndex;
    // ordinal index of the collection within the processed class
    protected MutableInt indexCollection;
    private MutableLong classIndex;

    /** Creating a table in the rollback section when deleting */
    private static final String CREATE_TABLE_ROLLBACK_TEMPLATE = getTemplate("/templates/changelog/createTable.rollback.changelog.template");

    private static final String ADD_COLUMN_CHANGELOG_TEMPLATE = getTemplate("/templates/changelog/addColumn.changelog.template");
    private static final String ADD_LOB_CHANGELOG_TEMPLATE = getTemplate("/templates/changelog/addLOBField.changelog.template");
    private static final String DROP_TABLE_TEMPLATE = getTemplate("/templates/changelog/dropTable.changelog.template");
    private static final String DROP_COLUMN_TEMPLATE = getTemplate("/templates/changelog/dropColumn.changelog.template");
    private static final String DROP_COMPUTED_COLUMN_TEMPLATE = getTemplate("/templates/changelog/dropComputedColumn.changelog.template");
    private static final String COLUMN_CHANGELOG_TEMPLATE = getTemplate("/templates/changelog/column.changelog.template");
    private static final String ADD_ORACLE_COLUMN_BINARY_DEF_VALUE_CHANGELOG_TEMPLATE = getTemplate("/templates/changelog/addOracleColumnWithBinaryDefaultValue.changelog.template");

    private static final String MOVE_LOB_CHANGE_CHANGELOG_TEMPLATE = getTemplate("/templates/changelog/moveLobSql.change.changelog.template");

    private static final String PRIMITIVES_SET_CHANGE_TEMPLATE = getTemplate("/templates/changelog/primitivesSet.change.changelog.template");
    private static final String PRIMITIVES_LIST_CHANGE_TEMPLATE = getTemplate("/templates/changelog/primitivesList.change.changelog.template");
    private static final String REFERENCES_SET_CHANGE_TEMPLATE = getTemplate("/templates/changelog/referencesSet.change.changelog.template");

    // templates without scripts oracle
    private static final String DROP_COMPUTED_COLUMN_WITHOUT_ORACLE_TEMPLATE = getTemplate("/templates/changelog/withoutOracle/dropComputedColumn.changelog.withoutOracle.template");
    //


    public static String prepareDbmsReplacer(DBMS dbms) {
        if (DBMS.ANY == dbms) {
            return "";
        }
        return "dbms=\"" + dbms.getLiquibaseValue() + "\"";
    }

    public HandlerCommonMethods() {
        this.indexIndex = new MutableInt(0);
        this.indexCollection = new MutableInt(0);
        ;
        this.classIndex = new MutableLong(0);
        ;
    }

    public HandlerCommonMethods(MutableInt indexIndex) {
        this.indexIndex = indexIndex;
        this.indexCollection = new MutableInt(0);
        this.classIndex = new MutableLong(0);
    }

    public void addSimpleColumnWithoutRollback(
            StringBuilder columnsSB,
            MutableLong index,
            String columnName,
            XmlModelClassProperty property,
            XmlModelClass controlClass,
            ModelParameters modelDiff,
            String preconditions,
            String onFailAction,
            PluginParameters pluginParameters) {
        addSimpleColumn(columnsSB, index, columnName, property, controlClass, modelDiff, preconditions, onFailAction, pluginParameters, false);
    }

    public void addSimpleColumn(
            StringBuilder columnsSB,
            MutableLong index,
            String columnName,
            XmlModelClassProperty property,
            XmlModelClass controlClass,
            ModelParameters modelDiff,
            String preconditions,
            String onFailAction,
            PluginParameters pluginParameters) {
        addSimpleColumn(columnsSB, index, columnName, property, controlClass, modelDiff, preconditions, onFailAction, pluginParameters, true);
    }

    public void addSimpleColumn(
            StringBuilder columnsSB,
            MutableLong index,
            String columnName,
            XmlModelClassProperty property,
            XmlModelClass controlClass,
            ModelParameters modelDiff,
            String preconditions,
            String onFailAction,
            PluginParameters pluginParameters,
            boolean needRollback) {

        boolean isMandatory = property.isMandatory();

        if (property.isMandatory()
                && controlClass.getStrategy() == ClassStrategy.SINGLE_TABLE
                && !controlClass.isBaseClassMark()) {
            isMandatory = false;
        }

        index.increment();
        if (property.getComputedExpression() == null) {
            if (isBinaryType(property) && StringUtils.isNotBlank(property.getDefaultValue())) {
                if (!pluginParameters.isDisableGenerateOracleLiquibase()) {
                    addColumnTo(columnsSB, index, columnName, property, controlClass, modelDiff, isMandatory, DBMS.ORACLE, preconditions, onFailAction, pluginParameters, needRollback);
                    index.increment();
                }
                addColumnTo(columnsSB, index, columnName, property, controlClass, modelDiff, isMandatory, DBMS.POSTGRES, preconditions, onFailAction, pluginParameters, needRollback);
                index.increment();
                addColumnTo(columnsSB, index, columnName, property, controlClass, modelDiff, isMandatory, DBMS.H2, preconditions, onFailAction, pluginParameters, needRollback);
            } else {
                addColumnTo(columnsSB, index, columnName, property, controlClass, modelDiff, isMandatory, DBMS.ANY, preconditions, onFailAction, pluginParameters, needRollback);
            }
        } else {
            columnsSB.append(CheckExpression.ComputedFieldsGenerator.addChangeSets(index, controlClass.getTableName(),property, pluginParameters));
        }

        if (property.getTypeInfo().getHbmName().endsWith("lob") && !pluginParameters.isDisableGenerateOracleLiquibase()) {
            columnsSB.append(ADD_LOB_CHANGELOG_TEMPLATE
                    .replace("${columnName}", columnName)
                    .replace("${version}", modelDiff.getVersion())
                    .replace("${index}", String.valueOf(index.incrementAndGet()))
                    .replace("${indexIndex}", String.valueOf(indexIndex.incrementAndGet()))
                    .replace("${modelName}", modelDiff.getModel().getModelName())
                    .replace("${tableName}", controlClass.getTableName()));
        } else {
            columnsSB.append("    ");
        }
    }

    private void addColumnTo(StringBuilder columnsSB,
                             MutableLong index,
                             String columnName,
                             XmlModelClassProperty property,
                             XmlModelClass controlClass,
                             ModelParameters modelDiff,
                             boolean isMandatory,
                             DBMS dbms,
                             String preconditions,
                             String onFailAction,
                             PluginParameters pluginParameters,
                             boolean needRollback) {

        String template;
        String defaultValue;
        if (isBinaryType(property) && StringUtils.isNotBlank(property.getDefaultValue())) {
            if (dbms == DBMS.ORACLE) {
                template = ADD_ORACLE_COLUMN_BINARY_DEF_VALUE_CHANGELOG_TEMPLATE;
                defaultValue = LiquibasePropertyUtils.oracleBase64DecodeFunction(property.getDefaultValue());
            } else if (dbms == DBMS.POSTGRES){
                template = ADD_COLUMN_CHANGELOG_TEMPLATE;
                defaultValue = LiquibasePropertyUtils.computeDefaultValue(property, DBMS.POSTGRES);
            } else if (dbms == DBMS.H2) {
                template = ADD_COLUMN_CHANGELOG_TEMPLATE;
                defaultValue = LiquibasePropertyUtils.computeDefaultValue(property, DBMS.H2);
            } else {
                template = ADD_COLUMN_CHANGELOG_TEMPLATE;
                defaultValue = LiquibasePropertyUtils.computeDefaultValue(property, dbms);
            }
        } else {
            template = ADD_COLUMN_CHANGELOG_TEMPLATE;
            defaultValue = LiquibasePropertyUtils.computeDefaultValue(property, dbms);
        }

        columnsSB.append(template
                .replace("${columnName}", columnName)
                .replace("${dbms}", prepareDbmsReplacer(dbms))
                .replace("${type}", getPropertyDbType(property))
                .replace("${name}", property.getName())
                .replace("${label}", property.getLabel())
                .replace("${version}", modelDiff.getVersion())
                .replace("${remarks1}", createRemarks(property, property.isDeprecated()))
                .replace("${index}", String.valueOf(index.incrementAndGet()))
                .replace("${nullable}", String.valueOf(!isMandatory))
                .replace("${modelName}", modelDiff.getModel().getModelName())
                .replace("${tableName}", controlClass.getTableName())
                .replace("${additional}", isMandatory ? "<constraints nullable=\"false\"/>" : " ")
                .replace("${defaultValue}", defaultValue)
                .replace("${onFailAction}", onFailAction)
                .replace("${addPreconditions}", preconditions)
                .replace("${rollback}", pluginParameters.isOptimizeChangelog() || !needRollback ? "\n\t\t<rollback/>" : "")
                .replace("${rollbackOraWithBinDefVal}", pluginParameters.isOptimizeChangelog() || !needRollback ?
                        "\n\t\t<rollback/>" :
                        String.format("\n\t\t<rollback>\n" +
                                "            <dropColumn tableName=\"%s\" columnName=\"%s\"/>\n" +
                                "        </rollback>", controlClass.getTableName(), columnName)))
                .append('\n');
    }


    public static boolean isClassHaveBooleanDefaultValueProperties(XmlModelClass modelClass) {
        return modelClass.getPropertiesWithIncome().stream()
                .anyMatch(it -> isBooleanType(it.getType()) && StringUtils.isNotBlank(it.getDefaultValue()));
    }

    public void dropTable(StringBuilder changesSB,
                          MutableLong index,
                          String tableName,
                          String migrateSql,
                          ModelParameters modelDiff) {

            changesSB.append(DROP_TABLE_TEMPLATE
                    .replace("${modelName}", modelDiff.getModel().getModelName())
                    .replace("${version}", modelDiff.getVersion())
                    .replace("${index}", String.valueOf(index.incrementAndGet()))
                    .replace("${tableName}", tableName)
                    .replace("${migrateSql}", migrateSql)
            )
                    .append('\n');

    }

    public void dropCollectionTable(StringBuilder changesSB,
                                    MutableLong index,
                                    XmlModelClass modelClass,
                                    XmlModelClassProperty modelClassProperty,
                                    String tableName,
                                    String migrateSql,
                                    ModelParameters modelDiff) {

        StringBuilder columnsSB = new StringBuilder();
        StringBuilder collectionColumnsSB = new StringBuilder();
        StringBuilder moveLobSB = new StringBuilder();

        generateChangelogClassColumnsAndIndexes(indexCollection, columnsSB, modelClassProperty, modelDiff, DBMS.ANY, collectionColumnsSB, tableName, moveLobSB);

        StringBuilder createTable = new StringBuilder();

        createTable.append(getCreateTableForRollback(modelClass, tableName, columnsSB, ""));

        changesSB.append(DROP_TABLE_TEMPLATE
                .replace("${modelName}", modelDiff.getModel().getModelName())
                .replace("${version}", modelDiff.getVersion())
                .replace("${index}", String.valueOf(index.incrementAndGet()))
                .replace("${tableName}", tableName)
                .replace("${migrateSql}", migrateSql)
        )
                .append('\n');
    }

    private String getCreateTableForRollback(XmlModelClass modelClass, String tableName, StringBuilder columnsSB, String moveLob) {
        return CREATE_TABLE_ROLLBACK_TEMPLATE
                .replace("${columns}", columnsSB.toString())
                .replace("${tableName}", tableName)
                .replace("${remarks2}", createRemarks(modelClass, modelClass.isDeprecated()))
                .replace("${pkIndexName}", getPkIndexName(modelClass, tableName))
                .replace("${pk_keys_column}", collectPrimaryKeysColumnForRollbackDropTable(modelClass))
                .replace("${moveLobSql}", moveLob);
    }

    private String getPkIndexName(XmlModelClass modelClass, String tableName) {
        if (modelClass.propertyChanged(XmlModelClass.PK_INDEX_NAME_TAG)) {
            return modelClass.getOldValueChangedProperty(XmlModelClass.PK_INDEX_NAME_TAG);
        } else {
            if (StringUtils.isBlank(modelClass.getPkIndexName())) {
                throw new PrimaryKeyPropertyNotFoundException(modelClass.getName());
            }
            return modelClass.getPkIndexName();
        }
    }

    public void dropColumn(StringBuilder changesSB,
                           MutableLong index,
                           ModelParameters modelDiff,
                           String tableName,
                           String columnName,
                           String addPreconditions,
                           String migrateSql) {

        changesSB.append(DROP_COLUMN_TEMPLATE
                .replace("${modelName}", modelDiff.getModel().getModelName())
                .replace("${version}", modelDiff.getVersion())
                .replace("${index}", String.valueOf(index.incrementAndGet()))
                .replace("${columnName}", columnName)
                .replace("${tableName}", tableName)
                .replace("${addPreconditions}", addPreconditions)
                .replace("${migrateSql}", migrateSql)
        );

    }

    /**
     * Generate columns and indexes for changelog.xml
     *
     * @param collectionIndex The index of the collection
     * @param columnsSB       StringBuilder колонок
     * @param classProperty   Class property
     */
    private void generateChangelogClassColumnsAndIndexes(MutableInt collectionIndex,
                                                         StringBuilder columnsSB,
                                                         XmlModelClassProperty classProperty,
                                                         ModelParameters modelDiff,
                                                         DBMS dbms,
                                                         StringBuilder collectionColumnsSB,
                                                         String tableName,
                                                         StringBuilder moveLobSB) {
        if (StringUtils.isNotEmpty(classProperty.getComputedExpression())) {
            return;
        }
        if (classProperty.getCollectionType() == null) {
            if (classProperty.isEmbedded()) {
                XmlModelClass.getEmbeddedList(classProperty).getEmbeddedPropertyList()
                        .forEach(xmlEmbeddedProperty -> {

                            columnsSB.append(addPrimitiveColumnChangelog(
                                    xmlEmbeddedProperty.getColumnName(),
                                    xmlEmbeddedProperty.getProperty(),
                                    classProperty.isId(),
                                    classProperty,
                                    dbms));

                            if (xmlEmbeddedProperty.getProperty().isLobProperty()) {
                                moveLobSB.append("\n")
                                        .append(moveLobSql(tableName, xmlEmbeddedProperty.getColumnName()));
                            }
                        });
            } else {
                if (classProperty.getObjectLinks() == ObjectLinks.O2O && classProperty.getMappedBy() != null) {
                    return;
                }

                //determine the value of mandatory (if there is a previous one, then it, otherwise the current one), for insertion into rollback
                boolean mandatory = classProperty.propertyChanged(XmlModelClassProperty.MANDATORY_TAG) ?
                        Boolean.TRUE.equals(classProperty.getOldValueChangedProperty(XmlModelClassProperty.MANDATORY_TAG)) :
                        classProperty.isMandatory();

                columnsSB.append(addPrimitiveColumnChangelog(
                        classProperty.getColumnName(),
                        classProperty,
                        mandatory,
                        classProperty,
                        dbms));

                if (classProperty.isLobProperty()) {
                    moveLobSB.append("\n")
                            .append(moveLobSql(tableName, classProperty.getColumnName()));
                }
            }
        } else {
            addCollectionTable(collectionColumnsSB, classProperty, collectionIndex, this.indexIndex);
        }
    }

    private String moveLobSql(String tableName, String columnName) {
        return MOVE_LOB_CHANGE_CHANGELOG_TEMPLATE
                .replace("${tableName}", tableName)
                .replace("${columnName}", columnName);
    }

    private String addPrimitiveColumnChangelog(String columnName,
                                               XmlModelClassProperty property,
                                               boolean mandatory,
                                               XmlModelClassProperty propertyForRemarks,
                                               DBMS dbms) {
        return COLUMN_CHANGELOG_TEMPLATE
                .replace("${columnName}", columnName)
                .replace("${type}", getPropertyDbType(property))
                .replace("${remarks1}", com.sbt.liquibase.helper.Helper.createRemarks(propertyForRemarks, propertyForRemarks.isDeprecated()))
                .replace("${computedExpression}", CheckExpression.computedExpressionAttribute(property))
                .replace("${additional}", makeAdditionalSection(mandatory));
    }

    private String makeAdditionalSection(boolean mandatory) {

        return mandatory ? "<constraints nullable=\"false\"/>" : "";
    }

    public static void addCollectionTable(StringBuilder changesSB,
                                          XmlModelClassProperty modelClassProperty,
                                          MutableInt collectionIndex,
                                          MutableInt indexIndex) {
        if (modelClassProperty.getCollectionType() != null && modelClassProperty.getMappedBy() != null) {
            return;
        }
        String tableName = modelClassProperty.getCollectionTableName();
        if (StringUtils.isBlank(modelClassProperty.getCollectionPkIndexName())) {
            throw new PrimaryKeyPropertyNotFoundException(modelClassProperty.getCollectionTableName());
        }
        String pkIndexName = modelClassProperty.getCollectionPkIndexName();

        // установка физического типа свойства
        Models.fillCategoryAndTypeInfo(modelClassProperty);

        collectionIndex.increment();
        indexIndex.increment();
        if (modelClassProperty.getCollectionType() == CollectionType.SET) {
            if (modelClassProperty.getCategory() == PropertyType.REFERENCE) {
                changesSB.append(REFERENCES_SET_CHANGE_TEMPLATE
                        .replace("${tableName}", tableName)
                        .replace("${name}", modelClassProperty.getModelClass().getName())
                        .replace("${propertyType}", modelClassProperty.getTypeInfo().getJavaName())
                        .replace("${propertyName}", modelClassProperty.getName())
                        .replace("${keyColumnName}", modelClassProperty.getKeyColumnName())
                        .replace("${columnName}", modelClassProperty.getColumnName())
                        .replace("${type}", getPropertyDbType(modelClassProperty))
                        .replace("${pkIndexName}", pkIndexName));
            } else {
                changesSB.append(PRIMITIVES_SET_CHANGE_TEMPLATE
                        .replace("${tableName}", tableName)
                        .replace("${name}", modelClassProperty.getModelClass().getName())
                        .replace("${propertyType}", modelClassProperty.getTypeInfo().getJavaName())
                        .replace("${propertyName}", modelClassProperty.getName())
                        .replace("${keyColumnName}", modelClassProperty.getKeyColumnName())
                        .replace("${columnName}", modelClassProperty.getColumnName())
                        .replace("${type}", getPropertyDbType(modelClassProperty))
                        .replace("${pkIndexName}", pkIndexName));
            }
        } else {
            changesSB.append(PRIMITIVES_LIST_CHANGE_TEMPLATE
                    .replace("${tableName}", tableName)
                    .replace("${name}", modelClassProperty.getModelClass().getName())
                    .replace("${propertyType}", modelClassProperty.getTypeInfo().getJavaName())
                    .replace("${propertyName}", modelClassProperty.getName())
                    .replace("${keyColumnName}", modelClassProperty.getKeyColumnName())
                    .replace("${columnName}", modelClassProperty.getColumnName())
                    .replace("${type}", getPropertyDbType(modelClassProperty))
                    .replace("${orderColumnName}", modelClassProperty.getOrderColumnName())
                    .replace("${pkIndexName}", pkIndexName));
        }
    }

    public static String collectPrimaryKeysColumnForRollbackDropTable(XmlModelClass modelClass) {
        XmlModelClassProperty primaryKeyProperty = modelClass.getPropertiesWithAllIncome().stream()
                .filter(XmlModelClassProperty::isId).findAny()
                .orElseThrow(() -> new PrimaryKeyPropertyNotFoundException(modelClass.getName()));

        if (modelClass.getModel().containsClass(primaryKeyProperty.getType())) {
            return XmlModelClass.getEmbeddedList(primaryKeyProperty).getEmbeddedPropertyList().stream()
                    .map(XmlEmbeddedProperty::getColumnName)
                    .collect(Collectors.joining(", "));
        }

        return primaryKeyProperty.getColumnName();
    }



}
