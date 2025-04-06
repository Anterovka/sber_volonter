package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.usermodel.UserXmlModelClass;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class PartitionKeyRegexAttributeException extends CheckXmlModelException {

    public PartitionKeyRegexAttributeException(XmlModelClass modelClass) {
        super(join(
            "Attribute",
            UserXmlModelClass.PARTITION_KEY_REGEX_TAG,
            "нельзя использовать с",
            modelClass.getName()
        ), "The attribute needs to be removed");
    }

}
