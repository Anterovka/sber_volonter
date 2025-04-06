package com.sbt.dataspace.pdm.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class XmlRootModel {

    public static final String MODEL_NAME = "model-name";
    public static final String SHA256 = "sha256";

    private String modelName;
    private String sha256;
    private String data;

    public XmlRootModel() {
    }

    public XmlRootModel(String modelName, String sha256, String data) {
        this.modelName = modelName;
        this.sha256 = sha256;
        this.data = data;
    }

    @JacksonXmlProperty(isAttribute = true, localName = MODEL_NAME)
    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @JacksonXmlProperty(isAttribute = true, localName = SHA256)
    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    @JacksonXmlText
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
