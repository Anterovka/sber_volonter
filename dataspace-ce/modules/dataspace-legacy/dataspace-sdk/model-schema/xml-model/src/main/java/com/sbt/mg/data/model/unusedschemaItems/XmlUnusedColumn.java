package com.sbt.mg.data.model.unusedschemaItems;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;

@XmlTagName(XmlUnusedSchemaItems.UNUSED_COLUMN_TAG)
public class XmlUnusedColumn {

    public static final String NAME_TAG = "name";
    public static final String TABLE_NAME_TAG = "tableName";
    public static final String DELETED_IN_VERSION_TAG = "deletedInVersion";

    @JsonIgnore
    private XmlUnusedSchemaItems unusedSchemaItems;
    private String name;
    private String tableName;
    private String deletedInVersion;

    @JsonCreator
    public XmlUnusedColumn(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                           @JacksonXmlProperty(isAttribute = true, localName = TABLE_NAME_TAG) String tableName,
                           @JacksonXmlProperty(isAttribute = true, localName = DELETED_IN_VERSION_TAG) String deletedInVersion) {
        this.name = name;
        this.tableName = tableName;
        this.deletedInVersion = deletedInVersion;
    }

    @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JacksonXmlProperty(isAttribute = true, localName = TABLE_NAME_TAG)
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
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
