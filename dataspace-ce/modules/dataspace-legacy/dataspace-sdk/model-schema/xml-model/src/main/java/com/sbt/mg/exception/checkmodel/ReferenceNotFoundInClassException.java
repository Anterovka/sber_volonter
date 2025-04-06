package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.exception.AnyPositionException;

public class ReferenceNotFoundInClassException extends AnyPositionException {
    public ReferenceNotFoundInClassException(XmlModelClass modelClass, String referenceName) {
        super(join("Not found external link", referenceName,
                "in the class", modelClass.getName()),
            "Check the correctness of model formation.");
    }
}
