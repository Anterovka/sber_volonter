package com.sbt.mg.data.physicaltologicmapping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@XmlTagName("pdmEntity")
public class XmlPdmEntity {
    @JacksonXmlProperty(isAttribute = true, localName = "objectName")
    private String xmlObjectName;
    @JsonIgnore
    private Map<String, XmlPdmAttribute> xmlPdmAttributes = new HashMap<>();

    public XmlPdmEntity() {
    }

    public XmlPdmEntity addXmlPdmAttribute(XmlPdmAttribute xmlPdmAttribute){
        if(xmlPdmAttributes.containsKey(xmlPdmAttribute.getColumnName())) {

        }
        xmlPdmAttributes.put(xmlPdmAttribute.getColumnName(), xmlPdmAttribute);
        return this;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = "pdmAttribute")
    public List<XmlPdmAttribute> getXmlPdmAttributesAsList() {
        return new ArrayList<>(xmlPdmAttributes.values());
    }


    public String getXmlObjectName() {
        return xmlObjectName;
    }

    public void setXmlObjectName(String xmlObjectName) {
        this.xmlObjectName = xmlObjectName;
    }

    public Map<String, XmlPdmAttribute> getXmlPdmAttributes() {
        return xmlPdmAttributes;
    }

    public void setXmlPdmAttributesAsList(List<XmlPdmAttribute> xmlPdmAttributesList) {
        Map<String, XmlPdmAttribute> xmlPdmAttributesMap = new HashMap<>();
        xmlPdmAttributesList.forEach(xmlPdmAttribute -> xmlPdmAttributesMap.put(xmlPdmAttribute.getColumnName(), xmlPdmAttribute));
        this.xmlPdmAttributes = xmlPdmAttributesMap;
    }

    public void setXmlPdmAttributes(Map<String, XmlPdmAttribute> xmlPdmAttributes) {
        this.xmlPdmAttributes = xmlPdmAttributes;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XmlPdmEntity that = (XmlPdmEntity) o;
        return xmlObjectName.equals(that.xmlObjectName) && xmlPdmAttributes.equals(that.xmlPdmAttributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(xmlObjectName, xmlPdmAttributes);
    }

    @Override
    public String toString() {
        return "XmlPdmEntity{" +
                "xmlObjectName='" + xmlObjectName + '\'' +
                '}';
    }
}
