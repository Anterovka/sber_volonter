package sbp.com.sbt.dataspace.feather.expressionscommon;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * General helper for expressions
 */
public final class CommonExpressionsHelper {

    private CommonExpressionsHelper() {
    }

    /**
     * Get specification
     *
     * @param specificationInitializer Initializer of the specification
     * @param specificationCode        Specification code
     * @param <S>                      The type of specification
     */
    public static <S> S getSpecification(Supplier<S> specificationInitializer, Consumer<S> specificationCode) {
        if (specificationCode == null) {
            return null;
        }
        S result = specificationInitializer.get();
        specificationCode.accept(result);
        return result;
    }
}
