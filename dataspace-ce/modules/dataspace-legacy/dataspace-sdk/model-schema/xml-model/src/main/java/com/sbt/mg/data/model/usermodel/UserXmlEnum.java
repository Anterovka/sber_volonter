package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlTagName(UserXmlEnum.ENUM_TAG)
public class UserXmlEnum<T extends UserXmlEnumValue> {

    public static final String ENUM_TAG = "enum";

    public static final String NAME_TAG = "name";
    public static final String LABEL_TAG = "label";
    public static final String VALUE_TAG = "value";

    protected List<T> enumValue = new ArrayList<>();
    private String name;
    protected String label;

    @JsonCreator
    public UserXmlEnum(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                       @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG) String label
    ) {
        this.name = name;
        this.label = label;
    }

    public UserXmlEnum() {
    }

    @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = VALUE_TAG)
    public List<T> getEnumValues() {
        return enumValue;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = VALUE_TAG)
    public void setEnumValue(List<T> enumValue) {
        this.enumValue = enumValue;
    }

    @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG)
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserXmlEnum classEnum = (UserXmlEnum) o;
        return Objects.equals(name, classEnum.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Enum{" +
                "name='" + name + '\'' +
                '}';
    }
}
