package com.sbt.mg.exceptional;

/**
 * Function that throws an exception
 *
 * @param <P> Parameter type
 * @param <R> Result type
 */
public interface ExceptionableFunction<P, R> {
    /**
     * Apply
     *
     * @param param Parameter
     * @return Result
     */
    R apply(P param) throws Exception;
}
