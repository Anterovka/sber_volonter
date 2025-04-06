package com.sbt.model.exception.diff;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class ChangeModelNameException extends CheckXmlModelException {

    public ChangeModelNameException(String oldName, String newName) {
        super(String.format("It is forbidden to change the model name (%s -> %s).", oldName, newName),
            "The model name participates in many processes. A change could lead to ambiguous behavior." +
                "Return the original name.");
    }
}
