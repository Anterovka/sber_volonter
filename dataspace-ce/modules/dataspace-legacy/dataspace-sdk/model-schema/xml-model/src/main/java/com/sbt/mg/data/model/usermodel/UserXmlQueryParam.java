package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.Helper;
import com.sbt.mg.data.model.EntityDiff;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import org.apache.commons.lang3.StringUtils;

@XmlTagName(UserXmlQueryParam.TAG)
public class UserXmlQueryParam extends EntityDiff {
    public static final String TAG = "param";

    public static final String NAME_TAG = "name";
    public static final String TYPE_TAG = "type";
    public static final String LABEL_TAG = "label";
    public static final String LENGTH_TAG = "length";
    public static final String MASK_TAG = "mask";
    public static final String DESCRIPTION_TAG = "description";
    public static final String COLLECTION_TAG = "collection";
    public static final String DEFAULT_VALUE_TAG = "default-value";

    protected String name;
    protected String type;
    protected String label;
    protected Integer length;
    protected String mask;
    protected String description;
    protected Boolean collection;
    protected String defaultValue;

    public UserXmlQueryParam(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                             @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG) String type,
                             @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG) String label,
                             @JacksonXmlProperty(isAttribute = true, localName = LENGTH_TAG) Integer length,
                             @JacksonXmlProperty(isAttribute = true, localName = MASK_TAG) String mask,
                             @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG) String description,
                             @JacksonXmlProperty(isAttribute = true, localName = COLLECTION_TAG) String collection,
                             @JacksonXmlProperty(isAttribute = true, localName = DEFAULT_VALUE_TAG) String defaultValue) {
        this.name = name;
        this.type = type;
        this.length = length;
        this.mask = mask;
        this.label = label != null ? Helper.replaceIllegalSymbols(label) : null;
        this.description = description != null ? Helper.replaceIllegalSymbols(description) : null;
        this.collection = StringUtils.isEmpty(collection) ? Boolean.FALSE : Boolean.valueOf(collection);
        this.defaultValue = defaultValue;
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

    @JacksonXmlProperty(isAttribute = true, localName = COLLECTION_TAG)
    public Boolean getCollection() {
        return collection;
    }

    public void setCollection(Boolean collection) {
        this.collection = collection;
    }

    @JacksonXmlProperty(isAttribute = true, localName = DEFAULT_VALUE_TAG)
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @JacksonXmlProperty(isAttribute = true, localName = LENGTH_TAG)
    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    @JacksonXmlProperty(isAttribute = true, localName = MASK_TAG)
    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }
}
