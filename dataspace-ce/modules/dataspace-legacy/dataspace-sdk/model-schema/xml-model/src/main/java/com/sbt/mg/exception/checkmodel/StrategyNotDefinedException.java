package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.data.model.XmlModelClass;

public class StrategyNotDefinedException extends CheckXmlModelException {
    public StrategyNotDefinedException(XmlModelClass modelClass) {
        super(join("The table name was not defined based on the strategy", modelClass.getStrategy(),
            "for class", modelClass.getName()), "Check that the strategy is correctly specified for the class.");
    }
}
