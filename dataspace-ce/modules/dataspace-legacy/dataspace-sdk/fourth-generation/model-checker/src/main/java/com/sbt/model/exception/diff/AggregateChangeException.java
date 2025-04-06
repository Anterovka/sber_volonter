package com.sbt.model.exception.diff;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class AggregateChangeException extends CheckXmlModelException {

    public AggregateChangeException(String modelClassName) {
        super(join("For class", modelClassName,
                "a change in the root of the aggregate was detected. This is not backward compatible change."),
            "Keep the aggregate structure unchanged.");
    }
}
