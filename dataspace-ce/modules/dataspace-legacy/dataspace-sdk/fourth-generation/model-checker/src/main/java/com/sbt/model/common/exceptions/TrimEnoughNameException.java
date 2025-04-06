package com.sbt.model.common.exceptions;

import java.util.List;

public class TrimEnoughNameException extends RuntimeException {
    public TrimEnoughNameException(List<String> nameElements) {
        super("Cannot trim enough elements " + nameElements.toString());
    }
}
