package sbp.com.sbt.dataspace.feather.common;

/**
 * Function from 0 arguments, throwing an exception
 *
 * @param <R> Type of result
 */
@FunctionalInterface
public interface ThrowingFunction0<R> {

    /**
     * Call
     *
     * @return Result
     */
    R call() throws Throwable;
}
