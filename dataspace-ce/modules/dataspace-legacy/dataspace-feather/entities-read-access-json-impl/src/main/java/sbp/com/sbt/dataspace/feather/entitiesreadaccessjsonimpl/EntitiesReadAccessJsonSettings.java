package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.tablequeryprovider.TableQueryProvider;

import java.time.ZoneId;
import java.time.ZoneOffset;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.getFullDescription;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Access settings to entities for reading through JSON
 */
public final class EntitiesReadAccessJsonSettings {

    public static final int DEFAULT_MAX_SECURITY_RECURSION_DEPTH = 10;

    SqlDialect sqlDialect;
    Integer defaultLimit;
    String schemaName;
    int maxSecurityRecursionDepth = DEFAULT_MAX_SECURITY_RECURSION_DEPTH;
    int readRecordsLimit = Integer.MAX_VALUE;
    TableQueryProvider tableQueryProvider = null;
    boolean optimizeJoins = false;
    ZoneId offsetDateTimeZoneId = ZoneOffset.UTC;

    /**
     * Get the identifier of the date and time zone with offset
     */
    public ZoneId getOffsetDateTimeZoneId() {
        return offsetDateTimeZoneId;
    }

    /**
     * Set the time zone identifier with offset
     *
     * @return Current settings
     */
    public EntitiesReadAccessJsonSettings setOffsetDateTimeZoneId(ZoneId offsetDateTimeZoneId) {
        this.offsetDateTimeZoneId = offsetDateTimeZoneId;
        return this;
    }

    /**
     * Get SQL dialect
     */
    public SqlDialect getSqlDialect() {
        return sqlDialect;
    }

    /**
     * Set the SQL dialect
     *
     * @return Current settings
     */
    public EntitiesReadAccessJsonSettings setSqlDialect(SqlDialect sqlDialect) {
        this.sqlDialect = sqlDialect;
        return this;
    }

    /**
     * Get the default limit on the number of elements
     */
    public Integer getDefaultLimit() {
        return defaultLimit;
    }

    /**
     * Set a default limit on the number of elements
     *
     * @return Current settings
     */
    public EntitiesReadAccessJsonSettings setDefaultLimit(int defaultLimit) {
        if (defaultLimit < 0) {
            throw new InvalidLimitException(defaultLimit);
        }
        this.defaultLimit = defaultLimit;
        return this;
    }

    /**
     * Get the schema name
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * Set the name of the schema
     *
     * @return Current settings
     */
    public EntitiesReadAccessJsonSettings setSchemaName(String schemaName) {
        this.schemaName = schemaName;
        return this;
    }

    /**
     * Get maximum recursion depth for safety
     */
    public int getMaxSecurityRecursionDepth() {
        return maxSecurityRecursionDepth;
    }

    /**
     * Set the maximum recursion depth for safety
     *
     * @return Current settings
     */
    public EntitiesReadAccessJsonSettings setMaxSecurityRecursionDepth(int maxSecurityRecursionDepth) {
        if (maxSecurityRecursionDepth < 1) {
            throw new InvalidMaxSecurityRecursionDepthException(maxSecurityRecursionDepth);
        }
        this.maxSecurityRecursionDepth = maxSecurityRecursionDepth;
        return this;
    }

    /**
     * Get the limit on the number of read records
     */
    public int getReadRecordsLimit() {
        return readRecordsLimit;
    }

    /**
     * Set a limit on the number of records read
     *
     * @return Current settings
     */
    public EntitiesReadAccessJsonSettings setReadRecordsLimit(int readRecordsLimit) {
        if (readRecordsLimit < 1) {
            throw new InvalidReadRecordsLimitException(readRecordsLimit);
        }
        this.readRecordsLimit = readRecordsLimit;
        return this;
    }

    /**
     * Get request table provider
     */
    public TableQueryProvider getTableQueryProvider() {
        return tableQueryProvider;
    }

    /**
     * Set the request table provider
     *
     * @return Current settings
     */
    public EntitiesReadAccessJsonSettings setTableQueryProvider(TableQueryProvider tableQueryProvider) {
        this.tableQueryProvider = tableQueryProvider;
        return this;
    }

    /**
     * Optimize joins
     */
    public boolean doOptimizeJoins() {
        return optimizeJoins;
    }

    /**
     * Set optimization flag for join's
     *
     * @return Current settings
     */
    public EntitiesReadAccessJsonSettings setOptimizeJoins() {
        this.optimizeJoins = true;
        return this;
    }

    @Override
    public String toString() {
        return getFullDescription("Access settings to entities for reading",
            param("SQL dialect", sqlDialect),
            param("Default limit of elements", defaultLimit),
            param("Schema name", schemaName),
            param("Maximum recursion depth for security", maxSecurityRecursionDepth),
            param("Limit on the number of read records", readRecordsLimit),
            param("Optimize joins", optimizeJoins),
            param("Identifier of the date and time zone with offset", offsetDateTimeZoneId));
    }
}
