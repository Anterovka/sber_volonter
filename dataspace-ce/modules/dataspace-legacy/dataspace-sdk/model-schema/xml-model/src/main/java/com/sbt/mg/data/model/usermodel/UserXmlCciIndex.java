package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

public class UserXmlCciIndex<T extends UserXmlProperty> {

    protected static final String PROPERTY_TAG = "property";
    private static final String NAME_TAG = "name";

    private String name;
    protected List<T> properties;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = PROPERTY_TAG)
    public void setIndexProperties(List<T> properties) {
        this.properties = properties;
    }


    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = PROPERTY_TAG)
    public List<T> getProperties() {
        if (properties == null) {
            properties = new ArrayList<>();
        }

        return properties;
    }

    @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
