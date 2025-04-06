package com.sbt.mg.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.mg.data.model.usermodel.UserXmlQueryId;

import java.util.Objects;

@XmlTagName(UserXmlQueryId.ID_TAG)
public class XmlQueryId extends UserXmlQueryId {


    @JsonIgnore
    private XmlQuery xmlQuery;

    public XmlQueryId(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                      @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG) String label,
                      @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG) String description) {
        super(name, label, description);
    }

    public XmlQuery getXmlQuery() {
        return xmlQuery;
    }

    @JsonIgnore
    public XmlQueryId setXmlQuery(XmlQuery xmlQuery) {
        this.xmlQuery = xmlQuery;
        return this;
    }

    @JsonIgnore
    public XmlQueryId copy() {
        return new XmlQueryId(name, label, description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        XmlQueryId that = (XmlQueryId) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
