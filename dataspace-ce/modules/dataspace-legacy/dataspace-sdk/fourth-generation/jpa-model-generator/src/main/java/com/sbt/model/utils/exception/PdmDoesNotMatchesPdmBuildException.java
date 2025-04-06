package com.sbt.model.utils.exception;

import com.sbt.mg.exception.GeneralSdkException;

public class PdmDoesNotMatchesPdmBuildException extends GeneralSdkException {
    public PdmDoesNotMatchesPdmBuildException(String errorText) {
        super(String.format("Build scheme (pdm.xml) has been changed compared to the intermediate (pdm-build.xml): %s", errorText),
            "Changes cannot be made to the model.xml file after the intermediate release cycle is disabled and before the release version is issued (see documentation).");
    }
}
