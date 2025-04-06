package com.sbt.model.exception.diff;

import com.sbt.mg.data.model.CollectionType;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class CollectionTypeChangeException extends CheckXmlModelException {
    public CollectionTypeChangeException(XmlModelClassProperty prevProperty) {
        super(join("Violation of backward compatibility. Sign", XmlModelClassProperty.COLLECTION_TAG,
                "forbidden to change.Error in", propertyInCLass("е", prevProperty)),
            solutionText(prevProperty.getCollectionType()));
    }

    private static String solutionText(CollectionType type) {
        if (type == null) {
            return join("Remove the sign", XmlModelClassProperty.COLLECTION_TAG, "from propertiesа.");
        }
        return join("Restore the old value: (", type, ')');
    }
}
