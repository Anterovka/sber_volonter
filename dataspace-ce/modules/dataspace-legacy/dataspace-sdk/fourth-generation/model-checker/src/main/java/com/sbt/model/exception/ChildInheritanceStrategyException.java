package com.sbt.model.exception;

import com.sbt.model.exception.parent.CheckModelException;

public class ChildInheritanceStrategyException extends CheckModelException {
    public ChildInheritanceStrategyException(String className) {
        super(join("The inheritance strategy can be defined only in base(non-abstract) classes.",
                "The definition was found on the class", className),
            "Move the definition of the inheritance strategy to the base class.");
    }
}
