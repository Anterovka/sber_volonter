package com.sbt.mg.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.Objects;

    /** The list item {@link XmlEmbeddedList} */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class XmlEmbeddedProperty {
    public static final String NAME_TAG = "name";
    public static final String COLUMN_NAME_TAG = "column-name";

    /** Name of the embeddable field (nested fields through a dot, but such are not supported) */
    private final String name;
    /**Physical name of the field for some class */
    private String columnName;

    /** Link to the last property in the chain of embeddable (well as the first, since chains are limited by checks) */
    @JsonIgnore
    private XmlModelClassProperty property;


    public XmlEmbeddedProperty(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                               @JacksonXmlProperty(isAttribute = true, localName = COLUMN_NAME_TAG) String columnName) {
        this.name = name;
        this.columnName = columnName;
    }

    @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG)
    public String getName() {
        return name;
    }

    @JacksonXmlProperty(isAttribute = true, localName = COLUMN_NAME_TAG)
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @JsonIgnore
    public XmlModelClassProperty getProperty() {
        return property;
    }

    public void setProperty(XmlModelClassProperty property) {
        this.property = property;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final XmlEmbeddedProperty that = (XmlEmbeddedProperty) obj;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
