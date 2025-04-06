package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.exception.AnyPositionException;

public class CustomQueryNotFoundException extends AnyPositionException {
    public CustomQueryNotFoundException(String customQueryName) {
        super(join("Custom query", customQueryName, "not found in model"), "");
    }
}
