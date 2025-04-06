package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.FeatherException;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Unexpected primitive value class
 */
public class UnexpectedPrimitiveValueClassException extends FeatherException {

    static final Set<String> SUPPORTED_PRIMITIVE_VALUE_CLASS_NAMES = Stream.of(DataType.CHARACTER, DataType.STRING, DataType.BYTE, DataType.SHORT, DataType.INTEGER, DataType.LONG, DataType.FLOAT, DataType.DOUBLE, DataType.BIG_DECIMAL, DataType.DATE, DataType.DATETIME, DataType.BOOLEAN)
        .map(DataType::getClass0)
        .map(Class::getName)
        .collect(Collectors.toSet());

    /**
     * @param primitiveValueClass The type of value
     */
    UnexpectedPrimitiveValueClassException(Class<?> primitiveValueClass) {
        super("Unexpected class of primitive value", param("Expected classes of primitive values", SUPPORTED_PRIMITIVE_VALUE_CLASS_NAMES), param("Class of primitive value", primitiveValueClass.getName()));
    }
}
