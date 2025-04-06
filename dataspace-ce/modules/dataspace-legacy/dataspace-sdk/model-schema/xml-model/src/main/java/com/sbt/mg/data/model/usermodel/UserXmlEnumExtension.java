package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;

@XmlTagName(UserXmlEnumValue.EXTENSION_TAG)
public class UserXmlEnumExtension {

    public static final String NAME_TAG = "name";
    public static final String VALUE_TAG = "value";

    private final String name;
    private final String value;

    public UserXmlEnumExtension(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                                @JacksonXmlProperty(isAttribute = true, localName = VALUE_TAG) String value) {
        this.name = name;
        this.value = value;
    }

    @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG)
    public String getName() {
        return name;
    }

    @JacksonXmlProperty(isAttribute = true, localName = VALUE_TAG)
    public String getValue() {
        return value;
    }

}
