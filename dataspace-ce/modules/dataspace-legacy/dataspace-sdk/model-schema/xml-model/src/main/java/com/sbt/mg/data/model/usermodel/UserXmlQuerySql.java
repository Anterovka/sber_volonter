package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import com.sbt.mg.data.model.EntityDiff;

public class UserXmlQuerySql extends EntityDiff {

    public static final String TAG = "sql";

    public static final String DBMS_TAG = "dbms";

    protected String dbms;

    @JacksonXmlText
    protected String value;

    public UserXmlQuerySql() {
        this.dbms = null;
    }

    public UserXmlQuerySql(@JacksonXmlProperty(isAttribute = true, localName = DBMS_TAG) String dbms) {
        this.dbms = dbms;
    }

    @JacksonXmlProperty(isAttribute = true, localName = DBMS_TAG)
    public String getDbmsString() {
        return dbms;
    }

    public void setDbmsString(String dbms) {
        this.dbms = dbms;
    }
}
