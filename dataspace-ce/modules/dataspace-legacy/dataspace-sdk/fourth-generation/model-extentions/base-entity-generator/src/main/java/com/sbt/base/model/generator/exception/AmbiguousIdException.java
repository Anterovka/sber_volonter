package com.sbt.base.model.generator.exception;

import java.util.List;

public class AmbiguousIdException extends BaseEntityGeneratorException {
    public AmbiguousIdException(String className, List<String> idFields) {
        super(join("As an identifier in the class ", className, "the fields are indicated:", idFields),
            "Необходимо указать одно поле.");
    }
}
