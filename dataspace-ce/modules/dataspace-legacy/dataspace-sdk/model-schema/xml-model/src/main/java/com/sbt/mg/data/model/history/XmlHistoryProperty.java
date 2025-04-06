package com.sbt.mg.data.model.history;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;

import javax.annotation.Nonnull;

/**
 * Class of history specific to the property
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@XmlTagName(XmlHistoryProperty.PROPERTY_TAG)
public class XmlHistoryProperty {
    public static final String PROPERTY_TAG = "property";
    public static final String NAME_TAG = "name";
    public static final String ID_TAG = "id";

    private String name;
    private String id;

    @JsonCreator
    public XmlHistoryProperty(
            @JacksonXmlProperty(localName = NAME_TAG) String name,
            @JacksonXmlProperty(localName = ID_TAG) String id
    ) {
        this.name = name;
        this.id = id;
    }

    @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG)
    public String getName() {
        return name;
    }

    public void setName(@Nonnull String name) {
        this.name = name;
    }

    @JacksonXmlProperty(isAttribute = true, localName = ID_TAG)
    public String getId() {
        return id;
    }

    public void setId(@Nonnull String id) {
        this.id = id;
    }
}
