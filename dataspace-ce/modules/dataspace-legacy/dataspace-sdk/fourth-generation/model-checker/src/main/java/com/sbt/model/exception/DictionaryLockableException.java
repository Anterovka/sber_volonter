package com.sbt.model.exception;

import com.sbt.mg.exception.GeneralSdkException;
import com.sbt.model.exception.parent.CheckModelException;

import java.util.List;

public class DictionaryLockableException extends CheckModelException {

    public DictionaryLockableException(List<String> classes) {
        super(GeneralSdkException.join("The lockable possibility is defined only for entities. Not for reference books.",
                "Detected in classes:",
                classes),
            "Remove lockable from the specified classes");
    }
}
