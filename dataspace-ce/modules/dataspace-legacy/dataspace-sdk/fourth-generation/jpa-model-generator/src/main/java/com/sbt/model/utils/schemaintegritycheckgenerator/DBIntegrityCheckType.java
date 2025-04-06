package com.sbt.model.utils.schemaintegritycheckgenerator;

enum DBIntegrityCheckType {

    PNUL("PNUL", "parent link is null", "PARENT", "the column with the parent reference is not filled", "select %1$s from %2$s t1 where t1.%3$s is null"),
    PMIS("PMIS", "parent class record is missing", "PARENT", "a row is missing in the parent table of aggregate %s", "select %1$s from %2$s t1 left join %4$s t2 on t1.%3$s = t2.object_id where t2.object_id is null"),
    ANUL("ANUL", "the aggregate link is null", "AGGREGATE_ROOT", "the column containing the reference to the aggregate root is not filled", "select %1$s from %2$s t1 where t1.aggregateroot_id is null"),
    AMIS("AMIS", "aggregate root record in missing", "AGGREGATE_ROOT", "the string is missing in the aggregate root table %s", "select %1$s from %2$s t1 left join %3$s t2 on t1.aggregateroot_id = t2.object_id where t2.object_id is null"),
    OMIS("OMIS", "the owner record is missing", "COLLECTION_OWNER", "the string is missing in the collection owner table %s", "select %1$s from %2$s t1 left join %4$s t2 on t1.%3$s = t2.object_id where t2.object_id is null"),
    JANC("JANC", "joined ancestor record does not exist", "JOINED_STRATEGY", "the string is missing in the parent table %s", "select %1$s from %2$s t1 left join %3$s t2 on t1.object_id = t2.object_id where t2.object_id is null"),
    JDES("JDES", "joined descendant record does not exist", "JOINED_STRATEGY", "the string is missing in the child table %s", "select %1$s from %2$s t1 left join %3$s t2 on t1.object_id= t2.object_id where t2.object_id is null and t1.type in (%4$s)"),
    FMIS("FMIS", "FK is missing", "AGGREGATE_FK", "for property reference there is no string in table %s", "select %1$s from %2$s t1 left join %4$s t2 on t1.%3$s = t2.object_id where t2.object_id is null and t1.%3$s is not null"),
    FABV("FABV", "aggregate boundary violation", "AGGREGATE_FK", "property-ссылка %1$s ссылается на сущность '||%2$s||' another aggregate %3$s", "select %1$s from %2$s t1 inner join %4$s t2 on t1.%3$s = t2.object_id where t2.%5$s <> t1.%6$s"),
    RMIS("RMIS", "reference is missing", "REFERENCE_FK", "for reference-link a line in table %s is missing", "select %1$s from %2$s t1 left join %4$s t2 on t1.%3$s = t2.object_id where t2.object_id is null and t1.%3$s is not null"),
    RABV("RABV", "reference aggregate boundary violation", "REFERENCE_FK", "for a reference link, the entity aggregate does not match the root of the aggregate in the link", "select %1$s from %2$s t1 inner join %4$s t2 on t1.%3$s = t2.object_id inner join %5$s t3 on t1.%6$s = t3.object_id where t2.object_id <> t3.aggregateroot_id"),
    MNUL("MNUL", "mandatory is null", "MANDATORY", "обязательное значение не заполнено", "select %1$s from %2$s t1 where t1.%3$s is null"),
    OWEM("OWEM", "ownerid is empty", "OWNERID_DEF", "ownerid не задан на корне агрегата", "select %1$s from %2$s t1 where t1.sys_ownerid is null"),
    OWDI("OWDI", "the owner ID differs in the root and child elements of the aggregate", "OWNERID_DIFF", "the owner ID differs in the root and child elements of the aggregate", "select %1$s from %2$s t1 inner join %3$s t2 on t1.aggregateroot_id = t2.object_id where t2.sys_ownerid is not null and t2.sys_ownerid <> t1.sys_ownerid");


    private String errorCode;
    private String errorCodeDescription;
    private String checkType;
    private String errorDetails;
    private String sqlQuery;

    DBIntegrityCheckType(String errorCode, String errorCodeDescription, String checkType, String errorDetails, String sqlQuery) {
        this.errorCode = errorCode;
        this.errorCodeDescription = errorCodeDescription;
        this.checkType = checkType;
        this.errorDetails = errorDetails;
        this.sqlQuery = sqlQuery;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // Not used yet. Can display the English representation of the problem
    public String getErrorCodeDescription() {
        return errorCodeDescription;
    }

    public String getCheckType() {
        return checkType;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }
}
