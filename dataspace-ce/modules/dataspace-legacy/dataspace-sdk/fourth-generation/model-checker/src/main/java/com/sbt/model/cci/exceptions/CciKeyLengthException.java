package com.sbt.model.cci.exceptions;

import com.sbt.model.exception.parent.CheckModelException;

import java.util.List;

/**
 * Exception: no field is indicated in the index.
 */
public class CciKeyLengthException extends CheckModelException {

    public CciKeyLengthException(String className, List<String> propertyNames, int currentLength, int maxLength) {
        super(join("In the class", className,
                "length of key interzone(cci) index consisting of fields", propertyNames.toString(),
                "may exceed the maximum allowable(",
                maxLength, "). The maximum length is calculated as the sum of the lengths of the fields that are part of the interzone index.",
                "The current key length is", currentLength, "."),
            "Specify property lengths so that their total length does not exceed the maximum key length of the cci index.");
    }
}
