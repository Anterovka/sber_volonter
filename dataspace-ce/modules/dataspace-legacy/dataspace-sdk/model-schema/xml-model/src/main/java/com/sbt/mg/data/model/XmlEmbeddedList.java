package com.sbt.mg.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Assigns physical names to the properties of the embedded field of the given class */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class XmlEmbeddedList {
    public static final String PROPERTY_LISTS_TAG = "property";
    public static final String NAME_TAG = "name";
    public static final String REFERENCE_TAG = "reference";
    public static final String MANDATORY_TAG = "mandatory";

    @JsonIgnore
    XmlModelClass modelClass;

    /** List of names for embeddable properties and their corresponding physical names */
    private List<XmlEmbeddedProperty> embeddedPropertyList = new ArrayList<>();

    /**Field name of an embedded field within a certain class */
    private String name;
    private Boolean reference;
    private Boolean mandatory;
    /** The flag that an element will be deleted in this model release */
    private Boolean isRemoved;

    public XmlEmbeddedList(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                           @JacksonXmlProperty(isAttribute = true, localName = REFERENCE_TAG) Boolean reference,
                           @JacksonXmlProperty(isAttribute = true, localName = MANDATORY_TAG) Boolean mandatory) {
        this.name = name;
        this.reference = reference;
        this.mandatory = mandatory;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = PROPERTY_LISTS_TAG)
    public XmlEmbeddedList setEmbeddedPropertyList(List<XmlEmbeddedProperty> embeddedPropertyList) {
        this.embeddedPropertyList.addAll(embeddedPropertyList);
        return this;
    }

    public XmlEmbeddedList addEmbeddedProperty(XmlEmbeddedProperty embeddedProperty) {
        this.embeddedPropertyList.add(embeddedProperty);
        return this;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = PROPERTY_LISTS_TAG)
    public List<XmlEmbeddedProperty> getEmbeddedPropertyList() {
        return embeddedPropertyList;
    }

    public Optional<XmlEmbeddedProperty> getEmbeddedProperty(String name) {
        return embeddedPropertyList.stream()
                .filter(it -> it.getName().equals(name))
                .findAny();
    }

    @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG)
    public String getName() {
        return name;
    }

    @JacksonXmlProperty(isAttribute = true, localName = REFERENCE_TAG)
    public Boolean isReference() {
        return Boolean.TRUE.equals(reference);
    }

    @JacksonXmlProperty(isAttribute = true, localName = MANDATORY_TAG)
    public Boolean isMandatory() {
        return Boolean.TRUE.equals(mandatory);
    }

    public XmlEmbeddedList setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
        return this;
    }

    public boolean isRemoved() {
        return Boolean.TRUE.equals(isRemoved);
    }

    public XmlEmbeddedList setRemoved(Boolean removed) {
        isRemoved = removed;
        return this;
    }

    @JsonIgnore
    public XmlModelClass getModelClass() {
        return modelClass;
    }

    public void setModelClass(XmlModelClass modelClass) {
        this.modelClass = modelClass;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final XmlEmbeddedList that = (XmlEmbeddedList) obj;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
