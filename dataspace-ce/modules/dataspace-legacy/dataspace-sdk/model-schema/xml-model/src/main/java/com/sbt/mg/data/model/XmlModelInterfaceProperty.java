package com.sbt.mg.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.PropertyFromXml;
import com.sbt.mg.data.model.interfaces.XmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.mg.data.model.usermodel.UserXmlModelInterface;
import com.sbt.mg.data.model.usermodel.UserXmlModelInterfaceProperty;

import javax.annotation.Nonnull;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@XmlTagName(UserXmlModelInterface.PROPERTY_TAG)
public class XmlModelInterfaceProperty
        extends UserXmlModelInterfaceProperty
        implements PropertyFromXml, XmlProperty<XmlModelInterface> {

    public static final String ENUM_TAG = XmlModelClassProperty.ENUM_TAG;
    public static final String EXTERNAL_LINK_TAG = XmlModelClassProperty.EXTERNAL_LINK_TAG;


    /** Interface of the model to which the property belongs */
    @JsonIgnore
    private XmlModelInterface modelInterface;

    @JsonIgnore
    private TypeInfo typeInfo;

    private boolean isEnum;
    private Boolean externalLink;


    public XmlModelInterfaceProperty() {
    }

    @JsonCreator
    public XmlModelInterfaceProperty(@Nonnull @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                                     @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG) String type,
                                     @JacksonXmlProperty(isAttribute = true, localName = COLLECTION_TAG) String collection,
                                     @JacksonXmlProperty(isAttribute = true, localName = EXTERNAL_LINK_TAG) Boolean externalLink,
                                     @JacksonXmlProperty(isAttribute = true, localName = ENUM_TAG) Boolean isEnum) {

        super(name, type, collection);
        this.externalLink = externalLink;
        this.isEnum = Boolean.TRUE.equals(isEnum);
    }

    /**
     * Get model interface
     */
    @JsonIgnore
    public XmlModelInterfaceProperty setModelInterface(XmlModelInterface modelInterface) {
        this.modelInterface = modelInterface;
        return this;
    }

    /**
     * Get model interface
     */
    @JsonIgnore
    public XmlModelInterface getModelInterface() {
        return modelInterface;
    }

    /**
     * Method duplicates getModelClass().
     * Introduced to support a common interface
     */
    @JsonIgnore
    public XmlModelInterface getParent() {
        return modelInterface;
    }

    @JsonIgnore
    public TypeInfo getTypeInfo() {
        return typeInfo;
    }

    public XmlModelInterfaceProperty setTypeInfo(TypeInfo typeInfo) {
        this.typeInfo = new TypeInfo(typeInfo);
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = ENUM_TAG)
    public boolean isEnum() {
        return isEnum;
    }

    public XmlModelInterfaceProperty setEnum(Boolean isEnum) {
        this.isEnum = Boolean.TRUE == isEnum;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = EXTERNAL_LINK_TAG)
    public Boolean isExternalLink() {
        return Boolean.TRUE.equals(externalLink);
    }

    public XmlModelInterfaceProperty setExternalLink(Boolean externalLink) {
        this.externalLink = externalLink;
        return this;
    }

    @Override
    public XmlModelInterfaceProperty setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public XmlModelInterfaceProperty setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public XmlModelInterfaceProperty setCollectionType(CollectionType collectionType) {
        this.collectionType = collectionType;
        return this;
    }

    @Override
    public String toString() {
        return "Property{"
                + "name='" + name + '\''
                + " in class = '" + (modelInterface == null ? "not yet defined " : modelInterface.getName()) + '\''
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        XmlModelInterfaceProperty property = (XmlModelInterfaceProperty) o;
        return name.equals(property.name) &&
                modelInterface.equals(property.modelInterface);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, modelInterface.getName());
    }
}
