package com.sbt.status.exception;

public class EqualStatusLinkException extends XmlStatusException {
    public EqualStatusLinkException(String mainStatus, String statusName) {
        super(join("Duplication of links to statuses in the status class was found", mainStatus,
                "\nDuplication is noticed on the property name:", statusName),
            join("Eliminate ambiguity by removing the redundant property or renaming it"));
    }
}
