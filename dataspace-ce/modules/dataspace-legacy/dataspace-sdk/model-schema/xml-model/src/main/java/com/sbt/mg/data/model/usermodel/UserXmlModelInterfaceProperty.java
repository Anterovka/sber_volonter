package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.CollectionType;
import com.sbt.mg.data.model.EntityDiff;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.mg.data.model.usermodel.interfaces.UserPropertyFromXml;

import javax.annotation.Nonnull;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@XmlTagName(UserXmlModelInterface.PROPERTY_TAG)
public class UserXmlModelInterfaceProperty extends EntityDiff implements UserPropertyFromXml {

    public static final String NAME_TAG = XmlModelClassProperty.NAME_TAG;
    public static final String TYPE_TAG = XmlModelClassProperty.TYPE_TAG;
    public static final String COLLECTION_TAG = XmlModelClassProperty.COLLECTION_TAG;

    protected String name;
    protected String type;
    protected CollectionType collectionType;

    @JsonCreator
    public UserXmlModelInterfaceProperty(@Nonnull @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                                         @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG) String type,
                                         @JacksonXmlProperty(isAttribute = true, localName = COLLECTION_TAG) String collection) {
        this.name = name;
        this.type = type;
        this.collectionType = collection != null ? CollectionType.valueOf(collection.toUpperCase()) : null;
    }

    public UserXmlModelInterfaceProperty() {
    }

    @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG)
    public String getName() {
        return name;
    }

    public UserXmlModelInterfaceProperty setName(String name) {
        this.name = name;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG)
    public String getType() {
        return type;
    }

    public UserXmlModelInterfaceProperty setType(String type) {
        this.type = type;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = COLLECTION_TAG)
    public CollectionType getCollectionType() {
        return collectionType;
    }

    public UserXmlModelInterfaceProperty setCollectionType(CollectionType collectionType) {
        this.collectionType = collectionType;
        return this;
    }
}
