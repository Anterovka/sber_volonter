package com.sbt.dataspace.pdm.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class NoModelInPdmException extends CheckXmlModelException {
    public NoModelInPdmException() {
        super("In the passed stream pdm.xml file, the model description is missing (the <model> tag).",
            "Необходимо понять, что такое pdm.xml.");
    }
}
