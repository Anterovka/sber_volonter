package com.sbt.model.exception.optimizechangelog;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class OldModelXmlNotFoundInPdmException extends CheckXmlModelException {
    public OldModelXmlNotFoundInPdmException() {
        super("In pdm.xml, the section (<source-models>) with the saved model.xml needed to perform optimizations on the changelog was not found." +
                "Presumably there were no releases of the model released from DataSpace version >= 1.9, or there were only \"-SNAPSHOT\" builds.",
            "For optimization execution, it is necessary to have a model release issued from DataSpace version >= 1.9");
    }
}
