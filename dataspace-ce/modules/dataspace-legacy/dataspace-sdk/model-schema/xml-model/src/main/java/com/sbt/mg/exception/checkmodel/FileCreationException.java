package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.exception.AnyPositionException;

public class FileCreationException extends AnyPositionException {
    public FileCreationException(String fileName) {
        super(join("Unable to create file:", fileName),
            "Check the access rights");
    }
}
