package com.sbt.model.checker.inner.root;

import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.dataspace.pdm.xml.XmlMetaInformation;
import com.sbt.model.exception.PluginPropertyChangedException;

import java.util.Objects;

import static com.sbt.dataspace.pdm.xml.XmlMetaInformation.DISABLED_BASE_ENTITY_FIELDS;
import static com.sbt.dataspace.pdm.xml.XmlMetaInformation.USE_RENAMED_FIELDS;

public class PluginPropertiesChecker implements RootChecker {

    private final XmlMetaInformation metaInformation;
    private final PluginParameters pluginParameters;

    public PluginPropertiesChecker(XmlMetaInformation metaInformation, PluginParameters pluginParameters) {
        this.metaInformation = metaInformation;
        this.pluginParameters = pluginParameters;
    }

    @Override
    public void check() {
        checkUseRenamedFields();
        checkDisableBaseEntityFields();
    }

    private void checkUseRenamedFields() {
        if (Objects.isNull(metaInformation)) {
            return;
        }
        final boolean metaValue = Boolean.parseBoolean(metaInformation.getUseRenamedFields());
        if (pluginParameters.isUseRenamedFields() != metaValue) {
            throw new PluginPropertyChangedException(
                    USE_RENAMED_FIELDS,
                    String.valueOf(metaValue),
                    String.valueOf(pluginParameters.isUseRenamedFields()));
        }
    }

    private void checkDisableBaseEntityFields() {
        if (Objects.isNull(metaInformation)) {
            return;
        }
        final boolean metaValue = Boolean.parseBoolean(metaInformation.getDisableBaseEntityFields());
        if (pluginParameters.isDisableBaseEntityFields() != metaValue) {
            throw new PluginPropertyChangedException(
                    DISABLED_BASE_ENTITY_FIELDS,
                    String.valueOf(metaValue),
                    String.valueOf(pluginParameters.isUseRenamedFields()));
        }
    }
}
