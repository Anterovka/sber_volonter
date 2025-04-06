package com.sbt.model.index.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.List;

public class IndexCollectionException extends CheckXmlModelException {
    public IndexCollectionException(List<String> foundErrors) {
        super(join("Found indexes that contain collection fields. Errors:",
                foundErrors.toString()),
            "The fields must be removed from the index composition.");
    }
}
