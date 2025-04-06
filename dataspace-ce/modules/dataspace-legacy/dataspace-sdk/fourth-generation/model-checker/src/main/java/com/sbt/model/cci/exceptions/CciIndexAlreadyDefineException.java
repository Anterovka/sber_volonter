package com.sbt.model.cci.exceptions;

import com.sbt.model.exception.parent.CheckModelException;

public class CciIndexAlreadyDefineException extends CheckModelException {

    public CciIndexAlreadyDefineException(String propertyName, String indexClassName) {
        super(join("Interzonal index with the property", propertyName, "in the class", indexClassName,
                "redundant. The index is already defined, having this property in the indexed."),
            "Удалите индекс.");
    }
}
