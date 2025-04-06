package com.sbt.model.exception;

import com.sbt.model.exception.parent.CheckModelException;

public class EnableVersionedEntitiesException extends CheckModelException {

    public EnableVersionedEntitiesException() {
        super("Enabled the use of optimistic entity locking in JPA without explicitly specifying the parameter." +
                "Due to the fact that this locking is not always predictable, it is necessary to explicitly declare its use.",
                "Add the enableVersionedEntities parameter with a value of true to the model generation plugin.");
    }
}
