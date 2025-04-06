package com.sbt.mg.exception.common;

import com.sbt.mg.exception.AnyPositionException;

public class ModelResourceNotUniqueException extends AnyPositionException {
    public ModelResourceNotUniqueException(int resourceAmount, String filePath) {
        super(join("Found", resourceAmount, "model resources by name", filePath),
            "The classpath of the project and the dependencies used must be checked.");
    }

}
