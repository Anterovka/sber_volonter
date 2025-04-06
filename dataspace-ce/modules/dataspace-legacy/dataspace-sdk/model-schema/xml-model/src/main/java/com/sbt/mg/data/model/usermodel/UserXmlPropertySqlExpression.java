package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.dataformat.xml.annotation.*;
import com.sbt.mg.Helper;
import com.sbt.mg.data.model.interfaces.XmlTagName;

@XmlTagName(UserXmlPropertySqlExpression.SQL_EXPRESSION)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserXmlPropertySqlExpression {
    public static final String SQL_EXPRESSION = "sql-expr";
    public static final String DBMS = "dbms";

    protected String dbms;
    protected String sqlExpression;

    public UserXmlPropertySqlExpression() {
    }

    @JsonCreator
    public UserXmlPropertySqlExpression(
            @JacksonXmlProperty(isAttribute = true, localName = DBMS) String dbms) {
            this.dbms = dbms;

    }

    @JacksonXmlProperty(isAttribute = true, localName = DBMS)
    public String getDbms() {
        return dbms;
    }

    @JacksonXmlText
    public String getComputedExpression() {
        return sqlExpression;
    }

    @JacksonXmlText
    public void setComputedExpression(String sqlExpression) {
        this.sqlExpression = Helper.beautifierExpression(sqlExpression);
    }
}
