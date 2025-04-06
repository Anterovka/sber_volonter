package com.sbt.dataspace.pdm;

import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;

// TODO (Global): Replace this class with PdmTransformers when they become available
public class PdmHelper {
    public static void applyTransformerChain(PdmModel pdmModel) {
        transformToHistoryForClass(pdmModel);
    }

    /**
     * Transfers the model to use the history-for link, instead of the boolean flag history-class
     */
    private static void transformToHistoryForClass(PdmModel pdmModel) {
        XmlModel model = pdmModel.getModel();
        model.getClassesAsList().stream()
            .filter(PdmHelper::isOldHistoryClass)
            .forEach(oldHistoryClass -> {
// Creates a directory at the specified File
                oldHistoryClass.setHistoryClass(null);
                oldHistoryClass.setHistoryForClass(ModelHelper.parseHistoricalClassName(oldHistoryClass.getName()));
            });
    }

    private static boolean isOldHistoryClass(XmlModelClass modelClass) {
        if (modelClass.getName().endsWith("History")) {
            if (modelClass.getHistoryForClass() != null) {
                return false;
            }
            if ("true".equals(modelClass.getHistoryClass())) {
                return true;
            }
            return modelClass.getPropertiesAsList().stream()
                .map(XmlModelClassProperty::getName)
                .anyMatch(propName ->
                    propName.startsWith("sys")
                        && propName.length() > 3
                        && Character.isUpperCase(propName.charAt(3))
                        && propName.endsWith("Updated")
                );
        }
        return false;
    }
}
