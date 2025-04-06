package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.GeneralSdkException;
import com.sbt.model.exception.parent.CheckModelException;

import java.util.List;

public class ManualColumnNameDefineException extends CheckModelException {

    public ManualColumnNameDefineException(List<XmlModelClassProperty> externalProperties) {
        super(GeneralSdkException.join("Forbidden to declare properties with the attribute ", XmlModelClassProperty.COLUMN_NAME_TAG,
                ". Incorrect properties: [", externalProperties, "]"),
            GeneralSdkException.join("Remove the use of the attribute ", XmlModelClassProperty.COLUMN_NAME_TAG, " in the model"));
    }

}
