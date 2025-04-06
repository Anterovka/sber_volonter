package com.sbt.mg.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.mg.data.model.usermodel.UserXmlIndex;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@XmlTagName(XmlModelClass.INDEX_TAG)
public class XmlIndex extends UserXmlIndex<Property> {

    public static final String DEPRECATED_TAG = "isDeprecated";
    public static final String PRIMARY_KEY_TAG = "primary-key";

    private boolean primaryKey;
    private boolean deprecated;
    private boolean deprecatedWithClass;

    @JsonIgnore
    private XmlModelClass modelClass;
    // If true - the index is built from the attribute "unique" or "index" on the field (XmlModelClassProperty)
    @JsonIgnore
    private boolean fromField;
    /* Sign that the name is set by the user on the model and cannot be changed */
    @JsonIgnore
    private boolean isManualName;

    @JsonCreator
    public XmlIndex(@JacksonXmlProperty(isAttribute = true, localName = UNIQUE_TAG) Boolean unique,
                    @JacksonXmlProperty(isAttribute = true, localName = INDEX_NAME_TAG) String indexName,
                    @JacksonXmlProperty(isAttribute = true, localName = DEPRECATED_TAG) Boolean deprecated,
                    @JacksonXmlProperty(isAttribute = true, localName = PRIMARY_KEY_TAG) Boolean primaryKey) {
        super(unique, indexName);
        this.deprecated = Boolean.TRUE.equals(deprecated);
        this.primaryKey = Boolean.TRUE.equals(primaryKey);
    }

    public XmlIndex() {

    }

    /** Creating a "clean" index */
    public static XmlIndex create() {
        return new XmlIndex();
    }

    @Override
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = PROPERTY_TAG)
    public List<Property> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    /**
     * Adds a property to the index information
     * @param property - the added property
     */
    public XmlIndex addProperty(Property property) {
        property.setXmlIndex(this);
        properties.add(property);
        return this;
    }

    /**
     * Removes the property from the index information
     * @param property - deleted property
     */
    public void removeProperty(Property property) {
        properties.remove(property);
    }

    public void setDeprecated(boolean isDeprecated) {
        this.deprecated = isDeprecated;
    }

    public void setDeprecated() {
        this.deprecated = true;
    }

    @JsonIgnore
    public XmlIndex setUnique(boolean unique) {
        this.unique = unique;
        return this;
    }

    @Override
    @JacksonXmlProperty(isAttribute = true, localName = INDEX_NAME_TAG)
    public String getIndexName() {
        return primaryKey ? modelClass.getPkIndexName() : indexName;
    }

    @Override
    public void setIndexName(String indexName) {
        indexName = StringUtils.upperCase(indexName);
        if (primaryKey) {
            modelClass.setPkIndexName(indexName);
        } else {
            this.indexName = indexName;
        }
    }

    /**
     * Set model class and take it
     */
    public XmlIndex setModelClass(XmlModelClass xmlModelClass){
        this.modelClass = xmlModelClass;
        return this;
    }
    /**
     * Get model class
     */
    @JsonIgnore
    public XmlModelClass getModelClass() { return modelClass; }


    @JacksonXmlProperty(isAttribute = true, localName = DEPRECATED_TAG)
    public boolean isDeprecated() {
        return deprecated;
    }

    @JacksonXmlProperty(isAttribute = true, localName = PRIMARY_KEY_TAG)
    public boolean isPrimaryKey() {
        return primaryKey;
    }

    @JsonIgnore
    public void setPrimaryKey() {
        this.primaryKey = Boolean.TRUE;
    }

    public XmlIndex setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
        return this;
    }

    @JsonIgnore
    public boolean isDeprecatedWithClass() {
        return deprecatedWithClass;
    }

    @JsonIgnore
    public void setDeprecatedWithClass(boolean deprecatedWithClass) {
        this.deprecatedWithClass = deprecatedWithClass;
    }

    public boolean isFromField() {
        return fromField;
    }

    public XmlIndex setFromField(boolean fromField) {
        this.fromField = fromField;
        return this;
    }

    public boolean isManualName() {
        return Boolean.TRUE.equals(isManualName);
    }

    public void setManualName(boolean manualName) {
        isManualName = manualName;
    }

    @Override
    public String toString() {
        return "XmlIndex{"
                + "indexName='" + getIndexName() + "',"
                + "className='" + (modelClass == null ? "null" : modelClass.getName()) + "'"
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XmlIndex xmlIndex = (XmlIndex) o;
        return primaryKey == xmlIndex.primaryKey
                && unique == xmlIndex.unique
                && Objects.equals(properties, xmlIndex.properties)
                // the get-method is applied, since for primary indexes the name is taken from the class field
                && Objects.equals(getIndexName(), xmlIndex.getIndexName())
                && Objects.equals(modelClass, xmlIndex.modelClass);
    }

    public boolean equalsIgnoreName(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XmlIndex xmlIndex = (XmlIndex) o;
        return primaryKey == xmlIndex.primaryKey
                && unique == xmlIndex.unique
                && Objects.equals(properties, xmlIndex.properties)
                && Objects.equals(modelClass, xmlIndex.modelClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties, primaryKey, unique, indexName, modelClass);
    }
}
