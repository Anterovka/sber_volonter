package com.sbt.dictionary;

import com.sbt.converter.InterfaceConverterToXml;
import com.sbt.dataspace.pdm.ModelGenerate;
import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PdmModel;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.dataspace.pdm.StreamModel;
import com.sbt.dataspace.pdm.xml.XmlMetaInformation;
import com.sbt.dictionary.check.DictionaryCheckModelLogic;
import com.sbt.dictionary.exceptions.RootDictionaryNotFoundException;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.XmlImport;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.mg.utils.LiquibasePropertyUtils;
import com.sbt.model.dictionary.base.RootDictionary;
import org.apache.commons.lang3.mutable.MutableLong;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sbt.mg.Helper.getTemplate;
import static com.sbt.mg.jpa.JpaConstants.OBJECT_ID;
import static com.sbt.sysversion.WildCards.CODE_WC;
import static com.sbt.sysversion.WildCards.COLUMNS_WC;
import static com.sbt.sysversion.WildCards.COLUMN_NAME_WC;
import static com.sbt.sysversion.WildCards.TABLE_CODE_WC;
import static com.sbt.sysversion.WildCards.TABLE_NAME_WC;
import static com.sbt.sysversion.WildCards.VALUE_WC;

public class DictionaryGenerator implements ModelGenerate {

    private static final String DEFAULT_FOLDER = "dictionary/";
    public static final String CURRENT_DATA_PREFIX = "current";

    protected static final String COLUMN_TEMPLATE = getTemplate("/templates/column.template");
    private static final String INSERT_ROOT_DICTIONARY_TEMPLATE = getTemplate("/templates/insertRootDictionary.changelog.template");

    public static final String PROJECT_NAME = "DICTIONARY_GENERATOR";

    private static final String INSERT_ROOT_DICTIONARY_WITHOUT_ORACLE_TEMPLATE = getTemplate("/templates/insertRootDictionary.changelog.withoutOracle.template");

    private boolean exceptionOnDictionaryReference;

    private boolean disableGenerateOracleLiquibase;

    @Override
    public int getPriority() {
        return 40;
    }

    @Override
    public String getProjectName() {
        return PROJECT_NAME;
    }

    @Override
    public void preInit(XmlModel model, PluginParameters pluginParameters) {
        this.exceptionOnDictionaryReference = pluginParameters.isExceptionOnDictionaryReference();

        Optional<XmlImport> xmlImport = model.getImports().stream()
            .filter(imp -> PROJECT_NAME.equals(imp.getType()))
            .findAny();
        if (xmlImport.isEmpty()) {
            model.getImports().add(new XmlImport(PROJECT_NAME, DEFAULT_FOLDER));
        } else if (xmlImport.get().getFile() == null) {
            xmlImport.get().setFile(DEFAULT_FOLDER);
        }
        this.disableGenerateOracleLiquibase = pluginParameters.isDisableGenerateOracleLiquibase();
    }

    public static String defineImportFolder(XmlModel model) {
        Optional<XmlImport> xmlImport = model.getImports().stream()
            .filter(imp -> PROJECT_NAME.equals(imp.getType()))
            .findAny();
        if (xmlImport.isPresent()) {
            if (xmlImport.get().getFile() == null) {
                return DEFAULT_FOLDER;
            }
            return xmlImport.get().getFile();
        } else {
            return DEFAULT_FOLDER;
        }
    }

    @Override
    public void initModel(XmlModel model, File file, ModelParameters modelParameters) {
        DictionaryCheckModelLogic.of(model).check(DictionaryCheckParams.Builder.create()
            .setWarnings(modelParameters.getWarnings())
            .setExceptionOnDictionaryReference(this.exceptionOnDictionaryReference)
            .build());

        addRootDictionary(model);
    }

    @Override
    public void initModel(XmlModel model, StreamModel streamModel, ModelParameters modelParameters) {
        DictionaryCheckModelLogic.of(model).check(DictionaryCheckParams.Builder.create()
            .setWarnings(modelParameters.getWarnings())
            .setExceptionOnDictionaryReference(this.exceptionOnDictionaryReference)
            .build());

        addRootDictionary(model);
    }

    private void addRootDictionary(XmlModel model) {
        InterfaceConverterToXml.convertToXml(RootDictionary.class, model);
    }

    @Override
    public String addDataToDB(MutableLong changelogId, XmlModel model, ModelParameters modelParameters) {
        StringBuilder liquibase = new StringBuilder();

        insertRootDictionary(liquibase, modelParameters.getPdmModel(), model);

        return liquibase.toString();
    }

    private void insertRootDictionary(StringBuilder liquibase, PdmModel pdmModel, XmlModel model) {
        if (!isChangelogHasInsertRootDictionary(pdmModel)) {
            XmlModelClass rootDictionaryClass = model.findClass(JpaConstants.ROOT_DICTIONARY_CLASS_NAME)
                .orElseThrow(RootDictionaryNotFoundException::new);

            insertByLb(rootDictionaryClass, liquibase);

            if (pdmModel.getMetaInformation() == null) {
                pdmModel.setMetaInformation(new XmlMetaInformation());
            }
            pdmModel.getMetaInformation().setHaveInsertRootDictionary(Boolean.TRUE.toString());
        }
    }

    private void insertByLb(XmlModelClass rootDictionaryClass, StringBuilder liquibase) {
        String primaryKey = "1";
        StringBuilder columnBuilder = new StringBuilder();

        addColumnAndValueToBuilder(columnBuilder, rootDictionaryClass, OBJECT_ID, primaryKey);
        addColumnAndValueToBuilder(columnBuilder, rootDictionaryClass, JpaConstants.JPA_DISCRIMINATOR_NAME, rootDictionaryClass.getName());

        String templateInsertRootDict = INSERT_ROOT_DICTIONARY_TEMPLATE;
        if (disableGenerateOracleLiquibase) {
            templateInsertRootDict = INSERT_ROOT_DICTIONARY_WITHOUT_ORACLE_TEMPLATE;
        }
        liquibase.append(templateInsertRootDict
            .replace(TABLE_NAME_WC, rootDictionaryClass.getTableName())
            .replace(TABLE_CODE_WC, rootDictionaryClass.getPropertyWithHierarchyInSingleTable(OBJECT_ID).getColumnName())
            .replace(CODE_WC, primaryKey)
            .replace(COLUMNS_WC, columnBuilder.toString())
            .replace("${rollback}",
                String.format("\n\t\t<rollback>\n" +
                        "            <sql>delete from %s where %s = '%s'</sql>\n" +
                        "        </rollback>",
                    rootDictionaryClass.getTableName(),
                    rootDictionaryClass.getPropertyWithHierarchyInSingleTable(OBJECT_ID).getColumnName(),
                    primaryKey))
            .replace("${rollbackPG}",
                String.format("\n\t\t<rollback>\n" +
                        "            <sql>delete from ${defaultSchemaName}.%s where %s = '%s'</sql>\n" +
                        "        </rollback>",
                    rootDictionaryClass.getTableName(),
                    rootDictionaryClass.getPropertyWithHierarchyInSingleTable(OBJECT_ID).getColumnName(),
                    primaryKey)));
    }

    private static boolean isChangelogHasInsertRootDictionary(PdmModel pdmModel) {
        return pdmModel != null &&
            pdmModel.getMetaInformation() != null &&
            Boolean.parseBoolean(pdmModel.getMetaInformation().getHaveInsertRootDictionary());
    }

    private static void addColumnAndValueToBuilder(StringBuilder columnBuilder, XmlModelClass aClass, String columnName, Object value) {
        columnBuilder.append(COLUMN_TEMPLATE
            .replace(COLUMN_NAME_WC, transformColumnName(aClass.getName(), columnName, aClass.getModel()))
            .replace(VALUE_WC, LiquibasePropertyUtils.computeValue(aClass.getPropertyWithHierarchyInSingleTable(columnName), value)));
    }

    private static String transformColumnName(String modelClass, String columnName, XmlModel model) {
        String name = replaceId(columnName);
        return getProperties(model, modelClass).get(name).getColumnName();
    }

    private static String replaceId(String columnName) {
        return JpaConstants.ID_NAME.equals(columnName) ? OBJECT_ID : columnName;
    }

    private static Map<String, XmlModelClassProperty> getProperties(XmlModel model, String entity) {
        return ModelHelper.getAllPropertiesWithInherited(model.getClassNullable(entity)).stream()
            .collect(Collectors.toMap(XmlModelClassProperty::getName, Function.identity()));
    }
}
