package com.sbt.dataspace.security.model.generator;

import com.sbt.converter.InterfaceConverterToXml;
import com.sbt.dataspace.pdm.ModelGenerate;
import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PdmModel;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.dataspace.pdm.xml.XmlMetaInformation;
import com.sbt.mg.data.model.XmlImport;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.jpa.JpaConstants;
import org.apache.commons.lang3.mutable.MutableLong;
import ru.sbertech.dataspace.security.model.interfaces.SysAdminSettings;
import ru.sbertech.dataspace.security.model.interfaces.SysCheckSelect;
import ru.sbertech.dataspace.security.model.interfaces.SysOperation;
import ru.sbertech.dataspace.security.model.interfaces.SysParamAddition;
import ru.sbertech.dataspace.security.model.interfaces.SysRootSecurity;

import java.io.File;
import java.util.Optional;

import static com.sbt.dataspace.pdm.ModelGenerateUtils.isProjectNameEnable;
import static com.sbt.mg.Helper.getTemplate;

public class GraphQlSecurityModelGenerate implements ModelGenerate {

    public static final String PROJECT_NAME = "GQLSECURITY";
    private static final String INSERT_TEMPLATE = getTemplate("/security/insertRootObject.changelog.template");
    private static final String INSERT_WITHOUT_ORACLE_TEMPLATE = getTemplate("/security/insertRootObject.changelog.withoutOracle.template");

    private PluginParameters pluginParameters = PluginParameters.emptyParameters();

    @Override
    public void preInit(XmlModel model, PluginParameters pluginParameters) {
        if (pluginParameters.getImportSpecification().isSecurity()) {
            Optional<XmlImport> xmlImport = model.getImports().stream()
                    .filter(imp -> PROJECT_NAME.equals(imp.getType()))
                    .findAny();
            if (!xmlImport.isPresent()) {
                model.getImports().add(new XmlImport(PROJECT_NAME, null));
            }
        }
        this.pluginParameters = pluginParameters;
    }

    @Override
    public String getProjectName() {
        return PROJECT_NAME;
    }

    @Override
    public void initModel(XmlModel model, File file, ModelParameters modelParameters) {
        if (!isProjectNameEnable(model, getProjectName())) {
            return;
        }

        InterfaceConverterToXml.convertToXml(SysRootSecurity.class, model);
        InterfaceConverterToXml.convertToXml(SysCheckSelect.class, model);
        InterfaceConverterToXml.convertToXml(SysOperation.class, model);
        InterfaceConverterToXml.convertToXml(SysAdminSettings.class, model);
        InterfaceConverterToXml.convertToXml(SysParamAddition.class, model);
    }

    @Override
    public String addDataToDB(MutableLong changelogId, XmlModel model, ModelParameters modelParameters) {
        if (!isProjectNameEnable(model, getProjectName())) {
            return "";
        }

        PdmModel pdmModel = modelParameters.getPdmModel();
        StringBuilder result = new StringBuilder();

        if (!hasChangelogRootSecurity(pdmModel)) {
            XmlModelClass rootDictionaryClass = model.findClass(JpaConstants.ROOT_SECURITY_CLASS_NAME)
                .orElseThrow(() -> new IllegalStateException(
                        String.format(
                            "The root class of the security system classes was not found.",
                            JpaConstants.ROOT_SECURITY_CLASS_NAME
                        )
                    )
                );

            if (pdmModel.getMetaInformation() == null) {
                pdmModel.setMetaInformation(new XmlMetaInformation());
            }
            pdmModel.getMetaInformation().setHaveInsertRootSecurity(Boolean.TRUE.toString());

            String templateInsert = INSERT_TEMPLATE;
            if (pluginParameters.isDisableGenerateOracleLiquibase()) {
                templateInsert = INSERT_WITHOUT_ORACLE_TEMPLATE;
            }
            result.append(templateInsert
                    .replace("${tableName}", rootDictionaryClass.getTableName())
                    .replace("${modelName}", modelParameters.getModel().getModelName())
                    .replace("${version}", modelParameters.getVersion())
                    .replace("${rollback}", pluginParameters.isOptimizeChangelog() ?
                            "\n\t\t<rollback/>" :
                            String.format("\n\t\t<rollback>\n" +
                                    "            <sql>delete from %s where OBJECT_ID = '1'</sql>\n" +
"        </rollback>", rootDictionaryClass.getTableName()))
            );

        }
        return result.toString();
    }

    private boolean hasChangelogRootSecurity(PdmModel pdmModel) {
        return pdmModel != null &&
                pdmModel.getMetaInformation() != null &&
                Boolean.parseBoolean(pdmModel.getMetaInformation().getHaveInsertRootSecurity());
    }
}
