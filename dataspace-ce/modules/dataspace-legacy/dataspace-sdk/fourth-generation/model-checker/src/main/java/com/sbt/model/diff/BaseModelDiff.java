package com.sbt.model.diff;

import com.sbt.dataspace.pdm.ParameterContext;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.model.exception.diff.ChangeModelNameException;

import java.util.logging.Logger;

public class BaseModelDiff implements DiffHandler {
    private static final Logger LOGGER = Logger.getLogger(BaseModelDiff.class.getName());

    private ParameterContext parameterContext;

    @Override
    public void handler(XmlModel newModel, XmlModel baseModel, ParameterContext parameterContext) {
        LOGGER.info("\n\n The verification phase of the model (beginning)");
        this.parameterContext = parameterContext;
        checkModelName(newModel, baseModel);
        checkComponentCode(newModel, baseModel);
        LOGGER.info("\n\n  The verification phase of the model (completion)");
    }

    private void checkModelName(XmlModel newModel, XmlModel baseModel) {
        if (parameterContext.getPluginParameters().isEnableModelNameChangeCheck() &&
            !baseModel.getModelName().equalsIgnoreCase(newModel.getModelName())) {
            throw new ChangeModelNameException(baseModel.getModelName(), newModel.getModelName());
        }
    }

    private void checkComponentCode(XmlModel newModel, XmlModel baseModel) {
        if (!baseModel.getComponentCode().equals(newModel.getComponentCode())) {
            baseModel.setComponentCode(newModel.getComponentCode());
        }
    }
}
