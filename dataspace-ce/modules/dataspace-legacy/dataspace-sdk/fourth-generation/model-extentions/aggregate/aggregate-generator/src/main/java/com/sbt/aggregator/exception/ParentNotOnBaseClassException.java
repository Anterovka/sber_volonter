package com.sbt.aggregator.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;

import java.util.Collection;

public class ParentNotOnBaseClassException extends AggregateException {

    public ParentNotOnBaseClassException(String className, Collection<XmlModelClassProperty> parentProperties) {
        super(join("In class", className, "it is forbidden to define properties with the attribute parent = \"true\",",
                "since:", "\n1) A superclass is defined on the class (the extends attribute)", "\n2) Not all superclasses of the class", className, "are abstract",
                "\nErroneous properties:", collectClassProperties(parentProperties)),
            join("It is necessary to remove properties with the parent = \"true\" attribute on the class", className, '.',
                "Move them to the parent class or remove altogether. You can also fix the type,",
                "удалив extends или сделав всех предков абстрактными."));
    }
}
