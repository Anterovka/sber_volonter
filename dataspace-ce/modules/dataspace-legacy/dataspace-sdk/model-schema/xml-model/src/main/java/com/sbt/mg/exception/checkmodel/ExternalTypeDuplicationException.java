package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.exception.AnyPositionException;

public class ExternalTypeDuplicationException extends AnyPositionException {
    public ExternalTypeDuplicationException(String externalTypeName) {
        super(join("External type", externalTypeName, "is declared more than once in the model"), "");
    }
}
