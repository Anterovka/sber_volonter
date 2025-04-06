package com.sbt.mg.data.physicaltologicmapping;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;


public class XmlLdmRoot {
    @JacksonXmlProperty(isAttribute = true, localName = "pdmEntity")
    private List<XmlPdmEntity> xmlPdmEntities = new ArrayList<>();

    public XmlLdmRoot() {
    }

    public List<XmlPdmEntity> getXmlPdmEntities() {
        return xmlPdmEntities;
    }

    public void setXmlPdmEntities(List<XmlPdmEntity> xmlPdmEntities) {
        this.xmlPdmEntities = xmlPdmEntities;
    }
}

