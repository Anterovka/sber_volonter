package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * Search data
 */
public final class SearchData {

    String sqlQuery;
    SqlParameterSource sqlParameterSource;

    /**
     * @param sqlQuery           SQL-запрос
     * @param sqlParameterSource The source of SQL parameters
     */
    SearchData(String sqlQuery, SqlParameterSource sqlParameterSource) {
        this.sqlQuery = sqlQuery;
        this.sqlParameterSource = sqlParameterSource;
    }

    /**
     * Get SQL query
     */
    public String getSqlQuery() {
        return sqlQuery;
    }

    /**
     * Get source of SQL parameters
     */
    public SqlParameterSource getSqlParameterSource() {
        return sqlParameterSource;
    }
}
