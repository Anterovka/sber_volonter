package com.sbt.mg.data.model.unusedschemaItems;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;

@XmlTagName(XmlUnusedSchemaItems.UNUSED_TABLE_TAG)
public class XmlUnusedTable {

    public static final String NAME_TAG = "name";
    public static final String DELETED_IN_VERSION_TAG = "deletedInVersion";

    @JsonIgnore
    private XmlUnusedSchemaItems unusedSchemaItems;
    private String name;
    private String deletedInVersion;

    @JsonCreator
    public XmlUnusedTable(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                          @JacksonXmlProperty(isAttribute = true, localName = DELETED_IN_VERSION_TAG) String deletedInVersion) {
        this.name = name;
        this.deletedInVersion = deletedInVersion;
    }

    @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JacksonXmlProperty(isAttribute = true, localName = DELETED_IN_VERSION_TAG)
    public String getDeletedInVersion() {
        return deletedInVersion;
    }

    public void setDeletedInVersion(String deletedInVersion) {
        this.deletedInVersion = deletedInVersion;
    }

    @JsonIgnore
    public XmlUnusedSchemaItems getUnusedSchemaItems() {
        return unusedSchemaItems;
    }

    public void setUnusedSchemaItems(XmlUnusedSchemaItems unusedSchemaItems) {
        this.unusedSchemaItems = unusedSchemaItems;
    }
}
