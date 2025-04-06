package sbp.com.sbt.dataspace.feather.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Testing the pointer")
public class PointerTest {

    @DisplayName("Test")
    @Test
    public void test() {
        assertNull(new Pointer<>().object);
        assertEquals(1, new Pointer<>(1).object);
    }
}
