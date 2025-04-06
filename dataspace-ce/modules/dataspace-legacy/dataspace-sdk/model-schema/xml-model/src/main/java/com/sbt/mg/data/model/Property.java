package com.sbt.mg.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.usermodel.UserXmlProperty;

public class Property extends UserXmlProperty {

    @JsonIgnore
    private XmlIndex xmlIndex;

    @JsonIgnore
    private XmlModelClassProperty property;
    @JsonIgnore
    private XmlModelClassReference reference;
    @JsonIgnore
    private XmlModelClassProperty propertyOwner;

    public Property(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name) {
        super(name);
    }

    @JsonIgnore
    public XmlIndex getXmlIndex() {
        return xmlIndex;
    }

    public void setXmlIndex(XmlIndex xmlIndex) {
        this.xmlIndex = xmlIndex;
    }

    @JsonIgnore
    public XmlModelClassProperty getProperty() {
        return property;
    }

    public void setProperty(XmlModelClassProperty property) {
        this.property = property;
    }

    @JsonIgnore
    public XmlModelClassReference getReference() {
        return reference;
    }

    public void setReference(XmlModelClassReference reference) {
        this.reference = reference;
    }

    @JsonIgnore
    public XmlModelClassProperty getPropertyOwner() {
        return propertyOwner;
    }

    public void setPropertyOwner(XmlModelClassProperty propertyOwner) {
        this.propertyOwner = propertyOwner;
    }
}
