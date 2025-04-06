package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Unexpected class of RAW object
 */
public class UnexpectedRawClassException extends FeatherException {

    static final Set<String> SUPPORTED_RAW_CLASS_NAMES = Stream.of(String.class.getName()).collect(Collectors.toSet());

    /**
     * @param rawClass Класс RAW-объекта
     */
    UnexpectedRawClassException(Class<?> rawClass) {
        super("Unexpected class of RAW object", param("Expected classes of RAW object", SUPPORTED_RAW_CLASS_NAMES), param("Class of RAW object", rawClass.getName()));
    }
}
