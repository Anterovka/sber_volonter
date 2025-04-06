package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Collection;

public class EmbeddableExtendsException extends CheckXmlModelException {
    public EmbeddableExtendsException(Collection<XmlModelClass> modelClasses) {
        super(join("Embeddable classes cannot extend",
                "and cannot be expanded (be in extends of another class).",
                "Parent-child relationship found between classes",
                prepareClasses(modelClasses)),
            join("Create an embeddable class with all necessary fields without inheritance."));
    }

    private static String prepareClasses(Collection<XmlModelClass> classes) {
        StringBuilder result = new StringBuilder();
        classes.forEach(modelClass -> {
            result.append(modelClass.getName());
            result.append(" и ");
            result.append(modelClass.getExtendedClassName());
            result.append(", ");
        });
        result.delete(result.length() - 2, result.length());
        result.append('.');

        return result.toString();
    }
}
