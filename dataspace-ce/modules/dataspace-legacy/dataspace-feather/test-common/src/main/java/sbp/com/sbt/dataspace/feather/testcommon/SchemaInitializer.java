package sbp.com.sbt.dataspace.feather.testcommon;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.wrapR;
import static sbp.com.sbt.dataspace.feather.testcommon.TestHelper.getStringFromResource;

/**
 * Schema initializer
 */
public class SchemaInitializer {

    /**
     * @param dataSource                     Data source
     * @param createSchemaScriptResourceName The name of the resource with the schema creation script
     */
    public SchemaInitializer(DataSource dataSource, String createSchemaScriptResourceName) {
        wrapR(dataSource::getConnection, connection -> Arrays.stream(getStringFromResource(SchemaInitializer.class, createSchemaScriptResourceName).split(";")).anyMatch(sql -> executeSql(connection, sql)));
    }

    /**
     * Execute SQL
     *
     * @param connection Connection
     * @param sql        SQL
     * @return Was there an error
     */
    boolean executeSql(Connection connection, String sql) {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
            return false;
        } catch (SQLException e) {
            return true;
        }
    }
}
