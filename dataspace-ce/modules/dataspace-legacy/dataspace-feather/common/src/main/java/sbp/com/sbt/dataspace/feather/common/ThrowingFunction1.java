package sbp.com.sbt.dataspace.feather.common;

/**
 * Function from 1 argument, throwing an exception
 *
 * @param <R> Result type
 * @param <A> Argument type
 */
@FunctionalInterface
public interface ThrowingFunction1<R, A> {

    /**
     * Call
     *
     * @param arg Argument
     * @return Result
     */
    R call(A arg) throws Throwable;
}
