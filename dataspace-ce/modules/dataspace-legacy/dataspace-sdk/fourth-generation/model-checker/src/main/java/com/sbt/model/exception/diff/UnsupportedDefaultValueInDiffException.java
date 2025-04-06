package com.sbt.model.exception.diff;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class UnsupportedDefaultValueInDiffException extends CheckXmlModelException {
    public UnsupportedDefaultValueInDiffException(XmlModelClassProperty property) {
        super(join(propertyInCLass("о", property),
                "was set as mandatory. Setting the default value for the type", property.getType(),
                "не определено"),
            "The default value should be removed, or an appropriate type should be specified.");
    }
}
