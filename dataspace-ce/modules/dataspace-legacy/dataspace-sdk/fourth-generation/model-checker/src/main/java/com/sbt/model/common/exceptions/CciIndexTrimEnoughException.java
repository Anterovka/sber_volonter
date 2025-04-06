package com.sbt.model.common.exceptions;

import com.sbt.mg.jpa.JpaConstants;
import com.sbt.model.exception.parent.CheckModelException;

import java.util.List;

public class CciIndexTrimEnoughException extends CheckModelException {
    public CciIndexTrimEnoughException(String className, List<String> properties) {
        super(join("Automatic generation of the cci index name was not able to sufficiently shorten it, so the name",
                "satisfied the maximum possible length(", JpaConstants.MAX_CCI_NAME_LENGTH, "characters.",
                "Index is defined on class", className, "and consists of fields", properties.toString()),
            "Specify the name manually(the name attribute of the cci - index tag) or reduce the length of the " +
                "first field's name, the class name.");
    }
}
