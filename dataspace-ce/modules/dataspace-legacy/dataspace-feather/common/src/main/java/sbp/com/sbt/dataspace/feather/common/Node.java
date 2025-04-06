package sbp.com.sbt.dataspace.feather.common;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Node
 *
 * @param <E> Element type
 */
public final class Node<E> implements Iterable<E> {

    E value;
    List<Node<E>> nodes;

    /**
     * @param value The value
     */
    Node(E value) {
        this.value = value;
    }

    /**
     * @param nodes Nodes
     */
    Node(List<Node<E>> nodes) {
        this.nodes = nodes;
    }

    /**
     * Get value
     */
    public E getValue() {
        return value;
    }

    @Override
    public Iterator<E> iterator() {
        return new NodeIterator<>(this);
    }

    /**
     * Create node
     *
     * @param value The value
     * @param <E>   Element type
     */
    public static <E> Node<E> node(E value) {
        return new Node<>(value);
    }

    /**
     * Create node
     *
     * @param nodes Nodes
     * @param <E>   Type of value
     */
    public static <E> Node<E> node(Node<E>... nodes) {
        return new Node<>(Arrays.asList(nodes));
    }

    /**
     * Create node
     *
     * @param nodes Nodes
     * @param <E>   Type of value
     */
    public static <E> Node<E> node(List<Node<E>> nodes) {
        return new Node<>(nodes);
    }
}
