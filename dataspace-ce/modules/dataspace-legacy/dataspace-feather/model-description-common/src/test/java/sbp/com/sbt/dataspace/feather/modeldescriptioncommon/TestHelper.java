package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Assistant for testing
 */
public class TestHelper {

    static final String ENUM_TYPE = "Enum";
    static final String ENTITY_TYPE = "Entity";
    static final String ENTITY_TYPE2 = "Entity2";
    static final String ENTITY_TYPE3 = "Entity3";
    static final String PROPERTY_NAME = "property";
    static final String PROPERTY_NAME2 = "property2";
    static final String PROPERTY_NAME3 = "property3";
    static final String GROUP_NAME = "group";

    private TestHelper() {
    }

    /**
     * Make sure that the exception occurred due to
     *
     * @param exceptionClass Exception class
     * @param code           Код
     */
    static void assertThrowsCausedBy(Class<? extends Throwable> exceptionClass, Supplier<?> code) {
        try {
            code.get();
            fail("An exception was expected");
        } catch (Exception e) {
            while (e.getCause() != null) {
                e = (Exception) e.getCause();
            }
            assertEquals(exceptionClass, e.getClass());
        }
    }
}
