package sbp.com.sbt.dataspace.feather.common;

/**
 * Function from 3 arguments
 *
 * @param <R>  Result type
 * @param <A1> Type of argument 1
 * @param <A2> Type of argument 2
 * @param <A3> Type of argument 3
 */
@FunctionalInterface
public interface Function3<R, A1, A2, A3> {

    /**
     * Call
     *
     * @param arg1 Argument 1
     * @param arg2 Argument 2
     * @param arg3 Argument 3
     * @return Result
     */
    R call(A1 arg1, A2 arg2, A3 arg3);
}
