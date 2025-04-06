package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

/**
 * Exception: The link name is a reserved word in the Java language
 */
public class ReferenceStorageException extends CheckXmlModelException {
    /**
     * @param reference reference with incorrect name
     */
    public ReferenceStorageException(XmlModelClassProperty reference) {
        super(join("Name of the link", reference.getName(), "of class", reference.getModelClass().getName(),
                "не найдено в embeddedList."),
            "Tell the developers about the error.");
    }
}
