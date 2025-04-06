package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.exception.AnyPositionException;

public class UnknownResourceException extends AnyPositionException {
    public UnknownResourceException(String path) {
        super(join("The resource was not found in the project at the following path:", path),
            "Check the correctness of the file position relative to the specified path");
    }
}
