package sbp.com.sbt.dataspace.feather.common;

/**
 * Procedure with 1 argument that throws an exception
 *
 * @param <A> Argument type
 */
@FunctionalInterface
public interface ThrowingProcedure1<A> {

    /**
     * Call
     *
     * @param  arg Argument
     */
    void call(A arg) throws Throwable;
}
