package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelExternalType;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class ExternalTypeNameRuleException extends CheckXmlModelException {

    public ExternalTypeNameRuleException(String type) {
        super(join("Attribute \"", XmlModelExternalType.TYPE_TAG, "\" external-type with value \"",
                type, "\" should start with an uppercase letter, not contain Cyrillic characters, and not be empty."),
            "Correct according to the requirements");
    }
}
