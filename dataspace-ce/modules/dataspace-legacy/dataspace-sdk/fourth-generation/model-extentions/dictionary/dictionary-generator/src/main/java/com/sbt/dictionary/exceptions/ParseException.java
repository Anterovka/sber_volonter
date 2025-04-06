package com.sbt.dictionary.exceptions;

import com.sbt.mg.exception.common.CompleteExecuteException;

public class ParseException extends CompleteExecuteException {

    public ParseException(String fileName, Throwable throwable) {
        super(String.format("Error while parsing file '%s'", fileName), throwable);
    }


}
