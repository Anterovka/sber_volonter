package sbp.com.sbt.dataspace.feather.tablequeryprovider;

/**
 * Table request provider
 */
public interface TableQueryProvider {

    /**
     * Get request
     *
     * @param entityName Entity name
     */
    String getQuery(String entityName);
}
