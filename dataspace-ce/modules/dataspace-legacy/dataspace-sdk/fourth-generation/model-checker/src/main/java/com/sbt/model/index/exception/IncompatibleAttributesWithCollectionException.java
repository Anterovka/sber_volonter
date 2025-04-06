package com.sbt.model.index.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class IncompatibleAttributesWithCollectionException extends CheckXmlModelException {
    public IncompatibleAttributesWithCollectionException(XmlModelClassProperty property) {
        super(join("On the attribute", property.getName(), "with collection type", XmlModelClassProperty.COLLECTION_TAG,
                "class", property.getModelClass().getName(),
                "properties cannot be defined", XmlModelClassProperty.INDEX_TAG, "and/or", XmlModelClassProperty.UNIQUE_TAG),
            join("Delete properties related to the index"));
    }
}
