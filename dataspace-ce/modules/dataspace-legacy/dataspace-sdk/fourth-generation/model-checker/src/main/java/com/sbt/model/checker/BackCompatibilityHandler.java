package com.sbt.model.checker;

import com.sbt.dataspace.pdm.PdmModel;
import com.sbt.sysversion.utils.semver.SemVerUtils;

import java.util.Objects;


public class BackCompatibilityHandler {

    public static void handlePdmModel(PdmModel pdmModel) {

        if (Objects.isNull(pdmModel)) {
            return;
        }

        handleVersionDeprecated(pdmModel);
    }

    /**
     * In older models, users could write any string to the version field that did not comply with semver rules.
     * For such models, we set null in outdated elements.
     *
     * @param pdmModel processed pdmModel
     */
    private static void handleVersionDeprecated(PdmModel pdmModel) {
        pdmModel.getModel().getClassesAsList().forEach(modelClass -> {
            if (Boolean.TRUE.equals(modelClass.isDeprecated()) &&
                !SemVerUtils.isSemverCompatible(modelClass.getVersionDeprecated())) {
                modelClass.setVersionDeprecated(null);
            }
            modelClass.getPropertiesAsList().stream()
                .filter(it -> Boolean.TRUE.equals(it.isDeprecated()) &&
                    !SemVerUtils.isSemverCompatible(it.getVersionDeprecated()))
                .forEach(it -> it.setVersionDeprecated(null));
        });
    }
}
