package com.sbt.mg.utils;

import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.TypeInfo;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.parameters.enums.DBMS;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

import static com.sbt.mg.jpa.JpaConstants.ORACLE_VALUE_RESTRICTION;

public final class LiquibasePropertyUtils {

    private LiquibasePropertyUtils() {

    }

    private static String computeDefaultValueCommon(XmlModelClassProperty property,
                                                    String value,
                                                    DBMS dbms) {
        if (value == null) {
            return "";
        }
        TypeInfo typeInfo = property.getTypeInfo();
        String result;

        switch (typeInfo.getJavaName()) {
            case "Boolean":
                result = computeBooleanDefaultValue(value);
                break;
            case "BigDecimal":
            case "Integer":
            case "Short":
            case "Long":
            case "Byte":
            case "Float":
                result = "defaultValueNumeric=\"" + value + '\"';
                break;
            case "Double":
                result = "defaultValueComputed=\"" + value + '\"';
                break;
            case "Date":
            case "OffsetDateTime":
            case "LocalDateTime":
            case "LocalDate":
                if ("now".equalsIgnoreCase(value)) {
                    result = "defaultValueComputed=\"${systimestamp}\"";
                } else {
                    result = "defaultValueDate=\"" + value + '\"';
                }
                break;
            case "byte[]":
                switch (dbms) {
                    case H2:
                        result = String.format("defaultValueComputed=\"%s\"", h2Base64DecodeFunction(value));
                        break;
                    case ORACLE:
                        result = String.format("defaultValueComputed=\"%s\"", oracleBase64DecodeFunction(value));
                        break;
                    case POSTGRES:
                        result = String.format("defaultValueComputed=\"%s\"", postgresBase64DecodeFunction(value));
                        break;
                    default:
                        result = String.format("defaultValueComputed=\"%s\"", value);
                }
                break;
            default:
                result = "defaultValue=\"" + replaceEscapeSymbols(value) + '\"';
        }

        if (ModelHelper.isClob(property) && dbms == DBMS.ORACLE && value.length() > ORACLE_VALUE_RESTRICTION) {
            result = "defaultValueComputed=\"" + replaceEscapeSymbols(value) + "\"";
        }

        return result;
    }

    public static String h2Base64DecodeFunction(String value) {
        return String.format("BASE64_DECODE('%s')", value);
    }

    public static String oracleBase64DecodeFunction(String value) {
        return String.format("utl_encode.BASE64_DECODE(utl_raw.cast_to_raw('%s'))", value);
    }

    public static String postgresBase64DecodeFunction(String value) {
        return String.format("decode('%s', 'base64')", value);
    }

    private static String computeValueCommon(XmlModelClassProperty property,
                                             String value,
                                             DBMS dbms) {
        if (value == null) {
            return "";
        }
        TypeInfo typeInfo = property.getTypeInfo();
        String result;
        switch (typeInfo.getJavaName()) {
            case "Boolean":
                result = "valueBoolean=\"" + value + '\"';
                break;
            case "BigDecimal":
            case "Integer":
            case "Short":
            case "Long":
            case "Byte":
            case "Float":
            case "Double":
                result = "valueNumeric=\"" + value + '\"';
                break;
            case "Date":
            case "OffsetDateTime":
            case "LocalDateTime":
            case "LocalDate":
                if ("now".equalsIgnoreCase(value)) {
                    result = "valueComputed=\"${systimestamp}\"";
                } else {
                    result = "valueDate=\"" + value + '\"';
                }
                break;
            case "byte[]":
                    switch (dbms) {
                        case H2:
                            result = String.format("valueComputed=\"BASE64_DECODE('%s')\"", value);
                            break;
                        case ORACLE:
                            result = String.format("valueComputed=\"utl_encode.BASE64_DECODE(utl_raw.cast_to_raw('%s'))\"", value);
                            break;
                        case POSTGRES:
                            result = String.format("valueComputed=\"decode('%s', 'base64')\"", value);
                            break;
                        default:
                            result = String.format("valueComputed=\"%s\"", value);
                    }
                break;
            default:
                result = "value=\"" + replaceEscapeSymbols(value) + '\"';
        }

        if (ModelHelper.isClob(property) && dbms == DBMS.ORACLE && value.length() > ORACLE_VALUE_RESTRICTION) {
            result = "valueComputed=\"" + replaceEscapeSymbols(value) + "\"";
        }

        return result;
    }

    private static String computeBooleanDefaultValue(String value) {
        return "defaultValueBoolean=\"" + value.toLowerCase(Locale.ENGLISH) + '\"';
    }

    public static String replaceEscapeSymbols(String value) {

        return value.replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("'", "&apos;")
                .replace("\r", "&#xD;")
                .replace("\n", "&#xA;");
    }

    public static String computeDefaultValue(XmlModelClassProperty property) {
        return computeDefaultValue(property, DBMS.ANY);
    }

    public static String computeDefaultValue(XmlModelClassProperty property, DBMS dbms) {
        String defaultValue = property.getDefaultValue();
        if (StringUtils.isBlank(defaultValue)) {
            return "";
        }
        return computeDefaultValueCommon(property, defaultValue, dbms);
    }

    public static String computeOldDefaultValue(XmlModelClassProperty property, DBMS dbms) {
        String defaultValue = property.getOldValueChangedProperty(XmlModelClassProperty.DEFAULT_VALUE_TAG);
        if (StringUtils.isBlank(defaultValue)) {
            return "";
        }
        return computeDefaultValueCommon(property, defaultValue, dbms);
    }

    public static String computeValue(XmlModelClassProperty property, Object value) {
        return computeValueCommon(property, value == null ? null : value.toString(), DBMS.ANY);
    }

    public static String computeValue(XmlModelClassProperty property, Object value, DBMS dbms) {
        return computeValueCommon(property, value == null ? null : value.toString(), dbms);
    }
}
