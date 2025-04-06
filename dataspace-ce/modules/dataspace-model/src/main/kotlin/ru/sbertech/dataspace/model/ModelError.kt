package ru.sbertech.dataspace.model

import ru.sbertech.dataspace.primitive.type.PrimitiveType

sealed class ModelError(
    val message: String,
) {
    val code: String get() = javaClass.simpleName.substring(1)

    override fun toString() = "Error[$code]: $message"

    class N1 internal constructor(
        attributePath: String,
    ) : ModelError("$attributePath is not set")

    class N2 internal constructor(
        attributePath: String,
    ) : ModelError("$attributePath should not be set")

    class N3 internal constructor(
        attributePath: String,
        value: Any?,
        anotherAttributePath: String,
    ) : ModelError("'$value' from $attributePath has already been used by $anotherAttributePath")

    class N4 internal constructor(
        attributePath: String,
    ) : ModelError("$attributePath is blank")

    class N5 internal constructor(
        attributePath: String,
        value: String,
        regex: Regex,
    ) : ModelError("'$value' from $attributePath does not match regular expression '$regex'")

    class N6 internal constructor(
        attributePath: String,
        typeName: String,
        typesHolderPath: String,
    ) : ModelError("Type with name '$typeName' from $attributePath is not found among types of $typesHolderPath")

    class N7 internal constructor(
        attributePath: String,
        typePath: String,
    ) : ModelError("$typePath referenced by $attributePath is not an identifiable type")

    class N8 internal constructor(
        attributePath: String,
        typePath: String,
    ) : ModelError("$typePath referenced by $attributePath is not an embeddable type")

    class N9 internal constructor(
        attributePath: String,
        propertyName: String,
        propertiesHolderPath: String,
    ) : ModelError("Property with name '$propertyName' from $attributePath is not found among properties of $propertiesHolderPath")

    class N10 internal constructor(
        attributePath: String,
        propertyPath: String,
    ) : ModelError("$propertyPath referenced by $attributePath is not suitable for id")

    class N11 internal constructor(
        valueAttributePath: String,
        valueType: PrimitiveType,
        typeAttributePath: String,
        type: PrimitiveType,
    ) : ModelError("Type '$valueType' of value from $valueAttributePath is not equal to type '$type' from $typeAttributePath")

    class N12 internal constructor(
        propertyOverridePath: String,
    ) : ModelError("Corresponding property for $propertyOverridePath is not found")

    class N13 internal constructor(
        propertyOverridePath: String,
        correspondingPropertyPath: String,
    ) : ModelError("Kind of $propertyOverridePath is not matching kind of corresponding $correspondingPropertyPath")

    class N14 internal constructor(
        attributePath: String,
        typePath: String,
    ) : ModelError("$typePath referenced by $attributePath is not an entity type")

    class N15 internal constructor(
        attributePath: String,
        typePath: String,
    ) : ModelError("$typePath referenced by $attributePath is not an enum type")

    class N16 internal constructor(
        attributePath: String,
        enumValueName: String,
        enumValuesHolderPath: String,
    ) : ModelError("Enum value with name '$enumValueName' from $attributePath is not found among enum values of $enumValuesHolderPath")

    class N17 internal constructor(
        attributePath: String,
        propertyPath: Collection<String>,
        propertiesHolderPath: String,
    ) : ModelError("Property with path $propertyPath from $attributePath is not found among properties of $propertiesHolderPath")

    class N18 internal constructor(
        attributePath: String,
        propertyPath: String,
    ) : ModelError("$propertyPath referenced by $attributePath is not a reference property")

    class N19 internal constructor(
        attributePath: String,
        typePath: String,
        anotherTypePath: String,
    ) : ModelError("Type $typePath of reference property referenced by $attributePath is not equal to $anotherTypePath")

    class N20 internal constructor(
        propertyPath: String,
    ) : ModelError("$propertyPath is not belong to properties of entity type")
}
