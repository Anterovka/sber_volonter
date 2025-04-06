package sbp.com.sbt.dataspace.feather.expressions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.wrap;

@DisplayName("Testing the abstract builder")
public class AbstractBuilderTest {

    @DisplayName("Checking protected methods")
    @Test
    public void protectedMethodsCheck() {
        Arrays.stream(ExpressionsProcessor.class.getMethods()).forEach(expressionsProcessorMethod -> {
            Method abstractBuilderMethod = wrap(() -> AbstractBuilder.class.getDeclaredMethod(expressionsProcessorMethod.getName(), expressionsProcessorMethod.getParameterTypes()));
            assertEquals(expressionsProcessorMethod.getReturnType(), abstractBuilderMethod.getReturnType());
            int modifiers = abstractBuilderMethod.getModifiers();
            assertFalse(Modifier.isPrivate(modifiers));
            assertTrue(Modifier.isProtected(modifiers));
            assertFalse(Modifier.isPublic(modifiers));
            assertFalse(Modifier.isAbstract(modifiers));
            assertTrue(Modifier.isFinal(modifiers));
            assertFalse(Modifier.isStatic(modifiers));
            assertFalse(Modifier.isSynchronized(modifiers));
        });
    }
}
