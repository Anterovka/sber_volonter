package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.exception.AnyPositionException;

public class CustomQueryResultDuplicationException extends AnyPositionException {
    public CustomQueryResultDuplicationException(String resultNames) {
        super(join("Results of user requests with names",
            resultNames, "declared in the model more than once"), "");
    }
}
