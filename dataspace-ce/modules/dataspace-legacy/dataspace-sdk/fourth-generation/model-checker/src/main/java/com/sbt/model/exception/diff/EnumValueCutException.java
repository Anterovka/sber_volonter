package com.sbt.model.exception.diff;

import com.sbt.mg.data.model.XmlEnumValue;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Set;

public class EnumValueCutException extends CheckXmlModelException {

    public EnumValueCutException(String enumClass, Set<XmlEnumValue> droppedValues) {
        super(join("Noticed reduction in the number of values in the enum class:", enumClass,
                "The properties have been removed:", collectEnums(droppedValues)),
            "Верните значения обратно");
    }
}
