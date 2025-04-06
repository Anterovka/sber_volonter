package com.sbt.model.checker.inner.root;

import com.sbt.dataspace.pdm.ParameterContext;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.model.exception.diff.VersionAlreadyDefinedException;

import static com.sbt.mg.jpa.JpaConstants.DEV_SNAPSHOT;

public class ChangelogChecker implements RootChecker {

    private final XmlModel newModel;
    private final String changelog;
    private final ParameterContext parameterContext;


    public ChangelogChecker(XmlModel newModel, String changelog, ParameterContext parameterContext) {
        this.newModel = newModel;
        this.changelog = changelog;
        this.parameterContext = parameterContext;
    }

    @Override
    public void check() {
        checkChangelogAlreadyHaveVersion();
    }

    private void checkChangelogAlreadyHaveVersion() {
        if (parameterContext.getPluginParameters().isChangelogChecks() && this.changelog != null) {
            final String version = newModel.getVersion();
            if (DEV_SNAPSHOT.equals(version)) {
                return;
            }
            if (changelog.contains(version + "-before") && parameterContext.getModelParameters().isModelChanged()) {
                throw new VersionAlreadyDefinedException(version);
            }
        }
    }
}
