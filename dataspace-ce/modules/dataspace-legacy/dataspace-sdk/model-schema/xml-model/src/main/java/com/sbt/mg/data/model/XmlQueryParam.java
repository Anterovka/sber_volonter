package com.sbt.mg.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.mg.data.model.usermodel.UserXmlQueryParam;

import java.util.Objects;

@XmlTagName(XmlQueryParam.TAG)
public class XmlQueryParam extends UserXmlQueryParam {

    @JsonIgnore
    private XmlQuery xmlQuery;

    public XmlQueryParam(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                         @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG) String type,
                         @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG) String label,
                         @JacksonXmlProperty(isAttribute = true, localName = LENGTH_TAG) Integer length,
                         @JacksonXmlProperty(isAttribute = true, localName = MASK_TAG) String mask,
                         @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG) String description,
                         @JacksonXmlProperty(isAttribute = true, localName = COLLECTION_TAG) String collection,
                         @JacksonXmlProperty(isAttribute = true, localName = DEFAULT_VALUE_TAG) String defaultValue) {
        super(name, type, label, length, mask, description, collection, defaultValue);
    }

    public XmlQuery getXmlQuery() {
        return xmlQuery;
    }

    @JsonIgnore
    public XmlQueryParam setXmlQuery(XmlQuery xmlQuery) {
        this.xmlQuery = xmlQuery;
        return this;
    }

    @JsonIgnore
    public XmlQueryParam copy() {
        return new XmlQueryParam(name, type, label, length, mask, description, String.valueOf(collection), defaultValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        XmlQueryParam that = (XmlQueryParam) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
