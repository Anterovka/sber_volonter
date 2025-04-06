package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.List;
import java.util.stream.Collectors;

public class ExtendFinalClassException extends CheckXmlModelException {
    public ExtendFinalClassException(List<XmlModelClass> classes) {
super(String.format("In the model, there are inherited final classes: [%s]", makeDetails(classes)),
"The text cannot be inherited by final classes. Remove the final tag or inheritance.");
    }

    private static String makeDetails(List<XmlModelClass> classes) {
        return classes.stream()
.map(it -> "class" + it.getName() + "extends final class" + it.getExtendedClass().getName())
                .collect(Collectors.joining(", "));
    }
}
