package com.sbt.dataspace.pdm;

import com.sbt.mg.data.model.XmlModel;

import java.util.Objects;

public class ModelGenerateUtils {

    public static boolean isProjectNameEnable(XmlModel model, String projectName) {
        return model.getImports().stream()
                .anyMatch(it -> Objects.equals(it.getType(), projectName));
    }

    public static String getModelVersion(ModelParameters modelParameters, XmlModel model) {
        return modelParameters.getVersion() != null ? modelParameters.getVersion() : model.getVersion();
    }
}
