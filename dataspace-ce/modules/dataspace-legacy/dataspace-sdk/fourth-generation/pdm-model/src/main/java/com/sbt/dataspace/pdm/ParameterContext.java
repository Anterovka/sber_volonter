package com.sbt.dataspace.pdm;

public class ParameterContext {

    private final PluginParameters pluginParameters;
    private final ModelParameters modelParameters;

    public static ParameterContext emptyContext() {
        return new ParameterContext(PluginParameters.emptyParameters(), new ModelParameters());
    }

    public ParameterContext(PluginParameters pluginParameters, ModelParameters modelParameters) {
        if (pluginParameters == null) {
            this.pluginParameters = PluginParameters.emptyParameters();
        } else {
            this.pluginParameters = pluginParameters;
        }
        this.modelParameters = modelParameters;
    }

    public PluginParameters getPluginParameters() {
        return pluginParameters;
    }

    public ModelParameters getModelParameters() {
        return modelParameters;
    }
}
