package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.GeneralSdkException;
import com.sbt.model.exception.parent.CheckModelException;

import java.util.List;

public class ManualExternalLinkDefineException extends CheckModelException {

    public ManualExternalLinkDefineException(List<XmlModelClassProperty> externalProperties) {
        super(GeneralSdkException.join("Forbidden to declare properties with the attribute ", XmlModelClassProperty.EXTERNAL_LINK_TAG,
            ". Incorrect properties: [", externalProperties, "]"),
            GeneralSdkException.join("Remove the use of attribute ", XmlModelClassProperty.EXTERNAL_LINK_TAG, " in the model"));
    }

}
