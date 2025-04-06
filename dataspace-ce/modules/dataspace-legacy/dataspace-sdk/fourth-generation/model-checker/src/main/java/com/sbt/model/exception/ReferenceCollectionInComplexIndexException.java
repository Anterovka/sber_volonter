package com.sbt.model.exception;

import com.sbt.model.exception.parent.CheckModelException;

import java.util.List;

/**
 * Exception: The index name is already set in the model
 */
public class ReferenceCollectionInComplexIndexException extends CheckModelException {

    public ReferenceCollectionInComplexIndexException(List<String> foundErrors) {
        super(join("Indices were found containing collections of references: ",
                foundErrors.toString()),
            "Index can consist of only one field of the links collection." +
                "In this case, the collection class will be indexed." +
                "There can be no other fields in such an index, since they belong to another table.");
    }
}
