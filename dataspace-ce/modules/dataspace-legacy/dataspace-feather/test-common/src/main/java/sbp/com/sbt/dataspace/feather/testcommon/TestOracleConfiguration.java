package sbp.com.sbt.dataspace.feather.testcommon;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

import static sbp.com.sbt.dataspace.feather.testcommon.TestHelper.getDataSource;

@Profile("oracle")
public class TestOracleConfiguration {

    @Bean
    public DataSource dataSource() {
        return getDataSource("oracle.jdbc.driver.OracleDriver", System.getProperty("db.oracle.url"), System.getProperty("db.oracle.username"), System.getProperty("db.oracle.password"));
    }

    @Bean
    public SchemaInitializer schemaInitializer(DataSource dataSource) {
        return new SchemaInitializer(dataSource, "/sbp/com/sbt/dataspace/feather/testmodel/oracle_create_schema.sql");
    }
}
