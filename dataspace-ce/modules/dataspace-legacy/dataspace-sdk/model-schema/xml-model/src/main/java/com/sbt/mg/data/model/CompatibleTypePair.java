package com.sbt.mg.data.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CompatibleTypePair {
    private String sourceType;
    private String targetType;

    public CompatibleTypePair(@JacksonXmlProperty(isAttribute = true, localName = "sourceType") String sourceType,
                              @JacksonXmlProperty(isAttribute = true, localName = "targetType") String targetType) {
        this.sourceType = sourceType;
        this.targetType = targetType;
    }

    public String getSourceType() {
        return sourceType;
    }

    public String getTargetType() {
        return targetType;
    }
}
