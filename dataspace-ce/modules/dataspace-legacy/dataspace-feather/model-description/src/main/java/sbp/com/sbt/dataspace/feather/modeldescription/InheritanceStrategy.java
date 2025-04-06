package sbp.com.sbt.dataspace.feather.modeldescription;

/**
 * Strategy of inheritance
 */
public enum InheritanceStrategy {

    /**
     * Each type corresponds to one table.<br/>
     * To get all the properties of an entity, it is necessary to combine all tables in the inheritance chain
     */
    JOINED,
    /**
     * For all types within the type hierarchy, there is a single table that corresponds to them.
     */
    SINGLE_TABLE
}
