package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.data.model.XmlModelClass;

public class UseIdPrefixChangingException extends CheckXmlModelException {
    public UseIdPrefixChangingException(XmlModelClass xmlModelClass) {
        super(String.format(
            "For the class %s, you cannot change the value of the attribute %s.",
            xmlModelClass.getName(),
            XmlModelClass.USE_ID_PREFIX_TAG));
    }
}
