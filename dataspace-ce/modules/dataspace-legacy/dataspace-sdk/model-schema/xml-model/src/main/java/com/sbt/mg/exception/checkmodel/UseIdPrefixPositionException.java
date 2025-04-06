package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.data.model.XmlModelClass;

public class UseIdPrefixPositionException extends CheckXmlModelException {
    public UseIdPrefixPositionException(XmlModelClass xmlModelClass) {
        super(String.format(
            "For the class %s, you cannot use the attribute %s because the class is not the root class of the aggregate.",
            xmlModelClass.getName(),
            XmlModelClass.USE_ID_PREFIX_TAG));
    }
}
