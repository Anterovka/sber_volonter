package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;

/**
 * The entity with the alias
 */
class AliasedEntityData {

    SqlQueryProcessor sqlQueryProcessor;
    EntityDescription entityDescription;
    boolean nullable;

    /**
     * @param sqlQueryProcessor The SQL query handler
     * @param entityDescription Entity description
     */
    AliasedEntityData(SqlQueryProcessor sqlQueryProcessor, EntityDescription entityDescription) {
        this.sqlQueryProcessor = sqlQueryProcessor;
        this.entityDescription = entityDescription;
    }
}
