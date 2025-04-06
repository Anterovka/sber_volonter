package com.sbt.pprb.paas.generator;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.dataspace.pdm.TargetFileHolder;
import com.sbt.model.ModelGenerator;

import java.io.File;
import java.util.Collections;
import java.util.Map;

public class PdmHandler {

    private final PluginParameters pluginParameters;

    public PdmHandler(PluginParameters pluginParameters) {
        this.pluginParameters = pluginParameters;
    }

    public ModelParameters generate(File source, File resource, String modelVersion, String jpaPackageName) {
        Map<String, String> pluginProperties = getPluginProperties();

        return new ModelGenerator().generatePdm(pluginParameters, modelVersion, jpaPackageName, source,
                new TargetFileHolder(resource), pluginProperties.get("version"));
    }

    public ModelParameters generate(File source,
                                    File resource,
                                    String modelVersion,
                                    String jpaPackageName,
                                    boolean addToGit,
                                    boolean makeDbChangeLog) {
        return generate(source,
                resource,
                modelVersion,
                jpaPackageName,
                addToGit,
                makeDbChangeLog,
                true);
    }

    public ModelParameters generate(File source,
                                    File resource,
                                    String modelVersion,
                                    String jpaPackageName,
                                    boolean addToGit,
                                    boolean makeDbChangeLog,
                                    boolean saveToFile) {
        Map<String, String> pluginProperties = getPluginProperties();

        return new ModelGenerator().generatePdm(
                pluginParameters,
                modelVersion,
                jpaPackageName,
                source,
                new TargetFileHolder(resource),
                pluginProperties.get("version"),
                addToGit,
                makeDbChangeLog,
                saveToFile);
    }

    private Map<String, String> getPluginProperties() {
        return Collections.emptyMap();
    }
}
