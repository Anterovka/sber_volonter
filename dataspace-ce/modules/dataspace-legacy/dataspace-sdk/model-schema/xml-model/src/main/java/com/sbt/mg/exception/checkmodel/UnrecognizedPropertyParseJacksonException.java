package com.sbt.mg.exception.checkmodel;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.mg.exception.AnyPositionException;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;

public class UnrecognizedPropertyParseJacksonException extends AnyPositionException {

    public UnrecognizedPropertyParseJacksonException(UnrecognizedPropertyException e) {
super(join("When reading the model, an unknown property was found for the tag <" + getTagName(e) + "> property: ", e.getPropertyName()),
join("Specify the correct property by selecting from the list: [", getKnownProperties(e), "]"));
    }

    private static String getTagName(UnrecognizedPropertyException e) {
        Class<?> clazz = e.getReferringClass();
        XmlTagName annotation = clazz.getAnnotation(XmlTagName.class);

        return annotation == null ?
            StringUtils.join("On class ",
                        clazz.getSimpleName(),
    " annotation is not installed ",
                        XmlTagName.class.getSimpleName()) :
                annotation.value();
    }

    private static String getKnownProperties(UnrecognizedPropertyException e) {
        return e.getKnownPropertyIds().stream().map(String::valueOf).collect(Collectors.joining(", "));
    }
}
