package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.EntityDiff;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@XmlTagName(XmlModelClass.INDEX_TAG)
public class UserXmlIndex<T extends UserXmlProperty> extends EntityDiff {

    public static final String UNIQUE_TAG = "unique";
    public static final String INDEX_NAME_TAG = "index-name";
    public static final String PROPERTY_TAG = "property";

    protected boolean unique;
    protected String indexName;
    protected List<T> properties = new ArrayList<>();

    @JsonCreator
    public UserXmlIndex(@JacksonXmlProperty(isAttribute = true, localName = UNIQUE_TAG) Boolean unique,
                        @JacksonXmlProperty(isAttribute = true, localName = INDEX_NAME_TAG) String indexName) {
        this.unique = Boolean.TRUE.equals(unique);
        this.indexName = StringUtils.upperCase(indexName);
    }

    public UserXmlIndex() {
    }

    @JacksonXmlProperty(isAttribute = true, localName = UNIQUE_TAG)
    public boolean isUnique() {
        return unique;
    }

    @JsonIgnore
    public void setUnique() {
        this.unique = Boolean.TRUE;
    }

    @JacksonXmlProperty(isAttribute = true, localName = INDEX_NAME_TAG)
    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = StringUtils.upperCase(indexName);
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = PROPERTY_TAG)
    public List<T> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = PROPERTY_TAG)
    public void setIndexProperties(List<T> properties) {
        this.properties.clear();
        properties.forEach(this::addProperty);
    }

    private void addProperty(T property) {
        properties.add(property);
    }
}
