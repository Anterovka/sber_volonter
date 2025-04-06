package sbp.com.sbt.dataspace.feather.common;

/**
 * Procedure from 4 arguments
 *
 * @param <A1> Type of argument 1
 * @param <A2> Type of argument 2
 * @param <A3> Type of argument 3
 * @param <A4> Argument type 4
 */
@FunctionalInterface
public interface Procedure4<A1, A2, A3, A4> {

    /**
     * Call
     *
     * @param arg1 Argument 1
     * @param arg2 Argument 2
     * @param arg3 Argument 3
     * @param arg4 Argument 4
     */
    void call(A1 arg1, A2 arg2, A3 arg3, A4 arg4);
}
