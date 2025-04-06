package sbp.com.sbt.dataspace.feather.common;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Node Iterator
 *
 * @param <E> Element type
 */
final class NodeIterator<E> implements Iterator<E> {

    Node<E>[] nodes = new Node[10];
    int pos;

    /**
     * @param node Node
     */
    NodeIterator(Node<E> node) {
        nodes[0] = node;
        prepareNodes();
    }

    /**
     * Prepare nodes
     */
    void prepareNodes() {
        while (pos != -1) {
            Node<E> currentNode = nodes[pos];
            if (currentNode.nodes != null) {
                int newLength = pos + currentNode.nodes.size();
                if (nodes.length < newLength) {
                    Node<E>[] nodes2 = nodes;
                    nodes = new Node[newLength + (newLength >> 1)];
                    System.arraycopy(nodes2, 0, nodes, 0, pos);
                }
                --pos;
                for (int i = currentNode.nodes.size() - 1; i >= 0; --i) {
                    nodes[++pos] = currentNode.nodes.get(i);
                }
            } else {
                return;
            }
        }
    }

    @Override
    public boolean hasNext() {
        return pos != -1;
    }

    @Override
    public E next() {
        if (pos == -1) {
            throw new NoSuchElementException();
        }
        E result = nodes[pos--].value;
        prepareNodes();
        return result;
    }
}
