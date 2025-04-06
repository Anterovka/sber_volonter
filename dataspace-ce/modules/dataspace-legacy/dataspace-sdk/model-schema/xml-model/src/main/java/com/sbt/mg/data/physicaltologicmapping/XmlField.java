package com.sbt.mg.data.physicaltologicmapping;

import com.sbt.mg.data.model.interfaces.XmlTagName;

import java.util.Objects;


@XmlTagName("field")
public class XmlField {

    private String ldmEntityCode;
    private String ldmAttributeCode;


    public XmlField() {
    }

    public String getLdmEntityCode() {
        return ldmEntityCode;
    }

    public void setLdmEntityCode(String ldmEntityCode) {
        this.ldmEntityCode = ldmEntityCode;
    }

    public String getLdmAttributeCode() {
        return ldmAttributeCode;
    }

    public void setLdmAttributeCode(String ldmAttributeCode) {
        this.ldmAttributeCode = ldmAttributeCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XmlField xmlField = (XmlField) o;
        return ldmEntityCode.equals(xmlField.ldmEntityCode) && ldmAttributeCode.equals(xmlField.ldmAttributeCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ldmEntityCode, ldmAttributeCode);
    }
}
