package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.Objects;

public class UserXmlProperty {

    public static final String NAME_TAG = "name";

    private String name;

    public UserXmlProperty(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name) {
        this.name = name;
    }

    @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG)
    public String getName() {
        return name;
    }

    public UserXmlProperty setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UserXmlProperty otherProperty = (UserXmlProperty) obj;
        return Objects.equals(name, otherProperty.name);

    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
