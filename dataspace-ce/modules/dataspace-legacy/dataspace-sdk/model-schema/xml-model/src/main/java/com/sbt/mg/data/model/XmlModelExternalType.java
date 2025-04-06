package com.sbt.mg.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.usermodel.UserXmlModelExternalType;

import java.util.Objects;

public class XmlModelExternalType extends UserXmlModelExternalType {

    // referenceType="ClientReference"

    public static final String REFERENCE_TYPE_TAG = "reference-type";

    private String referenceType;
    private XmlModel model;


    public XmlModelExternalType(
            @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG) String type,
            @JacksonXmlProperty(isAttribute = true, localName = REFERENCE_TYPE_TAG) String referenceType,
            @JacksonXmlProperty(isAttribute = true, localName = MERGE_KIND_TAG) String mergeKind
    ) {
        super(type, mergeKind);
        this.referenceType = referenceType;
    }

    @JacksonXmlProperty(isAttribute = true, localName = REFERENCE_TYPE_TAG)
    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    /**
     * Get model
     */
    @JsonIgnore
    public XmlModel getModel() {
        return model;
    }

    /**
     * Set model
     */
    public void setModel(XmlModel model) {
        this.model = model;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        XmlModelExternalType that = (XmlModelExternalType) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return "XmlModelExternalType{" +
                "type='" + type + '\'' +
                ", referenceType='" + referenceType + '\'' +
                ", mergeKind='" + mergeKind + '\'' +
                '}';
    }
}
