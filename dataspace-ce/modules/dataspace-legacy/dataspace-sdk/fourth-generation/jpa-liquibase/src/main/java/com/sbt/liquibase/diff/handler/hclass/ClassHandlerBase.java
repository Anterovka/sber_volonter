package com.sbt.liquibase.diff.handler.hclass;

import com.sbt.computed.expression.parser.CheckExpression;
import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.liquibase.diff.handler.ClassHandler;
import com.sbt.liquibase.diff.handler.PropertyHandler;
import com.sbt.liquibase.diff.handler.hproperty.PropertyHandlerDeprecated;
import com.sbt.liquibase.diff.handler.hproperty.PropertyHandlerNew;
import com.sbt.liquibase.diff.handler.hproperty.PropertyHandlerRemoved;
import com.sbt.liquibase.diff.handler.hproperty.PropertyHandlerUpdated;
import com.sbt.liquibase.exception.ColumnNotDefinedException;
import com.sbt.liquibase.exception.PrimaryKeyPropertyNotFoundException;
import com.sbt.mg.ElementState;
import com.sbt.mg.NameHelper;
import com.sbt.mg.data.model.CollectionType;
import com.sbt.mg.data.model.Property;
import com.sbt.mg.data.model.PropertyType;
import com.sbt.mg.data.model.XmlEmbeddedList;
import com.sbt.mg.data.model.XmlEmbeddedProperty;
import com.sbt.mg.data.model.XmlIndex;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.NoFoundEmbeddedPropertyException;
import com.sbt.model.exception.UnsupportedCollectionException;
import com.sbt.parameters.enums.ObjectLinks;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sbt.liquibase.helper.Helper.createRemarks;
import static com.sbt.mg.Helper.getTemplate;
import static com.sbt.mg.ModelHelper.getPropertyDbType;
import static com.sbt.mg.ModelHelper.isBinaryType;
import static com.sbt.mg.ModelHelper.isBooleanType;

public abstract class ClassHandlerBase implements ClassHandler {
    private static final String DROP_INDEX_CHANGELOG_TEMPLATE = getTemplate("/templates/changelog/dropIndex.changelog.template");
    private static final String DROP_INDEX_ROLLBACK_CHANGELOG_TEMPLATE = getTemplate("/templates/changelog/dropIndex.rollback.changelog.template");
    private static final String INDEX_CHANGELOG_TEMPLATE = getTemplate("/templates/changelog/index.changelog.template");

    private static final String CLASS_CHANGELOG_TEMPLATE = getTemplate("/templates/changelog/class.changelog.template");
    private static final String CREATE_CLASS_TEMPLATE = getTemplate("/templates/changelog/createTable.changelog.template");
    private static final String COLUMN_CHANGELOG_TEMPLATE = getTemplate("/templates/changelog/column.changelog.template");
    private static final String PRIMITIVES_SET_CHANGELOG_TEMPLATE = getTemplate("/templates/changelog/primitivesSet.changelog.template");
    private static final String PRIMITIVES_LIST_CHANGELOG_TEMPLATE = getTemplate("/templates/changelog/primitivesList.changelog.template");
    private static final String REFERENCES_SET_CHANGELOG_TEMPLATE = getTemplate("/templates/changelog/referencesSet.changelog.template");
    private static final String ADD_LOB_CHANGELOG_TEMPLATE = getTemplate("/templates/changelog/addLOBField.changelog.template");

    // templates without scripts oracle
    private static final String INDEX_CHANGELOG_WITHOUT_ORACLE_TEMPLATE = getTemplate("/templates/changelog/withoutOracle/index.changelog.withoutOracle.template");
    private static final String CLASS_CHANGELOG_WITHOUT_ORACLE_TEMPLATE = getTemplate("/templates/changelog/withoutOracle/class.changelog.withoutOracle.template");
    private static final String ADD_PK_TEMPLATE = getTemplate("/templates/changelog/addPK.template");
    private static final String ADD_PK_WITHOUT_ORACLE_TEMPLATE = getTemplate("/templates/changelog/withoutOracle/addPK.template");
    private static final String PRIMITIVES_LIST_CHANGELOG_WITHOUT_ORACLE_TEMPLATE = getTemplate("/templates/changelog/withoutOracle/primitivesList.changelog.withoutOracle.template");
    private static final String PRIMITIVES_SET_CHANGELOG_WITHOUT_ORACLE_TEMPLATE = getTemplate("/templates/changelog/withoutOracle/primitivesSet.changelog.withoutOracle.template");
    private static final String REFERENCES_SET_CHANGELOG_WITHOUT_ORACLE_TEMPLATE = getTemplate("/templates/changelog/withoutOracle/referencesSet.changelog.withoutOracle.template");


    //

    protected EnumMap<ElementState, PropertyHandler> propertyHandlerMap = new EnumMap<>(ElementState.class);
    // ordinal index of the collection within the processed class
    protected MutableInt indexCollection;
    // The ordinal number of the index within the processed class
    protected final MutableInt indexIndex;

    private final StringBuilder indexesSB = new StringBuilder();
    private final StringBuilder collectionColumnsSB = new StringBuilder();

    private MutableLong classIndex;


    public ClassHandlerBase(MutableLong classIndex, MutableInt indexCollection, MutableInt indexIndex) {
        this(indexCollection, indexIndex);
        this.classIndex = classIndex;
    }

    public ClassHandlerBase(MutableInt indexCollection, MutableInt indexIndex) {
        this.indexCollection = indexCollection;
        this.indexIndex = indexIndex;

        propertyHandlerMap.put(ElementState.NEW, new PropertyHandlerNew(indexCollection, indexIndex));
        propertyHandlerMap.put(ElementState.UPDATED, new PropertyHandlerUpdated(indexCollection, indexIndex));
        propertyHandlerMap.put(ElementState.DEPRECATED, new PropertyHandlerDeprecated());
        propertyHandlerMap.put(ElementState.REMOVED, new PropertyHandlerRemoved());
    }

    void dropIndexForUpdate(StringBuilder indexSB, XmlIndex xmlIndex, String version, String classIndex,
                            String modelName, String tableName, PluginParameters pluginParameters) {
        dropIndex(indexSB, xmlIndex, version, classIndex, modelName, tableName, pluginParameters, true);
    }

    void dropIndexForRemoved(StringBuilder indexSB, XmlIndex xmlIndex, String version, String classIndex,
                             String modelName, String tableName, PluginParameters pluginParameters) {
        dropIndex(indexSB, xmlIndex, version, classIndex, modelName, tableName, pluginParameters, false);
    }

    void dropIndex(StringBuilder indexSB, XmlIndex xmlIndex, String version, String classIndex,
                   String modelName, String tableName, PluginParameters pluginParameters, boolean needRollback) {
        indexSB.append(DROP_INDEX_CHANGELOG_TEMPLATE
                .replace("${modelName}", modelName)
                .replace("${version}", version)
                .replace("${index}", classIndex)
                .replace("${indexIndex}", String.valueOf(indexIndex.incrementAndGet()))
                .replace("${indexName}", xmlIndex.getIndexName())
                .replace("${tableName}", tableName)
                .replace("${rollback}", needRollback ? getDropIndexRollback(xmlIndex, tableName, pluginParameters) : "<rollback/>"));
    }

    private String getDropIndexRollback(XmlIndex xmlIndex, String tableName, PluginParameters pluginParameters) {
        if (pluginParameters.isDropDeletedItemsImmediately()) {
            return "<rollback/>";
        }
        return DROP_INDEX_ROLLBACK_CHANGELOG_TEMPLATE
                .replace("${tableName}", tableName)
                .replace("${isUnique}", getOldIndexUniqueFlag(xmlIndex))
                .replace("${indexName}", xmlIndex.getIndexName())
                .replace("${indexColumns}", getComplexOldIndexColumns(xmlIndex));
    }

    private String getOldIndexUniqueFlag(XmlIndex xmlIndex) {
        Boolean oldUniqueFlag = xmlIndex.getOldValueChangedProperty(XmlIndex.UNIQUE_TAG);
        if (oldUniqueFlag == null) {
            oldUniqueFlag = xmlIndex.isUnique();
        }
        return String.valueOf(oldUniqueFlag);
    }

    void createIndex(StringBuilder indexesSB, MutableLong classIndex,
                     XmlModelClass modelClass, XmlIndex xmlIndex,
                     ModelParameters modelDiff, PluginParameters pluginParameters) {
        if (xmlIndex.isDeprecated() || xmlIndex.isPrimaryKey()) {
            return;
        }
        String indexTemplate = INDEX_CHANGELOG_TEMPLATE;
        if (pluginParameters.isDisableGenerateOracleLiquibase()) {
            indexTemplate = INDEX_CHANGELOG_WITHOUT_ORACLE_TEMPLATE;
        }
        String tableName;
        String indexColumnsTags;
        CharSequence indexColumnsRaw;
        if (isPrimitiveCollectionIndex(xmlIndex, modelClass)) {
            final XmlModelClassProperty xmlModelClassProperty = modelClass.getProperty(xmlIndex.getProperties().get(0).getName());
            tableName = xmlModelClassProperty.getCollectionTableName();
            indexColumnsTags = getIndexColumnsTags(List.of(xmlModelClassProperty.getColumnName()));
            indexColumnsRaw = xmlModelClassProperty.getColumnName();
        } else {
            tableName = modelClass.getTableName();
            indexColumnsTags = getComplexIndexColumnsTags(xmlIndex);
            indexColumnsRaw = getComplexIndexColumnsRaw(xmlIndex);
        }
        indexesSB.append(indexTemplate
                .replace("${modelName}", modelDiff.getModel().getModelName())
                .replace("${version}", modelDiff.getVersion())
                .replace("${index}", String.valueOf(classIndex.getValue()))
                .replace("${isUnique}", String.valueOf(xmlIndex.isUnique()))
                .replace("${isUniqueRaw}", xmlIndex.isUnique() ? "unique" : "")
                .replace("${indexIndex}", String.valueOf(indexIndex.incrementAndGet()))
                .replace("${indexName}", xmlIndex.getIndexName())
                .replace("${tableName}", tableName)
                .replace("${indexColumnsTags}", indexColumnsTags)
                .replace("${indexColumnsRaw}", indexColumnsRaw)
                .replace("${rollback1}", pluginParameters.isOptimizeChangelog() ?
                        "\n\t\t<rollback/>" :
                        String.format("\n\t\t<rollback>\n" +
                                "            <sql dbms=\"postgresql\">alter table ${defaultSchemaName}.%s reset (parallel_workers)</sql>\n" +
                                "        </rollback>", modelClass.getTableName()))
                .replace("${rollback2}", pluginParameters.isOptimizeChangelog() ?
                        "\n\t\t<rollback/>" :
                        "")
                .replace("${rollback3}", pluginParameters.isOptimizeChangelog() ?
                        "\n\t\t<rollback/>" :
                        String.format("\n\t\t<rollback>\n" +
                                "            <sql>drop index ${liquibase.pg.online} if exists %s</sql>\n" +
                                "        </rollback>", xmlIndex.getIndexName()))

        );
    }

    private boolean isPrimitiveCollectionIndex(XmlIndex xmlIndex, XmlModelClass modelClass) {
        if (xmlIndex.getProperties().size() != 1) {
            return false;
        }
        final Property property = xmlIndex.getProperties().get(0);
        final XmlModelClassProperty xmlModelClassProperty = modelClass.getPropertyNullable(property.getName());
        if (xmlModelClassProperty == null) {
            return false;
        }
        return xmlModelClassProperty.getCollectionType() != null && xmlModelClassProperty.getCategory() == PropertyType.PRIMITIVE;
    }

    private CharSequence getComplexIndexColumnsRaw(XmlIndex xmlIndex) {
        return getIndexColumnsRaw(xmlIndex.getProperties());
    }

    String getComplexIndexColumnsTags(XmlIndex xmlIndex) {
        return getIndexColumnsTags(getListIndexColumns(xmlIndex.getProperties()));
    }

    String getComplexOldIndexColumns(XmlIndex xmlIndex) {
        List<Property> oldIndexProperties = xmlIndex.getOldValueChangedProperty(XmlIndex.PROPERTY_TAG);
        // The columns in the index did not change, so maybe something else changed, for example uniqueness,
        // that's why we take the current index fields
        if (oldIndexProperties == null) {
            oldIndexProperties = xmlIndex.getProperties();
        }

        return getSetOldIndexColumns(oldIndexProperties, xmlIndex).stream()
                .map(columnName -> String.format("<column name=\"%s\"/>", columnName))
                .collect(Collectors.joining("\n            "));
    }

    private Set<String> getSetOldIndexColumns(List<Property> oldIndexProperties, XmlIndex xmlIndex) {
        Set<String> oldColumnNames = new HashSet<>();
        oldIndexProperties
                .stream()
                .forEach(indexProperty -> {
                    XmlModelClassProperty xmlModelClassProperty = indexProperty.getProperty();
                    XmlModelClass xmlModelClass = xmlModelClassProperty.getModelClass();
                    // Let's check that the class is embeddable and this property has not changed in the current release
                    if (xmlModelClass.isEmbeddable() && !xmlModelClass.propertyChanged(XmlModelClass.EMBEDDED_TAG)) {
                        XmlEmbeddedProperty embeddedProperty = XmlModelClass.getEmbeddedProperty(indexProperty.getPropertyOwner(),
                                NameHelper.getEmbeddedIndexPropertyName(indexProperty));

                        oldColumnNames.add(embeddedProperty.getColumnName());
                    } else {
                        if (xmlModelClassProperty.isEmbedded()) {

                            if (xmlModelClassProperty.propertyChanged(XmlModelClassProperty.EMBEDDED_TAG)) {
                                if (xmlModelClassProperty.propertyChanged(XmlModelClassProperty.COLUMN_NAME_TAG)) {
                                    oldColumnNames.add(xmlModelClassProperty.getOldValueChangedProperty(XmlModelClassProperty.COLUMN_NAME_TAG));
                                } else {
                                    throw new ColumnNotDefinedException(
                                        "The previous column name is not defined for index rollback"
                                    );
                                }
                            } else {
                                xmlModelClassProperty.getModelClass().getEmbeddedPropertyList().stream()
                                        .filter(xmlEmbeddedList -> xmlEmbeddedList.getName().equals(xmlModelClassProperty.getName()))
                                        .findFirst()
                                        .orElseThrow(() -> new NoFoundEmbeddedPropertyException(
                                                xmlModelClassProperty.getName(),
                                                xmlModelClassProperty.getModelClass()))
                                        .getEmbeddedPropertyList().forEach(xmlEmbeddedProperty -> {
                                            //probably here also will be propertyChanged(XmlEmbeddedProperty.COLUMN_NAME_TAG),
                                            //then you will need to add a condition for obtaining the previous column name, similarly to the condition below.
                                            oldColumnNames.add(xmlEmbeddedProperty.getColumnName());
                                        });
                            }

                        } else {
                            if (xmlModelClassProperty.propertyChanged(XmlModelClassProperty.COLUMN_NAME_TAG)) {
                                oldColumnNames.add(xmlModelClassProperty.getOldValueChangedProperty(XmlModelClassProperty.COLUMN_NAME_TAG));
                            } else {
                                oldColumnNames.add(xmlModelClassProperty.getColumnName());
                            }
                        }
                    }
                });
        return oldColumnNames;
    }


    private String getIndexColumnsTags(List<String> columnNames) {
        return
                columnNames.stream()
                        .map(columnName -> String.format("<column name=\"%s\"/>", columnName))
                        .collect(Collectors.joining("\n            "));
    }

    private String getIndexColumnsRaw(List<Property> properties) {
        return String.join(",", getListIndexColumns(properties));
    }

    private List<String> getListIndexColumns(List<Property> properties) {

        List<String> result = new ArrayList<>();

        properties.stream()
                .filter(property -> !property.getProperty().isDeprecated())
                .forEach(property -> {
                    if (property.getProperty().getModelClass().isEmbeddable()) {
                        XmlEmbeddedProperty embeddedProperty = XmlModelClass.getEmbeddedProperty(property.getPropertyOwner(),
                                NameHelper.getEmbeddedIndexPropertyName(property));

                        result.add(embeddedProperty.getColumnName());
                    } else if (property.getProperty().isEmbedded()) {
                        XmlEmbeddedList embeddedList = property.getProperty().getModelClass().getEmbeddedPropertyList().stream()
                                .filter(xmlEmbeddedList -> xmlEmbeddedList.getName().equals(property.getProperty().getName()))
                                .findFirst()
                                .orElseThrow(() -> new NoFoundEmbeddedPropertyException(
                                        property.getProperty().getName(),
                                        property.getProperty().getModelClass()));

                        if (embeddedList.isReference()) {
                            String refName = property.getName().split("\\.")[1];
                            embeddedList.getEmbeddedPropertyList().stream().filter(it -> Objects.equals(it.getName(), refName))
                                    .forEach(it -> result.add(it.getColumnName()));
                        } else {
                            embeddedList.getEmbeddedPropertyList().forEach(xmlEmbeddedProperty ->
                                    result.add(xmlEmbeddedProperty.getColumnName()));
                        }
                    } else {
                        result.add(property.getProperty().getColumnName());
                    }
                });

        return result;

    }

    void createClass(StringBuilder changesSB, MutableLong index, XmlModelClass modelClass, ModelParameters modelDiff, PluginParameters pluginParameters) {

        StringBuilder createTableSB = new StringBuilder();

        createTableSB.append(CREATE_CLASS_TEMPLATE
                .replace("${columns}", fillColumns(modelClass, modelDiff, pluginParameters))
                .replace("${index}", classIndex.getValue().toString())
                .replace("${rollback}", pluginParameters.isOptimizeChangelog() ? "\n\t\t<rollback/>" : "")
        );
        addCollectionTableTo(createTableSB);

        if (modelClass.getIndices() != null) {
            modelClass.getIndices().forEach(complexIndex ->
                    createIndex(indexesSB, classIndex, modelClass, complexIndex, modelDiff, pluginParameters)
            );
        }

        String templateClass = CLASS_CHANGELOG_TEMPLATE;

        if (pluginParameters.isDisableGenerateOracleLiquibase()) {
            templateClass = CLASS_CHANGELOG_WITHOUT_ORACLE_TEMPLATE;
        }

        String commonTemplate = createTableSB + templateClass;

        final String primaryKeyColumns = collectPrimaryKeysColumn(modelClass);
        String addPKTemplate = "";
        if (!StringUtils.isBlank(primaryKeyColumns)) {
            addPKTemplate = ADD_PK_TEMPLATE;
            if (pluginParameters.isDisableGenerateOracleLiquibase()) {
                addPKTemplate = ADD_PK_WITHOUT_ORACLE_TEMPLATE;
            }
            addPKTemplate = addPKTemplate
                    .replace("${pk_keys_column}", primaryKeyColumns)
                    .replace("${rollback}", pluginParameters.isOptimizeChangelog() ? "\n\t\t<rollback/>" : "");
        }
        String complexChanges = commonTemplate
                .replace("${addPK}", addPKTemplate)
                .replace("${modelName}", modelDiff.getModel().getModelName())
                .replace("${version}", modelDiff.getVersion())
                .replace("${index}", classIndex.getValue().toString())
                .replace("${tableName}", modelClass.getTableName())
                .replace("${remarks2}", createRemarks(modelClass, modelClass.isDeprecated()))
                .replace("${label}", modelClass.getLabel())
                .replace("${name}", modelClass.getName())
                .replace("${computedColumns}", computedColumns(modelClass, index, pluginParameters))
                .replace("${clobChangeSets}", createLobChangeSet(modelClass, modelDiff))
                .replace("${pkIndexName}", modelClass.getPkIndexName())
                .replace("${indexes}", indexesSB.toString());

        switch (modelClass.getStrategy()) {
            case JOINED:
                changesSB.append(complexChanges);
                break;
            case SINGLE_TABLE:
                changesSB.insert(0, complexChanges);
                break;
        }
    }

    public static String collectPrimaryKeysColumn(XmlModelClass modelClass) {
        Optional<XmlModelClassProperty> primaryKeyPropertyOpt = modelClass.getPropertiesWithIncome().stream()
                .filter(XmlModelClassProperty::isId).findAny();

        if (primaryKeyPropertyOpt.isEmpty()) {
            return "";
        }

        XmlModelClassProperty primaryKeyProperty = primaryKeyPropertyOpt.get();

        if (modelClass.getModel().containsClass(primaryKeyProperty.getType())) {
            return XmlModelClass.getEmbeddedList(primaryKeyProperty).getEmbeddedPropertyList().stream()
                    .map(XmlEmbeddedProperty::getColumnName)
                    .collect(Collectors.joining(", "));
        }

        return primaryKeyProperty.getColumnName();
    }

    private void addCollectionTableTo(StringBuilder createTableSB) {
        if (this.collectionColumnsSB.length() > 0) {
            createTableSB.append(collectionColumnsSB);
        }
    }

    private String fillColumns(XmlModelClass modelClass,
                               ModelParameters modelDiff,
                               PluginParameters pluginParameters) {
        StringBuilder columnsBuilder = new StringBuilder();
        modelClass.getPropertiesWithIncome().forEach(
                modelClassProperty ->
                        generateChangelogClassColumnsAndIndexes(indexCollection,
                                columnsBuilder, modelClassProperty, modelDiff, pluginParameters));

        return columnsBuilder.toString();
    }

    /**
     * Generate columns and indexes for changelog.xml
     *
     * @param collectionIndex The index of the collection
     * @param columnsBuilder  StringBuilder колонок
     * @param classProperty   The model's class property
     */
    private void generateChangelogClassColumnsAndIndexes(
            MutableInt collectionIndex, StringBuilder columnsBuilder,
            XmlModelClassProperty classProperty, ModelParameters modelDiff, PluginParameters pluginParameters) {
        if (StringUtils.isNotEmpty(classProperty.getComputedExpression()) || !classProperty.getSqlExpressions().isEmpty()) {
            return;
        }
        if (classProperty.getCollectionType() == null) {
            if (classProperty.isEmbedded()) {
                XmlModelClass.getEmbeddedList(classProperty).getEmbeddedPropertyList()
                        .forEach(xmlEmbeddedProperty ->
                                columnsBuilder.append(addPrimitiveColumnChangelog(
                                        xmlEmbeddedProperty.getColumnName(),
                                        xmlEmbeddedProperty.getProperty(),
                                        classProperty.isId(),
                                        classProperty)));
            } else {
                if (classProperty.getObjectLinks() == ObjectLinks.O2O && classProperty.getMappedBy() != null) {
                    return;
                }

                columnsBuilder.append(addPrimitiveColumnChangelog(
                        classProperty.getColumnName(),
                        classProperty,
                        classProperty.isMandatory(),
                        classProperty));
            }
        } else {
            addCollectionTable(collectionColumnsSB, classProperty, modelDiff, collectionIndex, this.classIndex, this.indexIndex, pluginParameters);
        }
    }

    private String addPrimitiveColumnChangelog(String columnName,
                                               XmlModelClassProperty property,
                                               boolean mandatory,
                                               XmlModelClassProperty propertyForRemarks) {
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

    public static void addCollectionTable(StringBuilder changesSB, XmlModelClassProperty modelClassProperty,
                                          ModelParameters modelDiff, MutableInt collectionIndex,
                                          MutableLong index, MutableInt indexIndex, PluginParameters pluginParameters) {
        if (modelClassProperty.getCollectionType() != null && modelClassProperty.getMappedBy() != null) {
            return;
        }
        collectionIndex.increment();
        indexIndex.increment();
        if (modelClassProperty.getCollectionType() == CollectionType.SET) {
            if (modelClassProperty.getCategory() == PropertyType.REFERENCE) {
                String templateReferencesSet = REFERENCES_SET_CHANGELOG_TEMPLATE;
                if (pluginParameters.isDisableGenerateOracleLiquibase()) {
                    templateReferencesSet = REFERENCES_SET_CHANGELOG_WITHOUT_ORACLE_TEMPLATE;
                }
                changesSB.append(templateReferencesSet
                        .replace("${modelName}", modelDiff.getModel().getModelName())
                        .replace("${version}", modelDiff.getVersion())
                        .replace("${index}", index.getValue().toString())
                        .replace("${collectionIndex}", collectionIndex.getValue().toString())
                        .replace("${indexIndex}", indexIndex.getValue().toString())
                        .replace("${tableName}", modelClassProperty.getCollectionTableName())
                        .replace("${name}", modelClassProperty.getModelClass().getName())
                        .replace("${propertyType}", modelClassProperty.getTypeInfo().getJavaName())
                        .replace("${propertyName}", modelClassProperty.getName())
                        .replace("${keyColumnName}", modelClassProperty.getKeyColumnName())
                        .replace("${columnName}", modelClassProperty.getColumnName())
                        .replace("${type}", getPropertyDbType(modelClassProperty))
                        .replace("${pkIndexName}", modelClassProperty.getCollectionPkIndexName())
                        .replace("${rollback}", pluginParameters.isOptimizeChangelog() ? "\n\t\t<rollback/>" : ""));
            } else {
                String templatePrimitivesSet = PRIMITIVES_SET_CHANGELOG_TEMPLATE;
                if (pluginParameters.isDisableGenerateOracleLiquibase()) {
                    templatePrimitivesSet = PRIMITIVES_SET_CHANGELOG_WITHOUT_ORACLE_TEMPLATE;
                }
                changesSB.append(templatePrimitivesSet
                        .replace("${modelName}", modelDiff.getModel().getModelName())
                        .replace("${version}", modelDiff.getVersion())
                        .replace("${index}", index.getValue().toString())
                        .replace("${collectionIndex}", collectionIndex.getValue().toString())
                        .replace("${indexIndex}", indexIndex.getValue().toString())
                        .replace("${tableName}", modelClassProperty.getCollectionTableName())
                        .replace("${name}", modelClassProperty.getModelClass().getName())
                        .replace("${propertyType}", modelClassProperty.getTypeInfo().getJavaName())
                        .replace("${propertyName}", modelClassProperty.getName())
                        .replace("${keyColumnName}", modelClassProperty.getKeyColumnName())
                        .replace("${columnName}", modelClassProperty.getColumnName())
                        .replace("${type}", getPropertyDbType(modelClassProperty))
                        .replace("${pkIndexName}", modelClassProperty.getCollectionPkIndexName())
                        .replace("${rollback}", pluginParameters.isOptimizeChangelog() ? "\n\t\t<rollback/>" : ""));
            }
        } else {
            throw new UnsupportedCollectionException(modelClassProperty.getModelClass().getName(), Collections.singletonList(modelClassProperty));
        }
    }

    /**
     * Returns a changset for each compiledexpression property in the class
     */
    private String computedColumns(XmlModelClass modelClass, MutableLong index, PluginParameters pluginParameters) {
        StringBuilder changesSB = new StringBuilder();
        modelClass.getPropertiesWithIncome()
                .stream()
                .filter(p -> !StringUtils.isEmpty(p.getComputedExpression()) || !p.getSqlExpressions().isEmpty())
                .forEach(p -> changesSB.append(CheckExpression.ComputedFieldsGenerator.addChangeSets(index, modelClass.getTableName(), p, pluginParameters)));
        return changesSB.toString();
    }

    /**
     * Returns a changeset for each lob property in the class
     */
    private String createLobChangeSet(XmlModelClass modelClass, ModelParameters modelDiff) {
        StringBuilder stringBuffer = new StringBuilder();
        modelClass.getPropertiesAsList().stream()
                .filter(property -> property.getTypeInfo().getHbmName().endsWith("lob"))
                .forEach(property ->
                        stringBuffer.append(ADD_LOB_CHANGELOG_TEMPLATE
                                .replace("${columnName}", property.getColumnName())
                                .replace("${version}", modelDiff.getVersion())
                                .replace("${index}", classIndex.getValue().toString())
                                .replace("${indexIndex}", String.valueOf(indexIndex.incrementAndGet()))
                                .replace("${modelName}", modelDiff.getModel().getModelName())
                                .replace("${tableName}", property.getModelClass().getTableName())));

        return stringBuffer.toString();
    }

    private static boolean isClassHaveBooleanDefaultValueProperties(XmlModelClass modelClass) {
        return modelClass.getPropertiesWithIncome().stream()
                .anyMatch(it -> isBooleanType(it.getType()) && StringUtils.isNotBlank(it.getDefaultValue()));
    }

    protected Stream<XmlIndex> filterIndexByElementState(ElementState elementState, ModelParameters modelDiff, XmlModelClass modelClass) {
        return modelDiff.getObjectByType(elementState, XmlIndex.class).stream().filter(o ->
                modelClass.getIndices().contains(o));
    }

    private static boolean isClassHaveBinaryDefaultValueProperties(XmlModelClass modelClass) {
        return modelClass.getPropertiesWithIncome().stream()
                .anyMatch(it -> isBinaryType(it) && StringUtils.isNotBlank(it.getDefaultValue()));
    }

}
