package com.sbt.dataspace.security.exception;

import com.sbt.mg.exception.GeneralSdkException;

public class ImportedSecurityFileException extends GeneralSdkException {

    public ImportedSecurityFileException(String message) {
        super(GeneralSdkException.join("Error importing security file with predefined rules."),
            message);
    }
}
