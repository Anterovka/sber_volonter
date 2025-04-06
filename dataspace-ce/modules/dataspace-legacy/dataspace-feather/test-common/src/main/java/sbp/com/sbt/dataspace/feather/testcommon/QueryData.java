package sbp.com.sbt.dataspace.feather.testcommon;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The query data
 */
class QueryData {

    Map<String, TableData> tablesData = new LinkedHashMap<>();
    MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
    boolean update;
}
