package sbp.com.sbt.dataspace.liquibase;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.CompositeResourceAccessor;
import liquibase.resource.ResourceAccessor;
import liquibase.resource.SearchPathResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;


public class LBScriptRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(LBScriptRunner.class);

    private static final String LIQUIBASE_PARAMETERS_PREFIX = "liquibase.parameters";
    private static final int LIQUIBASE_PARAMETERS_PREFIX_LENGTH = LIQUIBASE_PARAMETERS_PREFIX.length();

    private final Properties properties;
    private final String searchPath;
    private final boolean isUseExternalDb;

    public LBScriptRunner(Properties properties, String searchPath) {
        this.properties = properties;
        this.searchPath = searchPath;
        this.isUseExternalDb = isUseExternalDb(properties);
    }

    public void run() {

        String driverClass = driverClass();
        if (driverClass != null && !driverClass.isBlank()) {
            try {
                Class.forName(driverClass);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
        LOGGER.info("Connect to: {}, by {}", url(), username());
        try (Connection connection = DriverManager.getConnection(
                url(),
                username(),
                password()
        )) {
            LOGGER.info("Start run lb on {}.", url());
            JdbcConnection jdbcConnection = new JdbcConnection(connection);
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);
            database.setDefaultSchemaName(this.properties.getProperty("liquibase.defaultSchema"));
            database.setLiquibaseSchemaName(this.properties.getProperty("liquibase.liquibaseSchema"));

            LOGGER.info("DefaultSchema {}, LiquibaseSchema {}.", database.getDefaultSchemaName(), database.getLiquibaseSchemaName());

            Liquibase liquibase = new Liquibase("changelog.xml", getResourceAccessor(), database);

            Map<String, Object> customProperties = extractLiquibaseProperties();
            customProperties.forEach(liquibase::setChangeLogParameter);

            if (isDropFirst()) {
                LOGGER.info("Drop DB is active.");
                liquibase.dropAll();
            }

            liquibase.update(new Contexts("main"), new LabelExpression());
        } catch (SQLException | LiquibaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String driverClass() {
        return this.properties.getProperty("spring.datasource.driver-class-name");
    }

    private String url() {
        if (isUseExternalDb) {
            LOGGER.info("Use external DB.");
            LOGGER.info("URI:{}", this.properties.getProperty("spring.datasource.url"));
            return this.properties.getProperty("spring.datasource.url");
        }
        return "jdbc:h2:tcp://localhost/%s/testdb".formatted(properties.getProperty("h2-db-path"));
    }

    private String username() {
        return this.properties.getProperty("spring.datasource.username");
    }

    private String password() {
        return this.properties.getProperty("spring.datasource.password");
    }

    private boolean isDropFirst() {
        return Boolean.parseBoolean(
                Optional.ofNullable(this.properties.getProperty("liquibase.dropFirst"))
                        .orElse("false")
        );
    }

    private Map<String, Object> extractLiquibaseProperties() {
        return this.properties.entrySet().stream()
                .filter(entry -> entry.getKey().toString().contains(LIQUIBASE_PARAMETERS_PREFIX))
                .collect(Collectors.toMap(
                                entry ->
                                {
                                    String key = entry.getKey().toString();
                                    return key.substring(
                                            key.lastIndexOf(LIQUIBASE_PARAMETERS_PREFIX) + LIQUIBASE_PARAMETERS_PREFIX_LENGTH + 1
                                    );
                                },
                                Map.Entry::getValue
                        )
                );
    }

    private ResourceAccessor getResourceAccessor() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        ClassLoaderResourceAccessor clOpener = new ClassLoaderResourceAccessor(classLoader);
        SearchPathResourceAccessor fsOpener = new SearchPathResourceAccessor(this.searchPath);
        return new CompositeResourceAccessor(fsOpener, clOpener);
    }

    private static boolean isUseExternalDb(Properties dataSpaceCoreLocalRunnerProperties) {
        return dataSpaceCoreLocalRunnerProperties.getProperty("h2DbPath") == null ||
            dataSpaceCoreLocalRunnerProperties.getProperty("h2DbPath").isBlank();
    }

    public static void execute(Properties properties, String searchPath) {
        new LBScriptRunner(
            properties,
            searchPath
        ).run();
    }

}
