package com.sbt.model.phase.serviceitems;

import com.sbt.dataspace.pdm.PdmModel;
import com.sbt.dataspace.pdm.xml.XmlMetaInformation;

import static com.sbt.mg.Helper.getTemplate;

public class H2DbFunctions {

    private static final String H2_DECODE_FUNCTION_TEMPLATE = getTemplate("/templates/h2FunctionDecodeBase64.template");

    public static void addToDb(StringBuilder liquibase, PdmModel pdmModel) {
        addH2DecodeBase64FunctionTo(liquibase, pdmModel);
    }

    private static void addH2DecodeBase64FunctionTo(StringBuilder liquibase, PdmModel pdmModel) {
        if (!isChangeLogHasDecodeBase64Function(pdmModel)) {
            liquibase.append(H2_DECODE_FUNCTION_TEMPLATE);
            if (pdmModel.getMetaInformation() == null) {
                pdmModel.setMetaInformation(new XmlMetaInformation());
            }
            pdmModel.getMetaInformation().setHaveH2DecodeBase64Function(Boolean.TRUE.toString());
        }
    }

    private static boolean isChangeLogHasDecodeBase64Function(PdmModel pdmModel) {
        return pdmModel != null &&
                pdmModel.getMetaInformation() != null &&
                Boolean.parseBoolean(pdmModel.getMetaInformation().getHaveH2DecodeBase64Function());
    }
}
