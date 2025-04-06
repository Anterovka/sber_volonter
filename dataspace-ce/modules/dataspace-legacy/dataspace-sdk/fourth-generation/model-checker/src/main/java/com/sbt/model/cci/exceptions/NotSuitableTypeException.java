package com.sbt.model.cci.exceptions;

import com.sbt.model.cci.ModelCciLogic;
import com.sbt.model.exception.parent.CheckModelException;

/**
 * Exception: no field is indicated in the index.
 */
public class NotSuitableTypeException extends CheckModelException {

    public NotSuitableTypeException(String typeName) {
        super(join("Type", typeName, "cannot be used as cciIndex."),
            join("Use the type from the list:", ModelCciLogic.CCI_SUITABLE_TYPES, "or the type can be a soft link. " +
                "The text cannot be used for collections.Embedded types are described explicitly with cciIndex."));
    }
}
