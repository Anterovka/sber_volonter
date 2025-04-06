package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassReference;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Collection;
import java.util.stream.Collectors;

public class UnsupportedReferenceInEmbeddableClassException extends CheckXmlModelException {
    public UnsupportedReferenceInEmbeddableClassException(XmlModelClass embeddableClass,
                                                          Collection<XmlModelClassReference> references) {
        super(join("In embeddable classes, it is forbidden to define external links (", XmlModelClass.REFERENCE_TAG,
                "). Errors in properties [",
                references.stream().map(XmlModelClassReference::getName).collect(Collectors.joining(", ")), ']',
                "class", embeddableClass.getName()),
            "Determine the specified properties in non-embeddable classes");
    }
}
