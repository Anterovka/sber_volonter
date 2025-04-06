package sbp.com.sbt.dataspace.feather.common;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Common helper
 */
public final class CommonHelper {

    private CommonHelper() {
    }

    /**
     * Parameter
     *
     * @param name  Name
     * @param value The value
     */
    public static String param(String name, Object value) {
        return name + " = '" + value + '\'';
    }

    /**
     * Get full description
     *
     * @param description Description
     * @param context     Контекст
     */
    public static String getFullDescription(String description, String... context) {
        return context.length == 0 ? description : (description + " (" + String.join("; ", context) + ')');
    }

    /**
     * Check that it is not a null value
     *
     * @param value       Value
     * @param description Description
     * @param <T>         The type of value
     * @return Value
     */
    public static <T> T checkNotNull(T value, String description) {
        if (value == null) {
            throw new NullValueException(description);
        }
        return value;
    }

    /**
     * Wrap the function that throws an exception
     *
     * @param function             Function
     * @param exceptionInitializer Exception initializer
     * @param <T>                  The type of the result
     * @return Result
     */
    public static <T> T wrap(ThrowingFunction0<T> function, Function<Throwable, FeatherException> exceptionInitializer) {
        try {
            return function.call();
        } catch (Throwable t) {
            throw exceptionInitializer.apply(t);
        }
    }

    /**
     * Wrap the function that throws an exception
     *
     * @param function Function
     * @param <T>      Type of result
     * @return Result
     */
    public static <T> T wrap(ThrowingFunction0<T> function) {
        return wrap(function, WrappedException::new);
    }

    /**
     * Wrap the function that throws an exception
     *
     * @param resourceInitializer  Resource initializer
     * @param function             Function
     * @param exceptionInitializer Exception initializer
     * @param <R>                  Resource type
     * @param <T>                  The type of the result
     * @return Result
     */
    public static <R extends AutoCloseable, T> T wrapR(ThrowingFunction0<R> resourceInitializer, ThrowingFunction1<T, R> function, Function<Throwable, FeatherException> exceptionInitializer) {
        try (R resource = resourceInitializer.call()) {
            return function.call(resource);
        } catch (Throwable t) {
            throw exceptionInitializer.apply(t);
        }
    }

    /**
     * Wrap the function that throws an exception
     *
     * @param resourceInitializer The resource initializer
     * @param function            Function
     * @param <R>                 Type of resource
     * @param <T>                 Type of result
     * @return Result
     */
    public static <R extends AutoCloseable, T> T wrapR(ThrowingFunction0<R> resourceInitializer, ThrowingFunction1<T, R> function) {
        return wrapR(resourceInitializer, function, WrappedException::new);
    }

    /**
     * Execute for each element
     *
     * @param stream               Flow
     * @param procedure            The procedure
     * @param exceptionInitializer Exception initializer
     * @param <T>                  Type of element
     */
    public static <T> void forEach(Stream<T> stream, ThrowingProcedure1<T> procedure, BiFunction<Throwable, T, FeatherException> exceptionInitializer) {
        stream.forEach(element -> {
            try {
                procedure.call(element);
            } catch (Throwable t) {
                throw exceptionInitializer.apply(t, element);
            }
        });
    }

    /**
     * Add list of nodes to the nodes
     *
     * @param nodes          Nodes
     * @param joiningNode Соединяющий узел
     * @param nodeStream     Node stream
     * @param <E>            Element type
     */
    public static <E> void addNodeListToNodes(List<Node<E>> nodes, Node<E> joiningNode, Stream<Node<E>> nodeStream) {
        Pointer<Boolean> flag = new Pointer<>(Boolean.FALSE);
        nodeStream.forEach(node -> {
            if (Boolean.TRUE.equals(flag.object)) {
                nodes.add(joiningNode);
            } else {
                flag.object = Boolean.TRUE;
            }
            nodes.add(node);
        });
    }

    /**
     * Get string
     *
     * @param stringNode String node
     */
    public static String getString(Node<String> stringNode) {
        StringBuilder stringBuilder = new StringBuilder();
        stringNode.forEach(stringBuilder::append);
        return stringBuilder.toString();
    }
}
