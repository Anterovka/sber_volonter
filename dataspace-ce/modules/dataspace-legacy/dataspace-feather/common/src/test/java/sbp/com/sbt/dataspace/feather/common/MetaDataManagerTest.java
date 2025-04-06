package sbp.com.sbt.dataspace.feather.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Metadata Manager Testing")
public class MetaDataManagerTest {

    static final MetaDataManager META_DATA_MANAGER = new MetaDataManager();
    static final Object WRITE_KEY = new Object();
    static final Object WRITE_KEY2 = new Object();
    static final String VERSION = "DEV-SNAPSHOT";

    /**
     * Testing (2)
     *
     * @param correctWriteKey   Correct write key
     * @param incorrectWriteKey Incorrect write key
     */
    void test2(Object correctWriteKey, Object incorrectWriteKey) {
        assertNull(META_DATA_MANAGER.get(String.class));
        META_DATA_MANAGER.put(String.class, correctWriteKey, VERSION);
        assertEquals(VERSION, META_DATA_MANAGER.get(String.class));
        assertThrows(MetaDataAccessViolationException.class, () -> META_DATA_MANAGER.put(String.class, incorrectWriteKey, VERSION));
        assertThrows(MetaDataAccessViolationException.class, () -> META_DATA_MANAGER.remove(String.class, incorrectWriteKey));
        META_DATA_MANAGER.remove(String.class, correctWriteKey);
        assertNull(META_DATA_MANAGER.get(String.class));
        assertThrows(MetaDataAccessViolationException.class, () -> META_DATA_MANAGER.remove(String.class, correctWriteKey));
    }

    @DisplayName("Тест")
    @Test
    public void test() {
        test2(WRITE_KEY, WRITE_KEY2);
        test2(WRITE_KEY2, WRITE_KEY);
    }
}
