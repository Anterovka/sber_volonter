package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class AbstractClassIndexException extends CheckXmlModelException {
    public AbstractClassIndexException(XmlModelClass modelClass) {
        super(join("Models do not allow indexes on abstract classes. Error in class",
                modelClass.getName()),
            join("Set indexes on non-abstract derived classes"));
    }
}
