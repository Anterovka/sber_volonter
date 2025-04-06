package com.sbt.model.exception.diff;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import static com.sbt.mg.utils.ClassUtils.isBaseClass;

public class ChangedExtendedClassException extends CheckXmlModelException {

    public ChangedExtendedClassException(XmlModelClass prevClass, XmlModelClass curClass) {
        super(join("Violation of backward compatibility inheritance class. Ancestor for the class",
                prevClass.getName(), "was previously:", getExtendClass(prevClass),
                ", and now: ", getExtendClass(curClass)),
            "Leave the previously declared ancestor.");
    }

    private static String getExtendClass(XmlModelClass modelClass) {
        return modelClass.getExtendedClassName() == null || isBaseClass(modelClass.getExtendedClassName()) ?
            "no parent" : modelClass.getExtendedClassName();
    }
}
