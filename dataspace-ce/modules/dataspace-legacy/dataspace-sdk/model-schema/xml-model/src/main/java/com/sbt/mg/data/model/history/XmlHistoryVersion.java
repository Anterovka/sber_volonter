package com.sbt.mg.data.model.history;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Entity history class
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@XmlTagName(XmlHistoryVersion.HISTORY_VERSION_TAG)
public class XmlHistoryVersion {

    public static final String HISTORY_VERSION_TAG = "history-version";

    public static final String NAME_TAG = "name";
    public static final String ID_TAG = "id";
    public static final String VERSION_TAG = "version";
    public static final String IS_LAST_TAG = "isLast";

    private String name;
    private String id;
    private Long version;
    private List<XmlHistoryProperty> xmlHistoryProperties;
    private Boolean isLast; // Change between last and second-to-last versions of the model

    @JsonCreator
    public XmlHistoryVersion(@JacksonXmlProperty(localName = NAME_TAG) String name,
                             @JacksonXmlProperty(localName = ID_TAG) String id,
                             @JacksonXmlProperty(localName = VERSION_TAG) Long version,
                             @JacksonXmlProperty(localName = XmlHistoryProperty.PROPERTY_TAG) List<XmlHistoryProperty> xmlHistoryProperties,
                             @JacksonXmlProperty(localName = IS_LAST_TAG) Boolean isLast) {
        this.name = name;
        this.id = id;
        this.version = version;
        this.xmlHistoryProperties = xmlHistoryProperties;
        this.isLast = isLast;
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

    @JacksonXmlProperty(isAttribute = true, localName = VERSION_TAG)
    public Long getVersion() {
        return version;
    }

    public void setVersion(@Nonnull Long version) {
        this.version = version;
    }

    @JacksonXmlProperty(isAttribute = true, localName = XmlHistoryProperty.PROPERTY_TAG)
    public List<XmlHistoryProperty> getXmlHistoryProperties() {
        return xmlHistoryProperties;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = XmlHistoryProperty.PROPERTY_TAG)
    public void setXmlHistoryProperties(List<XmlHistoryProperty> xmlHistoryProperties) {
        this.xmlHistoryProperties = xmlHistoryProperties;
    }

    @JacksonXmlProperty(isAttribute = true, localName = IS_LAST_TAG)
    public Boolean isLast() {
        return isLast;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = IS_LAST_TAG)
    public void setLast(Boolean last) {
        isLast = last;
    }
}
