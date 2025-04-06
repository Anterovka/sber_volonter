package com.sbt.base.model.generator.exception;

import com.sbt.parameters.enums.IdCategory;

public class NoIdNotSupportedForBaseEntityException extends BaseEntityGeneratorException {
    public NoIdNotSupportedForBaseEntityException(String modelClass) {
        super(join("Declare creation of id type", IdCategory.NO_ID, "is not allowed for base model class.",
                "Error in class", modelClass),
            "Fix the type of id generation. You can examine acceptable types of ids in the documentation.");
    }
}
