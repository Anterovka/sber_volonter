package sbp.com.sbt.dataspace.feather.testcommon;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.modeldescriptionimpl.ModelDescriptionConfiguration;
import sbp.com.sbt.dataspace.feather.modeldescriptionimpl.ModelDescriptionSettings;

import javax.sql.DataSource;

@Import({
        ModelDescriptionConfiguration.class,
        TestOracleConfiguration.class,
        TestPostgresqlConfiguration.class,
        TestH2Configuration.class
})
@EnableTransactionManagement
public class TestCommonConfiguration {

    @Bean
    public ModelDescriptionSettings modelDescriptionSettings() {
        return new ModelDescriptionSettings()
                .setModelResourceName("/sbp/com/sbt/dataspace/feather/testmodel/model.xml");
    }

    @Bean
    @Profile("tx")
    public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @Profile("tx")
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    @Profile("tx")
    public TestHelper testHelper(ModelDescription modelDescription, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new TestHelper(modelDescription, namedParameterJdbcTemplate);
    }

    @Bean
    @Profile("cleanTestData")
    public TestDataCleaner testDataCleaner(ModelDescription modelDescription, TestHelper testHelper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new TestDataCleaner(modelDescription, testHelper, namedParameterJdbcTemplate);
    }
}
