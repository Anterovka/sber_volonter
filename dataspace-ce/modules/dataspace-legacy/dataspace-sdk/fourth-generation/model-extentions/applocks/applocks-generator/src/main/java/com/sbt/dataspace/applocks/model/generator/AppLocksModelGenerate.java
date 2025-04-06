package com.sbt.dataspace.applocks.model.generator;

import com.sbt.dataspace.pdm.ModelGenerate;
import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.mg.data.model.XmlImport;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.parameters.enums.Changeable;
import sbp.com.sbt.dataspace.applocks.model.interfaces.LockInfo;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class AppLocksModelGenerate implements ModelGenerate {

    public static final String PROJECT_NAME = "APP_LOCKS";

    @Override
    public String getProjectName() {
        return PROJECT_NAME;
    }

    @Override
    public void preInit(XmlModel model, PluginParameters pluginParameters) {
        model.getImports().add(new XmlImport(PROJECT_NAME, ""));
    }

    @Override
    public void initModel(XmlModel xmlModel, File file, ModelParameters modelParameters) {
        xmlModel.getClassesAsList().forEach(xmlModelClass -> {
            if (Boolean.TRUE.equals(xmlModelClass.getLockable())) {
                xmlModelClass.addProperties(
                    XmlModelClassProperty.Builder.create()
                        .setName("syalTimeout")
                        .setType("Long")
                        .setLabel("Timeout of application locking")
                        .setChangeable(Changeable.SYSTEM)
                        .build(),

                    XmlModelClassProperty.Builder.create()
                        .setName("syalActive")
                        .setType("Boolean")
                        .setLabel("Indicator of activity of the application lock")
                        .setDefaultValue("false")
                        .setChangeable(Changeable.SYSTEM)
                        .build(),

                    XmlModelClassProperty.Builder.create()
                        .setName("syalToken")
                        .setType("String")
                        .setLabel("Application lock token")
                        .setLength(256)
                        .setChangeable(Changeable.SYSTEM)
                        .build(),

                    XmlModelClassProperty.Builder.create()
                        .setName("syalChangeDate")
                        .setType("Date")
                        .setLabel("Application lock change date")
                        .setChangeable(Changeable.SYSTEM)
                        .build(),

                    XmlModelClassProperty.Builder.create()
                        .setName("syalReason")
                        .setType("String")
                        .setLabel("Reason for installing/removing application lock")
                        .setLength(256)
                        .setChangeable(Changeable.READ_ONLY)
                        .build(),

                    XmlModelClassProperty.Builder.create()
                        .setName("syalUnlockTime")
                        .setType("OffsetDateTime")
                        .setLabel("Time of end of application lockdown")
                        .setChangeable(Changeable.READ_ONLY)
                        .build()
                );
            }
        });

    }

    @Override
    public List<String> addInterfacesToJpaModel(XmlModelClass xmlModelClass) {

        if (Boolean.TRUE.equals(xmlModelClass.getLockable())) {
            return Collections.singletonList(LockInfo.class.getSimpleName());
        }

        return Collections.emptyList();
    }

    @Override
    public List<String> addImports(XmlModelClass xmlModelClass) {
        if (Boolean.TRUE.equals(xmlModelClass.getLockable())) {
            return Collections.singletonList(LockInfo.class.getName());
        }
        return Collections.emptyList();
    }
}
