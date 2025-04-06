package com.sbt.mg.data.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.mg.data.model.usermodel.UserXmlEnumExtension;
import com.sbt.mg.data.model.usermodel.UserXmlEnumValue;

@XmlTagName(UserXmlEnumValue.EXTENSION_TAG)
public class XmlEnumExtension extends UserXmlEnumExtension {

    public XmlEnumExtension(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                            @JacksonXmlProperty(isAttribute = true, localName = VALUE_TAG) String value) {
        super(name, value);
    }
}
