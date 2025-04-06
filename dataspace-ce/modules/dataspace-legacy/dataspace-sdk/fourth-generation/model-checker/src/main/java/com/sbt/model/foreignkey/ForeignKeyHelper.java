package com.sbt.model.foreignkey;

import com.sbt.dataspace.pdm.PdmModel;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;

import java.util.Locale;
import java.util.Objects;

public class ForeignKeyHelper {
    private ForeignKeyHelper() {
    }

    public static boolean isEnableCreateForeignKeys(PluginParameters pluginParameters) {
        return "create".equals(getForeignKeys(pluginParameters));
    }

    public static boolean isEnableDropForeignKeys(PluginParameters pluginParameters) {
        return "drop".equals(getForeignKeys(pluginParameters));
    }

    private static String getForeignKeys(PluginParameters pluginParameters) {
        return Objects.nonNull(pluginParameters.getForeignKeys()) ? pluginParameters.getForeignKeys().toLowerCase(Locale.ENGLISH) : null;
    }

    /**
     * The conditions under which foreign keys are generated
     *
     * @param xmlModelClassProperty
     * @return
     */
    public static boolean getForeignKeyCondition(XmlModelClassProperty xmlModelClassProperty) {
        return Boolean.TRUE.equals(xmlModelClassProperty.isParent())
            || isReferenceToDictionary(xmlModelClassProperty);
    }

    public static boolean getFkDeleteCascadeCondition(XmlModelClassProperty xmlModelClassProperty) {
        return Boolean.TRUE.equals(xmlModelClassProperty.isParent());
    }

    public static void prepareGenerationForeignKey(XmlModelClassProperty xmlModelClassProperty, boolean isEnableDeleteCascade) {
        if (Objects.nonNull(xmlModelClassProperty.getColumnName()) && Objects.nonNull(xmlModelClassProperty.getModelClass().getTableName())) {
            if (!xmlModelClassProperty.isFkGenerated()) {
                xmlModelClassProperty.setFkGenerated(true);
                xmlModelClassProperty.addChangedProperty(XmlModelClassProperty.FK_GENERATED_TAG, false);
            }
            setDeleteCascade(xmlModelClassProperty, isEnableDeleteCascade);
        }
    }

    private static void setDeleteCascade(XmlModelClassProperty xmlModelClassProperty, boolean isEnableDeleteCascade) {
        if (getFkDeleteCascadeCondition(xmlModelClassProperty)) {
            xmlModelClassProperty.setFkDeleteCascade(isEnableDeleteCascade);
        }
    }

    public static void prepareDropForeignKey(XmlModelClassProperty xmlModelClassProperty) {
        if (xmlModelClassProperty.isFkGenerated()) {
            xmlModelClassProperty.addChangedProperty(XmlModelClassProperty.FK_GENERATED_TAG, true);
            xmlModelClassProperty.setFkGenerated(false);
            xmlModelClassProperty.addChangedProperty(XmlModelClassProperty.FK_NAME_TAG, xmlModelClassProperty.getFkName());
            xmlModelClassProperty.setFkName(null);
            if (xmlModelClassProperty.isFkDeleteCascade()) {
                xmlModelClassProperty.addChangedProperty(XmlModelClassProperty.FK_DELETE_CASCADE_TAG, xmlModelClassProperty.isFkDeleteCascade());
                xmlModelClassProperty.setFkDeleteCascade(false);
            }
        }
    }

    public static void transferFkPropertiesFromOldModel(XmlModelClassProperty xmlModelClassProperty, PdmModel pdmModel, boolean isEnableDeleteCascade) {
        if (Objects.nonNull(pdmModel)) {
            XmlModelClass oldXmlModelClass = pdmModel.getModel().getClassNullable(xmlModelClassProperty.getModelClass().getName());
            if (Objects.nonNull(oldXmlModelClass)) {
                XmlModelClassProperty oldXmlModelClassProperty = oldXmlModelClass.getPropertyNullable(xmlModelClassProperty.getName());
                if (Objects.nonNull(oldXmlModelClassProperty)) {
                    xmlModelClassProperty.setFkGenerated(oldXmlModelClassProperty.isFkGenerated());
                    xmlModelClassProperty.setFkName(oldXmlModelClassProperty.getFkName());
                    setDeleteCascade(xmlModelClassProperty, isEnableDeleteCascade);
                }
            }
        }
    }

    // todo: move somewhere to common helpers, or in XmlModelClassProperty
    public static boolean isReferenceToDictionary(XmlModelClassProperty xmlModelClassProperty) {
        if (ModelHelper.isPrimitiveType(xmlModelClassProperty.getType()) || xmlModelClassProperty.isEnum()) {
            return false;
        }
        XmlModelClass xmlModelClass = xmlModelClassProperty.getModelClass().getModel().getClassNullable(xmlModelClassProperty.getType());
        if (Objects.isNull(xmlModelClass)) {
            return false;
        }
        return xmlModelClass.isDictionary();
    }
}
