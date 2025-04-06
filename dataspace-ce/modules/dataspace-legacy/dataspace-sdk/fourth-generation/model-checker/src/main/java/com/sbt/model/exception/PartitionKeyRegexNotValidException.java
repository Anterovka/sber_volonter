package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.usermodel.UserXmlModelClass;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class PartitionKeyRegexNotValidException extends CheckXmlModelException {

    public PartitionKeyRegexNotValidException(XmlModelClass modelClass) {
        super(join(
            "Attribute",
            UserXmlModelClass.PARTITION_KEY_REGEX_TAG,
            "class",
            modelClass.getName(),
            "is not a regular expression"
        ), "A value correction is required");
    }

}
