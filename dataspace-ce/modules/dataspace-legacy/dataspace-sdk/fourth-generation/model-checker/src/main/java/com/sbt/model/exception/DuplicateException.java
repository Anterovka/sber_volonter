package com.sbt.model.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.List;
import java.util.Map;

public class DuplicateException extends CheckXmlModelException {
    public DuplicateException(String message, String solution) {
        super(message, solution);
    }

    public static DuplicateException duplicateExternalType(String valueType, String types) {
        return new DuplicateException(join("You cannot specify multiple identical ", valueType, " in an external-type ", types),
            join("leave only one mention of ", valueType, "in external-types"));
    }

    public static DuplicateException queryDuplicateProperty(List<String> xmlQueryPropertyList) {
        return new DuplicateException(join("You cannot specify multiple identical names for property:", xmlQueryPropertyList),
            join("leave Only One Mention", xmlQueryPropertyList));
    }

    public static <T> DuplicateException duplicatePhisicNameException(Map<String, List<T>> duplicateObjects) {
        return new DuplicateException(
            join("For the following model elements, identical physical names were formed, which is unacceptable:",
                duplicateObjects),
            join("Contact the developers"));
    }
}
