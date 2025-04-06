package com.sbt.mg.exception.checkmodel;

/**
 * Exception: The table name is already set in the model
 */
public class TableNameAlreadyDefinedException extends CheckXmlModelException {
    /**
     * @param tableName Table name
     */
    public TableNameAlreadyDefinedException(String tableName) {
        super(join("Table name", tableName, "is already reserved"),
            "Necessary to contact the module developers");
    }
}
