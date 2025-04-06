package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Метод не найден
 */
public class MethodNotFoundException extends FeatherException {

    /**
     * @param methodSignature Сигнатура метода
     * @param context         Контекст
     */
    MethodNotFoundException(String methodSignature, String context) {
        super("Метод не найден", param("Сигнатура метода", methodSignature), param("Контекст", context));
    }
}
