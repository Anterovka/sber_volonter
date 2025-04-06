package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Collection;

public class UndefinedExtendedClassException extends CheckXmlModelException {
    public UndefinedExtendedClassException(Collection<XmlModelClass> classList) {
        super(join("Classes with unspecified parents were found in the model. Classes: ", collectClasses(classList)),
            join("Determine correct parents for the listed classes or remove the EXTENDS tag", XmlModelClass.EXTENDS_TAG));
    }
}
