package com.sbt.mg.exception.common;

import javax.annotation.Nonnull;

/**
 * Exception: Error during execution
 */
public class ExecuteException extends RuntimeException {
    /**
     * @param cause The reason
     */
    public ExecuteException(@Nonnull Throwable cause) {
        super("Error during execution", cause);
    }

    public ExecuteException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
