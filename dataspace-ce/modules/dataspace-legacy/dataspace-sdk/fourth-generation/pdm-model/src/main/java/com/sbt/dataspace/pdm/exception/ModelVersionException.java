package com.sbt.dataspace.pdm.exception;

import com.sbt.mg.ModelHelper;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class ModelVersionException extends CheckXmlModelException {
    public ModelVersionException() {
        super(join("The model version must consist of Latin characters, digits, \"-\", \".\", \"_\", \"+\"",
                "The length is no more than", ModelHelper.MAX_CLASS_VERSION_NAME_LENGTH),
            "Fix the version according to the requirements");
    }
}
