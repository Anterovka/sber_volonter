package com.sbt.base.model.generator.exception;

import com.sbt.parameters.enums.IdCategory;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SnowflakeOnEmbeddableKeyNotSupportedException extends BaseEntityGeneratorException {
    public SnowflakeOnEmbeddableKeyNotSupportedException(String className) {
        super(join("For an embeddable class used as the primary key of the class", className,
                "it is forbidden to use strategies",
                Arrays.stream(IdCategory.values())
                    .filter(idCategory -> idCategory.isGeneratedAlways() || idCategory.isGeneratedOnEmpty())
                    .map(Enum::name)
                    .sorted()
                    .collect(Collectors.joining(", "))),
            join("Use the strategy", IdCategory.MANUAL));
    }
}
