package sbp.com.sbt.dataspace.feather.expressiontyperesolver;

import sbp.com.sbt.dataspace.feather.expressions.SpecificationWithEntityType;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Assistant
 */
final class Helper {

    static final Set<DataType> NUMBER_TYPES = EnumSet.of(DataType.BYTE, DataType.SHORT, DataType.INTEGER, DataType.LONG, DataType.FLOAT, DataType.DOUBLE, DataType.BIG_DECIMAL);
    static final Set<DataType> STRING_TYPES = EnumSet.of(DataType.CHARACTER, DataType.STRING, DataType.TEXT);
    static final Set<DataType> DATE_TYPES = EnumSet.of(DataType.DATE, DataType.DATETIME, DataType.OFFSET_DATETIME);
    static final Set<DataType> BOOLEAN_TYPES = EnumSet.of(DataType.BOOLEAN);
    static final ConditionImpl CONDITION = new ConditionImpl();
    static final ConditionalGroupImpl CONDITIONAL_GROUP = new ConditionalGroupImpl();

    private Helper() {
    }

    /**
     * Convert to type
     *
     * @param entityDescription Entity description
     * @param specification     Specification
     * @return Entity description
     */
    static EntityDescription cast(EntityDescription entityDescription, SpecificationWithEntityType<?> specification) {
        return specification == null ? entityDescription : entityDescription.cast(specification.getType());
    }

    /**
     * Get signature
     *
     * @param type Тип
     */
    static String getSignature(DataType type) {
        return "${" + type + "}";
    }

    /**
     * Check type
     *
     * @param expectedTypes Expected types
     * @param typesStream    Stream of types
     */
    static void checkType(Set<DataType> expectedTypes, Function<DataType, UnsupportedOperationException> exceptionInitializer, Stream<DataType> typesStream) {
        typesStream
                .filter(type -> !expectedTypes.contains(type))
                .findAny()
                .ifPresent(type -> {
                    throw exceptionInitializer.apply(type);
                });
    }

    /**
     * Get minimum type
     *
     * @param type Тип
     */
    static DataType getMinType(DataType type) {
        if (Helper.STRING_TYPES.contains(type)) {
            return DataType.STRING;
        } else if (Helper.NUMBER_TYPES.contains(type)) {
            return DataType.BIG_DECIMAL;
        } else if (Helper.DATE_TYPES.contains(type)) {
            return type;
        } else if (type == DataType.TIME) {
            return DataType.TIME;
        } else {
            throw new UnsupportedOperationException("${" + type + "}.$min");
        }
    }

    /**
     * Get maximum type
     *
     * @param type Тип
     */
    static DataType getMaxType(DataType type) {
        if (Helper.STRING_TYPES.contains(type)) {
            return DataType.STRING;
        } else if (Helper.NUMBER_TYPES.contains(type)) {
            return DataType.BIG_DECIMAL;
        } else if (Helper.DATE_TYPES.contains(type)) {
            return type;
        } else if (type == DataType.TIME) {
            return DataType.TIME;
        } else {
            throw new UnsupportedOperationException("${" + type + "}.$max");
        }
    }

    /**
     * Get sum type
     *
     * @param type Тип
     */
    static DataType getSumType(DataType type) {
        if (Helper.NUMBER_TYPES.contains(type)) {
            return DataType.BIG_DECIMAL;
        } else {
            throw new UnsupportedOperationException("${" + type + "}.$sum");
        }
    }

    /**
     * Get average type
     *
     * @param type Тип
     */
    static DataType getAvgType(DataType type) {
        if (Helper.NUMBER_TYPES.contains(type)) {
            return DataType.BIG_DECIMAL;
        } else {
            throw new UnsupportedOperationException("${" + type + "}.$avg");
        }
    }
}

