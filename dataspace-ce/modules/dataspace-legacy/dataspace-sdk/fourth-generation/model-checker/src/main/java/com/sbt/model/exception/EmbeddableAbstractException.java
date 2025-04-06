package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Collection;
import java.util.stream.Collectors;

public class EmbeddableAbstractException extends CheckXmlModelException {
    public EmbeddableAbstractException(Collection<XmlModelClass> modelClasses) {
        super(join("Embeddable classes cannot be abstract.", "And such are",
                prepareClasses(modelClasses)),
            join("Remove the flag from the specified classes", XmlModelClass.ABSTRACT_TAG, '.'));
    }

    private static String prepareClasses(Collection<XmlModelClass> classes) {
        return classes.stream()
            .map(XmlModelClass::getName)
            .collect(Collectors.joining(", "));
    }
}
