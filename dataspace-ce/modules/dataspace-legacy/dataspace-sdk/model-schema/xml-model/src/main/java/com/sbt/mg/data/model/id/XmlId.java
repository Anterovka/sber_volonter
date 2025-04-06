package com.sbt.mg.data.model.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.usermodel.UserXmlId;
import com.sbt.parameters.enums.IdCategory;

import java.util.Objects;

/**
 * Model class
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XmlId extends UserXmlId {

    public static final int DEFAULT_ID_LENGTH = 254;

    public XmlId() {
        /* default constructor */
    }

    public XmlId(@JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG) String type,
                 @JacksonXmlProperty(isAttribute = true, localName = CATEGORY_TAG) IdCategory idCategory) {
        super(type, idCategory, String.class.getSimpleName().equals(type) ? DEFAULT_ID_LENGTH : null);
    }

    @JsonCreator
    public XmlId(@JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG) String type,
                 @JacksonXmlProperty(isAttribute = true, localName = CATEGORY_TAG) IdCategory idCategory,
                 @JacksonXmlProperty(isAttribute = true, localName = LENGTH_TAG) Integer length
                 ) {
        super(type, idCategory, length);
    }

    @Override
    public String toString() {
        return "Id {"
                + "type='" + type + '\''
                + ", idCategory='" + idCategory + '\''
                + '}';
    }

    public static XmlId defaultId(XmlModelClass modelClass, IdCategory defaultAutoIdCategory) {

        return Boolean.TRUE.equals(modelClass.isDictionary()) ?
                new XmlId(String.class.getSimpleName(), IdCategory.MANUAL) :
                new XmlId(String.class.getSimpleName(), defaultAutoIdCategory);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XmlId xmlId = (XmlId) o;
        return Objects.equals(type, xmlId.type) && idCategory == xmlId.idCategory && Objects.equals(length, length);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, idCategory);
    }
}
