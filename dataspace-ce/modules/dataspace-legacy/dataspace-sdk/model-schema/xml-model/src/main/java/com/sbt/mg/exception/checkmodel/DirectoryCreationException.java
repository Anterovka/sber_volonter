package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.exception.AnyPositionException;

public class DirectoryCreationException extends AnyPositionException {
    public DirectoryCreationException(String directoryPath) {
        super(join("Failed to create folder:", directoryPath),
            "Check the access rights");
    }
}
