package sbp.com.sbt.dataspace.feather.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testing the list \"Black hole\"")
public class BlackHoleListTest {

    @DisplayName("Тест")
    @Test
    public void test() {
        BlackHoleList<String> blackHoleList = BlackHoleList.blackHoleList();
        assertEquals(0, blackHoleList.size());
        assertTrue(blackHoleList.isEmpty());
        assertFalse(blackHoleList.contains("Test"));
        assertEquals(Collections.emptyIterator(), blackHoleList.iterator());
        assertArrayEquals(new Object[0], blackHoleList.toArray());
        assertArrayEquals(new Object[0], blackHoleList.toArray(new Object[0]));
        assertFalse(blackHoleList.add("Test"));
        assertTrue(blackHoleList.isEmpty());
        assertFalse(blackHoleList.remove("Test"));
        assertTrue(blackHoleList.isEmpty());
        assertFalse(blackHoleList.containsAll(Arrays.asList("Test")));
        assertFalse(blackHoleList.addAll(Arrays.asList("Test")));
        assertTrue(blackHoleList.isEmpty());
        assertFalse(blackHoleList.addAll(0, Arrays.asList("Test")));
        assertTrue(blackHoleList.isEmpty());
        assertFalse(blackHoleList.removeAll(Arrays.asList("Test")));
        assertTrue(blackHoleList.isEmpty());
        assertFalse(blackHoleList.retainAll(Arrays.asList("Test")));
        assertTrue(blackHoleList.isEmpty());
        blackHoleList.clear();
        assertTrue(blackHoleList.isEmpty());
        assertThrows(IndexOutOfBoundsException.class, () -> blackHoleList.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> blackHoleList.set(0, "Test"));
        assertTrue(blackHoleList.isEmpty());
        assertThrows(IndexOutOfBoundsException.class, () -> blackHoleList.add(0, "Test"));
        assertTrue(blackHoleList.isEmpty());
        assertThrows(IndexOutOfBoundsException.class, () -> blackHoleList.remove(0));
        assertTrue(blackHoleList.isEmpty());
        assertEquals(-1, blackHoleList.indexOf("Test"));
        assertEquals(-1, blackHoleList.lastIndexOf("Test"));
        assertEquals(Collections.emptyListIterator(), blackHoleList.listIterator());
        assertThrows(IndexOutOfBoundsException.class, () -> blackHoleList.listIterator(0));
        assertThrows(IndexOutOfBoundsException.class, () -> blackHoleList.subList(0, 1));
        assertEquals("[]", blackHoleList.toString());
    }
}
