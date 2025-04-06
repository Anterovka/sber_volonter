package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Testing of special ordered set")
public class SpecialSortedSetTest {

    @DisplayName("Тест")
    @Test
    public void test() {
        Set<Object> set = new SpecialSortedSet<>(null);

        assertEquals(0, set.size());
        assertThrows(java.lang.UnsupportedOperationException.class, set::isEmpty);
        assertThrows(java.lang.UnsupportedOperationException.class, () -> set.contains("test"));
        Iterator<Object> iterator = set.iterator();
        assertFalse(iterator.hasNext());
        assertThrows(java.lang.UnsupportedOperationException.class, set::toArray);
        assertThrows(java.lang.UnsupportedOperationException.class, () -> set.toArray(new String[0]));
        assertThrows(java.lang.UnsupportedOperationException.class, () -> set.add("test"));
        assertThrows(java.lang.UnsupportedOperationException.class, () -> set.remove("test"));
        assertThrows(java.lang.UnsupportedOperationException.class, () -> set.containsAll(Arrays.asList("test")));
        assertThrows(java.lang.UnsupportedOperationException.class, () -> set.addAll(Arrays.asList("test")));
        assertThrows(java.lang.UnsupportedOperationException.class, () -> set.retainAll(Arrays.asList("test")));
        assertThrows(java.lang.UnsupportedOperationException.class, () -> set.removeAll(Arrays.asList("test")));
        assertThrows(java.lang.UnsupportedOperationException.class, set::clear);
    }
}
