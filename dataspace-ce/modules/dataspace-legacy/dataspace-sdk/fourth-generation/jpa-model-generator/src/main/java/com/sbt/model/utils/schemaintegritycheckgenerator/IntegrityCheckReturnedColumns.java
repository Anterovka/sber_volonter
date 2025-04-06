package com.sbt.model.utils.schemaintegritycheckgenerator;

public class IntegrityCheckReturnedColumns {
    private final String checkTime = "CURRENT_TIMESTAMP";
    private String entityType;
    private String id;
    private String table1;
    private String column1;
    private String value1;
    private String table2;
    private String column2;
    private String value2;
    private String checkType;
    private String errorCode;
    private String errorDetails;


    public IntegrityCheckReturnedColumns(String entityType, String id, String table1, String column1, String value1, String table2, String column2, String value2, String checkType, String errorCode, String errorDetails) {
        this.entityType = entityType;
        this.id = id;
        this.table1 = table1;
        this.column1 = column1;
        this.value1 = value1;
        this.table2 = table2;
        this.column2 = column2;
        this.value2 = value2;
        this.checkType = checkType;
        this.errorCode = errorCode;
        this.errorDetails = errorDetails;
    }

    public String getCheckTime() {
        return checkTime;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getId() {
        return id;
    }

    public String getTable1() {
        return table1;
    }

    public String getColumn1() {
        return column1;
    }

    public String getValue1() {
        return value1;
    }

    public String getTable2() {
        return table2;
    }

    public String getColumn2() {
        return column2;
    }

    public String getValue2() {
        return value2;
    }

    public String getCheckType() {
        return checkType;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorDetails() {
        return errorDetails;
    }


    @Override
    public String toString() {
        return String.format("%1$s as checktime, '%2$s' as entitytype, t1.%3$s as id, '%4$s' as table1, '%5$s' as column1, %6$s as value1, " +
                        "'%7$s' as table2, '%8$s' as column2, %9$s as value2, '%10$s' as checktype, '%11$s' as errorcode, '%12$s' as errordetails",
                checkTime, entityType, id, table1, column1, value1, table2, column2, value2, checkType, errorCode, errorDetails);
    }

}
