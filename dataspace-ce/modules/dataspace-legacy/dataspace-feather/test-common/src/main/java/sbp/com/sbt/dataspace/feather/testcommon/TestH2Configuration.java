package sbp.com.sbt.dataspace.feather.testcommon;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

import static sbp.com.sbt.dataspace.feather.testcommon.TestHelper.getDataSource;

@Profile("h2")
public class TestH2Configuration {

    @Bean
    public DataSource dataSource() {
        return getDataSource("org.h2.Driver", "jdbc:h2:mem:feather" + System.nanoTime() + ";DB_CLOSE_DELAY=-1", "feather", "feather");
    }

    @Bean
    public SchemaInitializer schemaInitializer(DataSource dataSource) {
        return new SchemaInitializer(dataSource, "/sbp/com/sbt/dataspace/feather/testmodel/h2_create_schema.sql");
    }
}
