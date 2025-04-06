package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.EntityDiff;

public class UserXmlQueryId extends EntityDiff {

    public static final String ID_TAG = "id";

    public static final String NAME_TAG = "name";
    public static final String LABEL_TAG = "label";
    public static final String DESCRIPTION_TAG = "description";

    protected final String name;
    protected final String label;
    protected final String description;

    public UserXmlQueryId(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                      @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG) String label,
                      @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG) String description) {
        this.name = name;
        this.label = label == null ? "" : label;
        this.description = description == null ? "" : description;
    }

    @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG)
    public String getName() {
        return name;
    }

    @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG)
    public String getLabel() {
        return label;
    }

    @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG)
    public String getDescription() {
        return description;
    }
}
