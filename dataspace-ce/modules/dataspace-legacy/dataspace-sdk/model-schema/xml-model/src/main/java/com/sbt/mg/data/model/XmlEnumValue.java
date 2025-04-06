package com.sbt.mg.data.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.mg.data.model.usermodel.UserXmlEnum;
import com.sbt.mg.data.model.usermodel.UserXmlEnumValue;

@XmlTagName(UserXmlEnum.VALUE_TAG)
public class XmlEnumValue extends UserXmlEnumValue {

    public XmlEnumValue(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                        @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG) String label,
                        @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG) String description) {
        super(name, label, description);
    }
}
