package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import sbp.com.sbt.dataspace.feather.tablequeryprovider.TableQueryProvider;
import sbp.com.sbt.dataspace.feather.testcommon.TestCommonConfiguration;

@Import({
        TestCommonConfiguration.class,
        EntitiesReadAccessJsonTestSecurityConfiguration.class,
        EntitiesReadAccessJsonTestSecurity2Configuration.class,
        EntitiesReadAccessJsonTestSecurity3Configuration.class,
        EntitiesReadAccessJsonConfiguration.class
})
class EntitiesReadAccessJsonTestConfiguration {

    @Bean
    @Profile("oracle")
    EntitiesReadAccessJsonSettings oracleEntitiesReadAccessJsonSettings() {
        return new EntitiesReadAccessJsonSettings()
                .setSqlDialect(SqlDialect.ORACLE)
                .setTableQueryProvider(getTableQueryProvider());
    }

    @Bean
    @Profile("postgresql")
    EntitiesReadAccessJsonSettings postgresqlEntitiesReadAccessJsonSettings() {
        return new EntitiesReadAccessJsonSettings()
                .setSqlDialect(SqlDialect.POSTGRESQL)
                .setTableQueryProvider(getTableQueryProvider());
    }

    @Bean
    @Profile("h2s1")
    EntitiesReadAccessJsonSettings h2EntitiesReadAccessJsonSettings1() {
        return new EntitiesReadAccessJsonSettings()
                .setSqlDialect(SqlDialect.H2)
                .setTableQueryProvider(getTableQueryProvider());
    }

    @Bean
    @Profile("h2s2")
    EntitiesReadAccessJsonSettings h2EntitiesReadAccessJsonSettings2() {
        return new EntitiesReadAccessJsonSettings()
                .setSqlDialect(SqlDialect.H2)
                .setDefaultLimit(100)
                .setSchemaName("public")
                .setTableQueryProvider(getTableQueryProvider());
    }

    @Bean
    @Profile("h2s3")
    EntitiesReadAccessJsonSettings h2EntitiesReadAccessJsonSettings3() {
        return new EntitiesReadAccessJsonSettings()
                .setSqlDialect(SqlDialect.H2)
                .setReadRecordsLimit(1);
    }

    @Bean
    @Profile("h2s4")
    EntitiesReadAccessJsonSettings h2EntitiesReadAccessJsonSettings4() {
        return new EntitiesReadAccessJsonSettings()
                .setSqlDialect(SqlDialect.H2)
                .setReadRecordsLimit(1)
                .setTableQueryProvider(entityType -> null);
    }

    @Bean
    @Profile("h2s5")
    EntitiesReadAccessJsonSettings h2EntitiesReadAccessJsonSettings5() {
        return new EntitiesReadAccessJsonSettings()
                .setSqlDialect(SqlDialect.H2)
                .setReadRecordsLimit(1)
                .setTableQueryProvider(entityType -> {
                    if (entityType.equals("Test3Entity")) {
                        return "select t1.ID ID " +
                                "from ${dspc.schemaPrefix}F_TEST_ENTITY t1 " +
                                "where ${dspc.param1} is not null and ${param2} is not null";
                    } else {
                        return null;
                    }
                });
    }

    @Bean
    @Profile("h2s6")
    EntitiesReadAccessJsonSettings h2EntitiesReadAccessJsonSettings6() {
        return new EntitiesReadAccessJsonSettings()
                .setSqlDialect(SqlDialect.H2)
                .setOptimizeJoins();
    }

    /**
     * Get request table provider
     */
    private static TableQueryProvider getTableQueryProvider() {
        return entityType -> {
            if (entityType.equals("UserProduct")) {
                return "select t2.ID ID, " +
                        "      t3.CODE CODE, " +
                        "      t2.RELATED_PRODUCT RELATED_PRODUCT, " +
                        "      t1.INITIATOR_FIRST_NAME INITIATOR_FIRST_NAME, " +
                        "      t1.INITIATOR_LAST_NAME INITIATOR_LAST_NAME, " +
                        "      t1.INITIATOR_AGE INITIATOR_AGE, " +
                        "      t1.INITIATOR_DOCUMENT INITIATOR_DOCUMENT " +
                        "from ${dspc.schemaPrefix}F_REQUEST t1 " +
                        "join ${dspc.schemaPrefix}F_PRODUCT t2 on t1.CREATED_ENTITY = t2.ID " +
                        "join ${dspc.schemaPrefix}F_ENTITY t3 on t2.ID = t3.ID " +
                        "where t1.INITIATOR_FIRST_NAME = ${firstName} and t1.INITIATOR_LAST_NAME = ${lastName}";
            } else if (entityType.equals("ProductCopy")) {
                return "select t1.ID ID " +
                        "from ${dspc.schemaPrefix}F_PRODUCT t1 ";
            } else if (entityType.equals("Test2Entity")) {
                return "select t1.ID ID " +
                        "from ${dspc.schemaPrefix}F_TEST_ENTITY t1 " +
                        "where ${character} is not null " +
                        "and (t1.ID = ${string} or t1.ID in (${strings})) " +
                        "and ${byte} is not null " +
                        "and ${short} is not null " +
                        "and ${integer} is not null " +
                        "and ${long} is not null " +
                        "and ${float} is not null " +
                        "and ${double} is not null " +
                        "and ${bigDecimal} is not null " +
                        "and ${date} is not null " +
                        "and ${datetime} is not null " +
                        "and ${offsetDatetime} is not null " +
                        "and ${boolean} is not null";
            } else if (entityType.equals("Test3Entity")) {
                return "select t1.ID ID " +
                        "from ${dspc.schemaPrefix}F_TEST_ENTITY t1 " +
                        "where t1.p13 = ${dspc.character} " +
                        "and t1.p1 = ${dspc.string} " +
                        "and t1.p2 = ${dspc.byte} " +
                        "and t1.p3 = ${dspc.short} " +
                        "and t1.p4 = ${dspc.integer} " +
                        "and t1.p5 = ${dspc.long} " +
                        "and t1.p12 between ${dspc.float} - 0.01 and ${dspc.float} + 0.01 " +
                        "and t1.p6 between ${dspc.double} - 0.01 and ${dspc.double} + 0.01 " +
                        "and t1.p10 between ${dspc.bigDecimal} - 0.01 and ${dspc.bigDecimal} + 0.01 " +
                        "and t1.p14 = ${dspc.date} " +
                        "and t1.p7 = ${dspc.dateTime} " +
                        "and t1.p15 = ${dspc.offsetDateTime} " +
                        "and t1.p8 = ${dspc.boolean} " +
                        "and '${test}' = '${test}'";
            } else {
                return null;
            }
        };
    }
}
