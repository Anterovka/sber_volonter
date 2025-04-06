package com.sbt.model.phase;

/**
 * Phase
 *
 * @param <R> Type of result
 * @param <P> Parameter type
 */
public interface Phase<R, P> {
    /**
     * Execute
     *
     * @param param
     * @return Result
     */
    R execute(P param);
}
