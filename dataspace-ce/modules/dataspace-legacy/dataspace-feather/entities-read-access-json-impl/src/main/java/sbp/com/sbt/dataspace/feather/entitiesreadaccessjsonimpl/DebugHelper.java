package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Assistant for debugging
 */
final class DebugHelper {

    private DebugHelper() {
    }

    /**
     * Get spaces
     *
     * @param count The number of
     */
    static String getSpaces(int count) {
        char[] spaces = new char[count];
        Arrays.fill(spaces, ' ');
        return new String(spaces);
    }

    /**
     * Get the result of the SQL query
     *
     * @param namedParameterJdbcTemplate The JDBC template with named parameters
     * @param sqlQuery                   SQL-запрос
     * @param sqlParameterSource         The source of SQL parameters
     */
    static String getSqlQueryResult(NamedParameterJdbcTemplate namedParameterJdbcTemplate, String sqlQuery, SqlParameterSource sqlParameterSource) {
        List<List<String>> data = new ArrayList<>();
        Map<Integer, Integer> columnSizes = new HashMap<>();
        namedParameterJdbcTemplate.query(sqlQuery, sqlParameterSource, resultSet -> {
            List<String> rowData = new ArrayList<>(resultSet.getMetaData().getColumnCount());
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); ++i) {
                String value = resultSet.getString(i);
                int columnSize = columnSizes.computeIfAbsent(i, key -> 4);
                if (value != null && value.length() > columnSize) {
                    columnSizes.put(i, value.length());
                }
                rowData.add(value);
            }
            data.add(rowData);
        });
        StringBuilder stringBuilder = new StringBuilder();
        data.forEach(rowData -> {
            String value = rowData.get(0);
            stringBuilder.append(value);
            for (int i = 1; i < rowData.size(); ++i) {
                stringBuilder
                    .append(getSpaces(columnSizes.get(i) - (value == null ? 4 : value.length()) + 4))
                    .append("|    ");
                value = rowData.get(i);
                stringBuilder.append(value);
            }
            stringBuilder.append('\n');
        });
        return stringBuilder.toString();
    }
}
