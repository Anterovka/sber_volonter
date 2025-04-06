package com.sbt.dictionary.exceptions;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassReference;
import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class ReferenceException extends DictionaryCheckParentException {

    public ReferenceException(Collection<XmlModelClass> modelClasses) {
        super(join("For reference books, the external link (reference) is not supported.",
                "The problem is with the links", prepareInfo(modelClasses)),
            join("Remove the indicated external links.",
                " If you need to store the identifier in an external system, simply save it as a string."));
    }

    private static String prepareInfo(Collection<XmlModelClass> modelClasses) {
        StringBuilder stringBuilder = new StringBuilder();
        modelClasses.forEach(modelClass -> {
// concatenate the names of external links into a string
            Set<String> refNames = modelClass.getReferencesAsList().stream().map(XmlModelClassReference::getName)
                    .collect(Collectors.toSet());

            stringBuilder.append('[');
            stringBuilder.append(String.join(", ", refNames));
            stringBuilder.append(']');
            stringBuilder.append(" in the class ");
            stringBuilder.append(modelClass.getName());
        });
        return stringBuilder.toString();
    }
}
