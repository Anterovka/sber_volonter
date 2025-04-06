package com.sbt.model.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class NotAllModelNamesFilledException extends CheckXmlModelException {
    public NotAllModelNamesFilledException() {
        super("Not all models in the passed streams contain model names",
            "Imported models must have names.");
    }
}
