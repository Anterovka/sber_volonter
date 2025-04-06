package com.sbt.model;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.util.List;
import java.util.stream.Collectors;

public class SubscriptionsXmlValidationException extends RuntimeException {
    public SubscriptionsXmlValidationException(Throwable cause) {
        super("Error during validation of subscription.xml file. Details in cause.", cause);
    }

    public SubscriptionsXmlValidationException(List<SAXParseException> exceptionList) {
        super("Errors during validation of subscription.xml file: " + makeErrorList(exceptionList));
    }

    private static String makeErrorList(List<SAXParseException> exceptionList) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("[");

        final String messages = exceptionList.stream()
                .map(SAXException::getMessage)
                .collect(Collectors.joining("; "));

        stringBuilder.append(messages);

        stringBuilder.append("]");

        return stringBuilder.toString();
    }
}
