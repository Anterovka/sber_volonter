package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserXmlEvent<T extends UserXmlModelClassProperty, V extends UserXmlIndex> {

    public static final String EVENT_TAG = "event";

    public static final String PROPERTY_TAG = "property";
    public static final String INDEX_TAG = "index";
    public static final String NAME_TAG = "name";
    public static final String LABEL_TAG = "label";
    public static final String DESCRIPTION_TAG = "description";
    public static final String MERGE_EVENT_TAG = "merge-event";
    public static final String DEPRECATED_TAG = "isDeprecated";

    protected String name;
    protected String label;
    protected String description;
    protected Boolean mergeEvent;
    protected Boolean isDeprecated;

    @JsonIgnore
    protected final List<T> properties = new ArrayList<>();

    @JsonIgnore
    protected final List<V> indices = new ArrayList<>();

    public UserXmlEvent() {
    }

    @JsonCreator
    public UserXmlEvent(
            @JacksonXmlProperty(isAttribute = true, localName = MERGE_EVENT_TAG) Boolean mergeEvent
    ) {
        this.mergeEvent = mergeEvent;
    }

    @JacksonXmlProperty(isAttribute = true, localName = MERGE_EVENT_TAG)
    public Boolean getMergeEvent() {
        return mergeEvent;
    }

    public void setMergeEvent(boolean mergeEvent) {
        this.mergeEvent = mergeEvent;
    }

    public void setDeprecated(Boolean isDeprecated) {
        this.isDeprecated = isDeprecated;
    }

    @JacksonXmlProperty(isAttribute = true, localName = DEPRECATED_TAG)
    public Boolean isDeprecated() {
        return isDeprecated;
    }

    @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG)
    public String getLabel() {
        return label;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG)
    public String getDescription() {
        return description;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = PROPERTY_TAG)
    public List<T> getPropertiesAsList() {
        return new ArrayList<>(properties);
    }

    @JsonSetter(value = PROPERTY_TAG)
    public void addProperties(Collection<T> properties) {
        this.properties.addAll(properties);
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonSetter(value = INDEX_TAG)
    public void addIndices(List<V> indices) {
        this.indices.addAll(indices);
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = INDEX_TAG)
    public List<V> getIndices() {
        return new ArrayList<>(indices);
    }
}
