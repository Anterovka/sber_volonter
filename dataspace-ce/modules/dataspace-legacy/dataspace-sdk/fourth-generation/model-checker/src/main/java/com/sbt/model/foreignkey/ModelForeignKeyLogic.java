package com.sbt.model.foreignkey;

import com.sbt.dataspace.pdm.PdmModel;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClassProperty;

import java.util.Objects;

import static com.sbt.mg.NameHelper.getFkIndexName;

public class ModelForeignKeyLogic {
    private final XmlModel model;
    private final PdmModel pdmModel;
    private final PluginParameters pluginParameters;

    public ModelForeignKeyLogic(XmlModel model, PluginParameters pluginParameters, PdmModel pdmModel) {
        this.model = model;
        this.pdmModel = pdmModel;
        this.pluginParameters = pluginParameters;
    }

    public void initForeignKeys() {
        transferOldFkNamesToNewModel();
        defineForeignKeyNames();
    }

    private void transferOldFkNamesToNewModel() {
        if (Objects.nonNull(pdmModel) && Objects.nonNull(pdmModel.getModel())) {
            pdmModel.getModel().getClassesAsList().stream()
                    .forEach(modelClass -> modelClass.getPropertiesAsList().stream()
                            .filter(xmlModelClassProperty -> xmlModelClassProperty.isFkGenerated() && Objects.nonNull(xmlModelClassProperty.getFkName()))
                            .forEach(xmlModelClassProperty -> model.addFkName(xmlModelClassProperty.getFkName())));
        }
    }

    private void defineForeignKeyNames() {
        model.getClassesAsList().stream()
                .forEach(modelClass -> modelClass.getPropertiesAsList().stream()
                        .filter(xmlModelClassProperty -> xmlModelClassProperty.isFkGenerated() && Objects.isNull(xmlModelClassProperty.getFkName()))
                        .forEach(xmlModelClassProperty -> {
                            xmlModelClassProperty.setFkName(getFkIndexName(xmlModelClassProperty, pluginParameters.getMaxDBObjectNameLength()));
                            xmlModelClassProperty.addChangedProperty(XmlModelClassProperty.FK_NAME_TAG, null);
                        }));
    }
}
