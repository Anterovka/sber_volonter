package com.sbt.mg.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.mg.data.model.usermodel.UserXmlQueryProperty;

import java.util.Objects;

import static com.sbt.mg.data.model.usermodel.UserXmlQueryProperty.PROPERTY_TAG;

@XmlTagName(PROPERTY_TAG)
public class XmlQueryProperty extends UserXmlQueryProperty implements XmlProperty<XmlQuery> {

    @JsonIgnore
    private XmlQuery xmlQuery;

    public XmlQueryProperty(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                            @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG) String type,
                            @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG) String label,
                            @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG) String description) {
        super(name, type, label, description);
    }

    @JsonIgnore
    public XmlQuery getXmlQuery() {
        return xmlQuery;
    }

    /**
     * Method duplicates getModelClass().
     * Introduced to support a common interface
     */
    @JsonIgnore
    public XmlQuery getParent() {
        return xmlQuery;
    }

    @JsonIgnore
    public XmlQueryProperty setXmlQuery(XmlQuery xmlQuery) {
        this.xmlQuery = xmlQuery;
        return this;
    }

    @JsonIgnore
    public XmlQueryProperty copy() {
        return new XmlQueryProperty(name, type, label, description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        XmlQueryProperty that = (XmlQueryProperty) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
