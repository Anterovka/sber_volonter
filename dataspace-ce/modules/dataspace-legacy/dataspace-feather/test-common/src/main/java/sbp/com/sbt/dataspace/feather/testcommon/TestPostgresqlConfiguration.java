package sbp.com.sbt.dataspace.feather.testcommon;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

import static sbp.com.sbt.dataspace.feather.testcommon.TestHelper.getDataSource;

@Profile("postgresql")
public class TestPostgresqlConfiguration {

    @Bean
    public DataSource dataSource() {
        return getDataSource("org.postgresql.Driver", System.getProperty("db.postgres.url"), System.getProperty("db.postgres.username"), System.getProperty("db.postgres.password"));
    }

    @Bean
    public SchemaInitializer schemaInitializer(DataSource dataSource) {
        return new SchemaInitializer(dataSource, "/sbp/com/sbt/dataspace/feather/testmodel/postgresql_create_schema.sql");
    }
}
