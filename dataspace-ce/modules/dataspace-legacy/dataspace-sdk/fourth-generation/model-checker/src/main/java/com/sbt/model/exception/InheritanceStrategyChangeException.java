package com.sbt.model.exception;

import com.sbt.mg.data.model.ClassStrategy;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.model.exception.parent.TestGeneratorException;

public class InheritanceStrategyChangeException extends TestGeneratorException {
    public InheritanceStrategyChangeException(XmlModelClass modelClass, ClassStrategy newStrategy, ClassStrategy prevStrategy) {
        super(join("For class", modelClass.getName(), "the inheritance strategy is changed to",
                newStrategy.name()),
            join("The inheritance strategy cannot be changed. Return", "Стратегия наследования изменяться не может. Верните",
                prevStrategy.name(),
                "By default, the value of ", ClassStrategy.JOINED.name(), " is used."));
    }
}
