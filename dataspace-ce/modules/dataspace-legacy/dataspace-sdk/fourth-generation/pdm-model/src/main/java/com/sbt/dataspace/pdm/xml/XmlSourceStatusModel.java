package com.sbt.dataspace.pdm.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class XmlSourceStatusModel {
    private String data;

    public XmlSourceStatusModel() {
    }

    public XmlSourceStatusModel(String data) {
        this.data = data;
    }

    @JacksonXmlText
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
