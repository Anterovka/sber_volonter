package com.sbt.mg.data.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

/**
 * Information about type
 */
public class TypeInfo {

    @Nonnull
    private List<String> names;
    @Nonnull
    private String hbmName;
    @Nonnull
    private String javaName;
    @Nonnull
    private String oracleName;

    private String oracleType;
    @Nonnull
    private String fullName;

    private String dbType;
    private Integer firstNumber;
    private Integer secondNumber;
    private boolean isCollect;
    private boolean isLength;

    /**
     * @param hbmName    Name for hbm
     * @param javaName   Name for Java
     * @param oracleName Name for Oracle
     */
    public TypeInfo(@Nonnull @JacksonXmlProperty(isAttribute = true, localName = "names") String names,
                    @Nonnull @JacksonXmlProperty(isAttribute = true, localName = "hbmName") String hbmName,
                    @Nonnull @JacksonXmlProperty(isAttribute = true, localName = "javaName") String javaName,
                    @JacksonXmlProperty(isAttribute = true, localName = "oracleName") String oracleName,
                    @JacksonXmlProperty(isAttribute = true, localName = "dbType") String dbType,
                    @Nonnull @JacksonXmlProperty(isAttribute = true, localName = "fullName") String fullName,
                    @JacksonXmlProperty(isAttribute = true, localName = "collect") boolean isCollect,
                    @JacksonXmlProperty(isAttribute = true, localName = "firstNumber") Integer firstNumber,
                    @JacksonXmlProperty(isAttribute = true, localName = "secondNumber") Integer secondNumber,
                    @JacksonXmlProperty(isAttribute = true, localName = "length") boolean length) {
        this.names = Arrays.asList(names.split(","));
        this.hbmName = hbmName;
        this.javaName = javaName;
        this.oracleName = oracleName;
        this.dbType = dbType;
        this.fullName = fullName;
        this.isCollect = isCollect;
        this.firstNumber = firstNumber;
        this.secondNumber = secondNumber;

        if (oracleName == null) {
            return;
        }
        // parse OracleName, in a separate method - half a dozen sonar bends can't make sense of this approach and complain
        int indexOf = oracleName.indexOf('(');
        if (indexOf == -1) {
            this.oracleType = this.oracleName;
        } else {
            this.oracleType = oracleName.substring(0, indexOf);

        }
        this.isLength = length;
    }

    public TypeInfo(TypeInfo typeInfo) {
        this.names = typeInfo.getNames();
        this.hbmName = typeInfo.getHbmName();
        this.javaName = typeInfo.getJavaName();
        this.oracleName = typeInfo.getOracleName();
        this.firstNumber = typeInfo.getFirstNumber();
        this.secondNumber = typeInfo.getSecondNumber();
        this.oracleType = typeInfo.getOracleType();
        this.dbType = typeInfo.getDbType();
        this.fullName = typeInfo.getFullName();
        this.isCollect = typeInfo.isCollect();
        this.isLength = typeInfo.isLength();
    }

    public void setFirstNumber(Integer firstNumber) {
        this.firstNumber = firstNumber;
    }

    public void setSecondNumber(Integer secondNumber) {
        this.secondNumber = secondNumber;
    }

    /**
     * Get name for Hbm
     */
    @Nonnull
    public String getHbmName() {
        return hbmName;
    }

    /**
     * Get the name for Java (shortened - without package)
     */
    @Nonnull
    public String getJavaName() {
        return javaName;
    }

    /**
     * Get the name for Oracle
     */
    public String getOracleName() {
        return oracleType + (firstNumber == null ? "" : "(" + firstNumber + (secondNumber == null ? ")" : "," + secondNumber + ")"));
    }

    @Nonnull
    public List<String> getNames() {
        return names;
    }

    public String getOracleType() {
        return oracleType;
    }

    public String getDbType() {
        return dbType;
    }

    public Integer getFirstNumber() {
        return firstNumber;
    }

    public Integer getSecondNumber() {
        return secondNumber;
    }

    @Nonnull
    public String getFullName() {
        return fullName;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public boolean isLength() {
        return isLength;
    }
}
