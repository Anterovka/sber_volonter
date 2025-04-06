package sbp.com.sbt.dataspace.feather.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.getString;
import static sbp.com.sbt.dataspace.feather.common.Node.node;

@DisplayName("Node Testing")
public class NodeTest {

    @DisplayName("Тест")
    @Test
    public void test() {
        Node<String> helloNode = node("Hello");
        Node<String> commaNode = node(",");
        Node<String> spaceNode = node(" ");
        Node<String> worldNode = node("world");
        Node<String> exclamationNode = node("!");

        assertEquals("Hello", helloNode.getValue());

        Node<String> node = node(
                node(
                        node(Arrays.asList(
                                helloNode,
                                commaNode)),
                        spaceNode),
                node(
                        worldNode,
                        exclamationNode),
                exclamationNode);

        assertEquals("Hello, world!!", getString(node));

        Iterator<String> iterator = helloNode.iterator();
        assertEquals("Hello", iterator.next());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @DisplayName("Тест (2)")
    @Test
    public void test2() {
        List<Node<String>> nodes = new ArrayList<>(20);
        StringBuilder stringBuilder = new StringBuilder();
        IntStream.range(1, 20).forEach(number -> {
            String string = String.valueOf(number);
            nodes.add(node(string));
            stringBuilder.append(string);
        });

        assertEquals(stringBuilder.toString(), CommonHelper.getString(node(nodes)));
    }
}
