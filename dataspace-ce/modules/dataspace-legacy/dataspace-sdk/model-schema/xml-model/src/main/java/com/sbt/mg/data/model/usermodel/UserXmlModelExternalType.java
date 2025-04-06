package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class UserXmlModelExternalType {

    // external-type type="Client" merge-kind="organization|individual|clientRb"

    public static final String TYPE_TAG = "type";
    public static final String MERGE_KIND_TAG = "merge-kind";

    protected String type;
    protected String mergeKind;

    public UserXmlModelExternalType(
            @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG) String type,
            @JacksonXmlProperty(isAttribute = true, localName = MERGE_KIND_TAG) String mergeKind
    ) {
        this.type = type;
        this.mergeKind = mergeKind;
    }

    @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JacksonXmlProperty(isAttribute = true, localName = MERGE_KIND_TAG)
    public String getMergeKind() {
        return mergeKind;
    }

    public void setMergeKind(String mergeKind) {
        this.mergeKind = mergeKind;
    }

}
