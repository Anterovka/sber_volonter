package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.data.model.XmlModelClass;


public class ExtendedClassDoesNotExistException extends CheckXmlModelException {

    public ExtendedClassDoesNotExistException(XmlModelClass modelClass) {
        super(join("Class", modelClass.getName(), "is inherited from the class", modelClass.getExtendedClassName(),
                ", however, the class", modelClass.getExtendedClassName(), "is not declared in the model"),
            "Necessary to specify an existing model class or remove inheritance");
    }
}
