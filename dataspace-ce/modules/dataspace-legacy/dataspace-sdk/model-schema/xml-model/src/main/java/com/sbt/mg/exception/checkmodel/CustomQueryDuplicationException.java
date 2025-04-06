package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.exception.AnyPositionException;

public class CustomQueryDuplicationException extends AnyPositionException {
    public CustomQueryDuplicationException(String customQueryName) {
        super(join("Request with name", customQueryName, "is declared in model more than once"), "");
    }
}
