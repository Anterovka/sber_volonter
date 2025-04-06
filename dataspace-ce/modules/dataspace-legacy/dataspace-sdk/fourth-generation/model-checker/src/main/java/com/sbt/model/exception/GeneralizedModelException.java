package com.sbt.model.exception;

import com.sbt.mg.exception.SdkException;
import com.sbt.model.exception.parent.CheckModelException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GeneralizedModelException extends CheckModelException {

    private final List<SdkException> errors;

    public GeneralizedModelException(List<SdkException> errors){
        this.errors = Optional.ofNullable(errors).orElse(Collections.emptyList());
    }

    @Override
    protected List<SdkException> getMultipleExceptionList() {
        return errors;
    }

    public List<SdkException> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
