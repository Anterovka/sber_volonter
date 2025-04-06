package com.sbt.mg.exceptional;

/**
 * Data recipient throwing an exception
 *
 * @param <R> Type of result
 */
public interface ExceptionableSupplier<R> {
    /**
     * Get
     *
     * @return Result
     */
    R get() throws Throwable;
}
