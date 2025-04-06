package com.sbt.mg.data.physicaltologicmapping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@XmlTagName("pdmAttribute")
public class XmlPdmAttribute {
    @JacksonXmlProperty(isAttribute = true, localName = "columnName")
    private String columnName;
    @JsonIgnore
    private List<XmlField> xmlFields = new ArrayList<>();

    public XmlPdmAttribute() {
    }

    @JsonIgnore
    public XmlPdmAttribute addXmlField(XmlField xmlField){
        xmlFields.add(xmlField);
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = "field")
    public List<XmlField> getXmlFields() {
        return xmlFields;
    }


    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setXmlFields(List<XmlField> xmlFields) {
        this.xmlFields = xmlFields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XmlPdmAttribute that = (XmlPdmAttribute) o;

        return columnName.equals(that.columnName)
                && xmlFields.containsAll(that.xmlFields)
                && that.xmlFields.containsAll(xmlFields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnName, xmlFields);
    }
}
