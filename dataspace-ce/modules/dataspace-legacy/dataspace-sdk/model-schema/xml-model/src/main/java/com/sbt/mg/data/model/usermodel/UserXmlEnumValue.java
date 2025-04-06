package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.XmlEnumExtension;
import com.sbt.mg.data.model.interfaces.XmlTagName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@XmlTagName(UserXmlEnum.VALUE_TAG)
public class UserXmlEnumValue {

    public static final String NAME_TAG = "name";
    public static final String LABEL_TAG = "label";
    public static final String DESCRIPTION_TAG = "description";
    public static final String EXTENSION_TAG = "extension";

    private final String name;
    private String label;
    private String description;
    private List<XmlEnumExtension> extensions = new ArrayList<>();

    public UserXmlEnumValue(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                            @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG) String label,
                            @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG) String description) {
        this.name = name;
        this.label = label;
        this.description = description;
    }

    @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG)
    public String getName() {
        return name;
    }

    @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG)
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG)
    public String getDescription() {
        return description;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = EXTENSION_TAG)
    public List<XmlEnumExtension> getExtensions() {
        return extensions;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = EXTENSION_TAG)
    public void setExtensions(List<XmlEnumExtension> extensions) {
        this.extensions = extensions;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserXmlEnumValue that = (UserXmlEnumValue) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
