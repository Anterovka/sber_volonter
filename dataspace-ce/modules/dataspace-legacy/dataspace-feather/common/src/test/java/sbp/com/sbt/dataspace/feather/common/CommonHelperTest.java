package sbp.com.sbt.dataspace.feather.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.addNodeListToNodes;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.checkNotNull;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.forEach;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.getFullDescription;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.getString;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.wrap;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.wrapR;
import static sbp.com.sbt.dataspace.feather.common.Node.node;

@DisplayName("Testing the general assistant")
public class CommonHelperTest {

    @DisplayName("Testing Wrappers")
    @Test
    public void wrapTest() {
        assertEquals("Test", wrap(() -> "Test", UnexpectedException::new));
        assertThrows(NullValueException.class, () -> wrap(() -> {
            throw new UnexpectedException();
        }, throwable -> new NullValueException("description")));

        assertEquals("Test", wrap(() -> "Test"));
        assertThrows(WrappedException.class, () -> wrap(() -> {
            throw new UnexpectedException();
        }));

        assertEquals(1, CommonHelper.<InputStream, Integer>wrapR(() -> new ByteArrayInputStream(new byte[]{1}), InputStream::read, UnexpectedException::new));
        assertThrows(NullValueException.class, () -> wrapR(() -> new ByteArrayInputStream(new byte[]{1}), inputStream -> {
            throw new UnexpectedException();
        }, throwable -> new NullValueException("description")));

        assertEquals(1, CommonHelper.<InputStream, Integer>wrapR(() -> new ByteArrayInputStream(new byte[]{1}), InputStream::read));
        assertThrows(WrappedException.class, () -> wrapR(() -> new ByteArrayInputStream(new byte[]{1}), inputStream -> {
            throw new UnexpectedException();
        }));

        assertDoesNotThrow(() -> forEach(Stream.of(1), BlackHoleList.blackHoleList()::add, (exception, element) -> new UnexpectedException()));
        assertThrows(NullValueException.class, () -> forEach(Stream.of(1), element -> {
            throw new UnexpectedException(new UnexpectedException());
        }, (exception, element) -> new NullValueException("description")));
    }

    @DisplayName("Testing adding node list to nodes")
    @Test
    public void addNodeListToNodesTest() {
        List<Node<String>> nodes = new ArrayList<>();
        Stream<Node<String>> nodesStream = Stream.of(1, 2, 3)
            .map(String::valueOf)
            .map(Node::node);
        addNodeListToNodes(nodes, node(","), nodesStream);
        assertEquals("1,2,3", getString(node(nodes)));
    }

    @DisplayName("Тест")
    @Test
    public void test() {
        assertEquals("name = 'value'", param("name", "value"));
        assertEquals("description", getFullDescription("description"));
        assertEquals("description (name1 = 'value1'; name2 = 'value2')", getFullDescription("description", param("name1", "value1"), param("name2", "value2")));
        assertThrows(NullValueException.class, () -> checkNotNull(null, "description"));
        assertEquals("value", checkNotNull("value", "description"));
    }
}
