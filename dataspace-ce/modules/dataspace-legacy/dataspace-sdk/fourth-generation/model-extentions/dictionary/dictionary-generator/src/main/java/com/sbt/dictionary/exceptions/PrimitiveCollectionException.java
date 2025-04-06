package com.sbt.dictionary.exceptions;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

public class PrimitiveCollectionException extends DictionaryCheckParentException {

    public PrimitiveCollectionException(XmlModelClassProperty property, Object value) {
        super(join("Property value", property.getName(), "class", property.getModelClass().getName(),
                "is an instance of the class", value.getClass().getCanonicalName(), "which is not a List."),
            "The value must support the List interface.");
    }
}
