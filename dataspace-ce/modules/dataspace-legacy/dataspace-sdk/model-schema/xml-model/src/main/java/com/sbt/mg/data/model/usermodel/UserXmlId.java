package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.EntityDiff;
import com.sbt.parameters.enums.IdCategory;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserXmlId extends EntityDiff {

    public static final String ID_DESCRIPTION_TAG = "id";

    public static final String TYPE_TAG = "type";
    public static final String CATEGORY_TAG = "category";
    public static final String LENGTH_TAG = "length";

    protected String type;
    protected Integer length;
    protected IdCategory idCategory;

    public UserXmlId() {
    }

    @JsonCreator
    public UserXmlId(@JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG) String type,
                     @JacksonXmlProperty(isAttribute = true, localName = CATEGORY_TAG) IdCategory idCategory,
                     @JacksonXmlProperty(isAttribute = true, localName = LENGTH_TAG) Integer length) {
        this.type = type;
        this.idCategory = idCategory;
        this.length = length;
    }

    @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG)
    public String getType() {
        return type == null ? String.class.getSimpleName() : type;
    }

    @JacksonXmlProperty(isAttribute = true, localName = CATEGORY_TAG)
    public IdCategory getIdCategory() {
        return idCategory;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setIdCategory(IdCategory idCategory) {
        this.idCategory = idCategory;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }
}
