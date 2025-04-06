package com.sbt.mg.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.mg.data.model.usermodel.UserXmlQuerySql;
import com.sbt.mg.exception.checkmodel.UserQueryUnknownDialectException;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@XmlTagName(UserXmlQuerySql.TAG)
public class XmlQuerySql extends UserXmlQuerySql {

    @JsonIgnore
    private XmlQuery xmlQuery;

    /** Parsed dbms collection, populated when getDmbs() is called */
    private Set<XmlQueryDBMS> dbmsList;

    public XmlQuerySql() {}

    public XmlQuerySql(@JacksonXmlProperty(isAttribute = true, localName = DBMS_TAG) String dbms) {
        super(dbms);
    }

    @Override
    public void setDbmsString(String dbms) {
        this.dbms = dbms;
        dbmsList = null;
    }

    public Set<XmlQueryDBMS> getDbms() {
        if (dbmsList != null) {
            return dbmsList;
        }

        dbmsList = new HashSet<>();
        if (StringUtils.isEmpty(dbms)) {
            return Collections.singleton(XmlQueryDBMS.ANY);
        } else {
            String[] split = dbms.split(",");
            for (String dbmsItem : split) {
                try {
                    XmlQueryDBMS xmlQueryDBMS = XmlQueryDBMS.valueOf(dbmsItem.trim().toUpperCase());
                    dbmsList.add(xmlQueryDBMS);
                } catch (IllegalArgumentException ex) {
                    throw new UserQueryUnknownDialectException(xmlQuery.getName(), dbmsItem);
                }
            }
        }
        return dbmsList;
    }

    public XmlQuery getXmlQuery() {
        return xmlQuery;
    }

    public XmlQuerySql setXmlQuery(XmlQuery xmlQuery) {
        this.xmlQuery = xmlQuery;
        return this;
    }

    public String getValue() {
        return value;
    }

    public XmlQuerySql setValue(String value) {
        this.value = value;
        return this;
    }

    @JsonIgnore
    public XmlQuerySql copy() {
        return new XmlQuerySql(dbms).setValue(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        XmlQuerySql that = (XmlQuerySql) o;
        return Objects.equals(dbms, that.dbms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dbms);
    }
}
