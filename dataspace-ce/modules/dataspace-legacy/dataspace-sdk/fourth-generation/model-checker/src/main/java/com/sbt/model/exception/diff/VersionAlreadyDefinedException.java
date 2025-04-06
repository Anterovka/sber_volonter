package com.sbt.model.exception.diff;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class VersionAlreadyDefinedException extends CheckXmlModelException {
    public VersionAlreadyDefinedException(String version) {
        super(
            String.format("When analyzing changeLog, an earlier version of the model was found to be used %s. " +
                "It is necessary to increase the model version if there are changes in the model.", version),
            "Install a different version of the model"
        );
    }
}
