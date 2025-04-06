package com.sbt.mg.exception.checkmodel;

/**
 * Exception: The table name is already set in the model
 */
public class IndexNameAlreadyDefinedException extends CheckXmlModelException {
    /**
     * @param name The name of the index
     */
    public IndexNameAlreadyDefinedException(String name) {
        super(join("Index Name", name, "is already reserved"),
            "Necessary to contact the module developers");
    }
}
