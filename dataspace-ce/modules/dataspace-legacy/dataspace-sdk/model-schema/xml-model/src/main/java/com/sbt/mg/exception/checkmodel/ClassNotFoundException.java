package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.exception.AnyPositionException;

public class ClassNotFoundException extends AnyPositionException {
    public ClassNotFoundException(String className) {
        super(join("Class", className, "not found in model"), "");
    }
}
