package com.sbt.mg.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.ReferenceFromXml;
import com.sbt.mg.data.model.interfaces.XmlTagName;

import java.util.Objects;

/**
 * No markup on the model! Forgot to delete.
 */
@XmlTagName(XmlModelClass.REFERENCE_TAG)
public class XmlModelInterfaceReference implements ReferenceFromXml {

    public static final String TYPE_TAG = "type";
    public static final String NAME_TAG = "name";
    public static final String COLLECTION_TAG = "collection";

    @JsonIgnore
    private XmlModelInterface modelInterface;

    private String name;
    private String type;
    private CollectionType collectionType;


    public XmlModelInterfaceReference() {
    }


    @JsonCreator
    public XmlModelInterfaceReference(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                                      @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG) String type,
                                      @JacksonXmlProperty(isAttribute = true, localName = COLLECTION_TAG) String collection) {
        this.name = name;
        this.type = type;

        if (collection != null) {
            this.collectionType = CollectionType.valueOf(collection.toUpperCase());
        }
    }

    /**
     * Get model class
     */

    public XmlModelInterfaceReference setModelInterface(XmlModelInterface modelInterface) {
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
     * Get the title
     */
    @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG)
    public String getName() {
        return name;
    }

    public XmlModelInterfaceReference setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get type
     */
    @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG)
    public String getType() {
        return type;
    }


    public XmlModelInterfaceReference setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Get collection type
     */
    @JacksonXmlProperty(isAttribute = true, localName = COLLECTION_TAG)
    public CollectionType getCollectionType() {
        return collectionType;
    }

    public XmlModelInterfaceReference setCollectionType(CollectionType collectionType) {
        this.collectionType = collectionType;
        return this;
    }

    @Override
    public String toString() {
        return "Reference{"
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
        XmlModelInterfaceReference property = (XmlModelInterfaceReference) o;
        return name.equals(property.name) &&
                modelInterface.equals(property.modelInterface);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, modelInterface.getName());
    }
}
