package com.sbt.mg.exception.checkmodel;

/**
 * Exception: The column name is already set in the model class
 */
public class ColumnNameAlreadyDefinedException extends CheckXmlModelException {
    /**
     * @param className  Class name
     * @param columnName Column name
     */
    public ColumnNameAlreadyDefinedException(String className, String columnName) {
        super(join("Column name", columnName, "is already set in the model class", className),
            "To solve the problem, it is necessary to contact the module developer.");
    }
}
