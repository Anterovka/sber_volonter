package com.sbt.model.exception;

import com.sbt.mg.exception.GeneralSdkException;
import com.sbt.model.exception.parent.CheckModelException;

import java.util.Collection;

public class LockMarkupException extends CheckModelException {

    public LockMarkupException(Collection<String> classNames) {
        super(GeneralSdkException.join("The locking possibility can be specified only for basic types. Classes:", classNames),
            GeneralSdkException.join("Move the lock markup to the base type",
                "(all inheritors will have the ability to be blocked),",
                "otherwise remove the lock from the class."));
    }
}
