package com.sbt.mg.data.model.unusedschemaItems;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.interfaces.XmlTagName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@XmlTagName(XmlModel.UNUSED_SCHEMA_ITEMS)
public class XmlUnusedSchemaItems {
    public static final String UNUSED_TABLE_TAG = "unusedTable";
    public static final String UNUSED_COLUMN_TAG = "unusedColumn";

    @JsonIgnore
    private XmlModel model;
    private List<XmlUnusedTable> unusedTables = new ArrayList<>();
    private List<XmlUnusedColumn> unusedColumns = new ArrayList<>();


    public XmlUnusedSchemaItems() {
    }

    public XmlUnusedSchemaItems(XmlModel xmlModel) {
        this.model = xmlModel;
        xmlModel.setUnusedSchemaItems(this);//?
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = UNUSED_TABLE_TAG)
    public List<XmlUnusedTable> getUnusedTables() {
        return unusedTables;
    }

    public XmlUnusedSchemaItems setUnusedTables(List<XmlUnusedTable> unusedTables) {
        if (Objects.isNull(unusedTables)) {
            this.unusedTables = new ArrayList<>();
        } else {
            this.unusedTables.clear();
            unusedTables.forEach(this::addUnusedTable);
        }
        return this;
    }

    public XmlUnusedSchemaItems addUnusedTable(XmlUnusedTable unusedTable) {
        unusedTable.setUnusedSchemaItems(this);
        unusedTables.add(unusedTable);
        return this;
    }

    public void removeUnusedTable(XmlUnusedTable unusedTable) {
        unusedTables.remove(unusedTable);
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = UNUSED_COLUMN_TAG)
    public List<XmlUnusedColumn> getUnusedColumns() {
        return unusedColumns;
    }

    public XmlUnusedSchemaItems setUnusedColumns(List<XmlUnusedColumn> unusedColumns) {
        if (Objects.isNull(unusedColumns)) {
            this.unusedColumns = new ArrayList<>();
        } else {
            this.unusedColumns.clear();
            unusedColumns.forEach(this::addUnusedColumn);
        }
        return this;
    }

    public XmlUnusedSchemaItems addUnusedColumn(XmlUnusedColumn unusedColumn) {
        unusedColumn.setUnusedSchemaItems(this);
        unusedColumns.add(unusedColumn);
        return this;
    }

    public void removeUnusedColumn(XmlUnusedColumn unusedColumn) {
        unusedColumns.remove(unusedColumn);
    }

    @JsonIgnore
    public XmlModel getModel() {
        return model;
    }

    public void setModel(XmlModel model) {
        this.model = model;
        model.setUnusedSchemaItems(this);
    }

    public boolean containsTable(String nameTable) {
        return unusedTables.stream().anyMatch(unusedTable -> unusedTable.getName().equals(nameTable));
    }

    public boolean containsColumn(String tableName, String columnName) {
        return unusedColumns.stream().anyMatch(unusedColumn -> unusedColumn.getName().equals(columnName) && unusedColumn.getTableName().equals(tableName));
    }

}
