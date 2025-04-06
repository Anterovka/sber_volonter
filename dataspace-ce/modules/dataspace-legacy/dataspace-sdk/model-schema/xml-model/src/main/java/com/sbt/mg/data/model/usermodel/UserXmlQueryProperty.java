package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.Helper;
import com.sbt.mg.data.model.EntityDiff;
import com.sbt.mg.data.model.interfaces.XmlTagName;

@XmlTagName(UserXmlQueryProperty.PROPERTY_TAG)
public class UserXmlQueryProperty extends EntityDiff {

    public static final String PROPERTY_TAG = "property";

    public static final String NAME_TAG = "name";
    public static final String TYPE_TAG = "type";
    public static final String LABEL_TAG = "label";
    public static final String DESCRIPTION_TAG = "description";

    protected String name;
    protected String type;
    protected String label;
    protected String description;

    public UserXmlQueryProperty(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                                @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG) String type,
                                @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG) String label,
                                @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG) String description) {
        this.name = name;
        this.type = type;
        this.label = label != null ? Helper.replaceIllegalSymbols(label) : null;
        this.description = description != null ? Helper.replaceIllegalSymbols(description) : null;
    }

    @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG)
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
