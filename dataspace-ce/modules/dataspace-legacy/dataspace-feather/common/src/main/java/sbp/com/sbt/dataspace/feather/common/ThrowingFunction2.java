package sbp.com.sbt.dataspace.feather.common;

/**
 * Function from 2 arguments that throws an exception
 *
 * @param <R>  Type of result
 * @param <A1> Type of argument 1
 * @param <A2> Type of argument 2
 */
@FunctionalInterface
public interface ThrowingFunction2<R, A1, A2> {

    /**
     * Call
     *
     * @param arg1 Argument 1
     * @param arg2 Argument 2
     * @return Result
     */
    R call(A1 arg1, A2 arg2) throws Throwable;
}
