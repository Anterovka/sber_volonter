package sbp.com.sbt.dataspace.feather.testcommon;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import sbp.com.sbt.dataspace.feather.common.UnexpectedException;
import sbp.com.sbt.dataspace.feather.modeldescription.CollectionDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.GroupDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.InheritanceStrategy;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.PropertyDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.PropertyDescriptionWithColumnName;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.wrapR;

/**
 * Assistant for testing
 */
public class TestHelper {

    static final String UNIQUE_PREFIX = String.valueOf(System.nanoTime());
    public static final String AGGREGATE_SETTING = "$aggregate";
    public static final String VERSION_SETTING = "$version";
    public static final LocalDate DATE = LocalDate.of(2020, 10, 29);
    public static final LocalDateTime DATETIME = LocalDateTime.of(2020, 10, 29, 16, 12, 10, 123000000);
    public static final OffsetDateTime OFFSET_DATETIME = OffsetDateTime.of(2020, 10, 29, 16, 12, 10, 123000000, ZoneOffset.of("+08:00"));
    public static final byte[] BYTES = new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE};
    public static final String STRING_5000 = getStringFromResource(TestHelper.class, "/string5000.txt");
    public static final String NONEXISTENT_ID = "nonexistentId";

    ModelDescription modelDescription;
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * @param modelDescription           Description of the model
     * @param namedParameterJdbcTemplate The JDBC template with named parameters
     */
    TestHelper(ModelDescription modelDescription, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.modelDescription = modelDescription;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    /**
     * Get data source
     *
     * @param driverClassName The name of the driver class
     * @param jdbcUrl         URL jdbc
     * @param username        Username
     * @param password        Password
     */
    static DataSource getDataSource(String driverClassName, String jdbcUrl, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driverClassName);
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        return ProxyDataSourceBuilder.create(new HikariDataSource(config))
                .logQueryBySlf4j()
                .build();
    }

    /**
     * Get column data
     *
     * @param tablesData          The table data
     * @param propertyDescription Property description
     * @param entityId            Entity ID
     */
    Map<String, ColumnData> getColumnsData(Map<String, Map<String, ColumnData>> tablesData, PropertyDescription propertyDescription, String entityId) {
        EntityDescription rootEntityDescription = propertyDescription.getOwnerEntityDescription().getRootEntityDescription();
        EntityDescription targetEntityDescription = rootEntityDescription.getInheritanceStrategy() == InheritanceStrategy.SINGLE_TABLE ? rootEntityDescription : propertyDescription.getOwnerEntityDescription();
        Map<String, ColumnData> columnsData = tablesData.computeIfAbsent(targetEntityDescription.getTableName(), key -> new LinkedHashMap<>());
        if (columnsData.isEmpty()) {
            columnsData.put(targetEntityDescription.getIdColumnName(), new ColumnData("id", entityId));
        }
        return columnsData;
    }

    /**
     * Execute SQL queries
     *
     * @param entityType Entity type
     * @param entityId   Entity ID
     * @param properties Properties
     * @return The entity's Id
     */
    String executeSqlQueries(String entityType, String entityId, Map<String, Object> properties) {
        EntityDescription entityDescription = modelDescription.getEntityDescription(entityType);
        EntityDescription rootEntityDescription = entityDescription.getRootEntityDescription();
        Map<String, Map<String, ColumnData>> tablesData = new LinkedHashMap<>();
        String result;
        if (entityId == null) {
            result = UNIQUE_PREFIX + UUID.randomUUID().toString();
            EntityDescription currentEntityDescription = entityDescription;
            do {
                if (currentEntityDescription.getTableName() != null) {
                    Map<String, ColumnData> entityTableData = new LinkedHashMap<>();
                    entityTableData.put(currentEntityDescription.getIdColumnName(), new ColumnData("id", result));
                    tablesData.put(currentEntityDescription.getTableName(), entityTableData);
                }
                currentEntityDescription = currentEntityDescription.getParentEntityDescription();
            } while (currentEntityDescription != null);
            Map<String, ColumnData> rootEntityTableData = tablesData.get(rootEntityDescription.getTableName());
            if (!rootEntityDescription.isFinal()) {
                rootEntityTableData.put(rootEntityDescription.getTypeColumnName(), new ColumnData("type", entityDescription.getName()));
            }
            if (rootEntityDescription.getAggregateColumnName() != null && properties.containsKey(AGGREGATE_SETTING)) {
                rootEntityTableData.put(rootEntityDescription.getAggregateColumnName(), new ColumnData("aggregate", properties.get(AGGREGATE_SETTING)));
            }
        } else {
            result = entityId;
        }
        if (rootEntityDescription.getSystemLocksTableName() != null && properties.containsKey(VERSION_SETTING)) {
            Map<String, ColumnData> systemLocksTableData = new LinkedHashMap<>();
            systemLocksTableData.put(rootEntityDescription.getSystemLocksAggregateColumnName(), new ColumnData("id", result));
            systemLocksTableData.put(rootEntityDescription.getSystemLocksVersionColumnName(), new ColumnData("version", properties.get(VERSION_SETTING)));
            tablesData.put(rootEntityDescription.getSystemLocksTableName(), systemLocksTableData);
        }
        properties.remove(AGGREGATE_SETTING);
        properties.remove(VERSION_SETTING);
        properties.forEach((propertyName, value) -> {
            PropertyDescriptionWithColumnName propertyDescription = entityDescription.getPrimitiveDescriptions().get(propertyName);
            if (propertyDescription == null) {
                propertyDescription = entityDescription.getReferenceDescriptions().get(propertyName);
            }
            if (propertyDescription != null) {
                getColumnsData(tablesData, propertyDescription, result).put(propertyDescription.getColumnName(), new ColumnData(propertyDescription.getName(), value));
                return;
            }
            CollectionDescription collectionDescription = entityDescription.getPrimitivesCollectionDescriptions().get(propertyName);
            if (collectionDescription == null) {
                collectionDescription = entityDescription.getReferencesCollectionDescriptions().get(propertyName);
            }
            if (collectionDescription != null) {
                CollectionDescription currentCollectionDescription = collectionDescription;
                ((List<Object>) value).forEach(element -> namedParameterJdbcTemplate.update("insert into " + currentCollectionDescription.getTableName() + '(' + currentCollectionDescription.getOwnerColumnName() + ", " + currentCollectionDescription.getColumnName() + ") values (:owner, :element)", new MapSqlParameterSource()
                        .addValue("owner", result)
                        .addValue("element", element)));
                return;
            }
            GroupDescription groupDescription = entityDescription.getGroupDescriptions().get(propertyName);
            if (groupDescription != null) {
                Map<String, ColumnData> columnsData = getColumnsData(tablesData, groupDescription, result);
                ((PropertiesBuilder) value).properties.forEach((propertyName2, value2) -> {
                    PropertyDescriptionWithColumnName propertyDescription2 = groupDescription.getPrimitiveDescriptions().get(propertyName2);
                    if (propertyDescription2 == null) {
                        propertyDescription2 = groupDescription.getReferenceDescriptions().get(propertyName2);
                    }
                    if (propertyDescription2 != null) {
                        columnsData.put(propertyDescription2.getColumnName(), new ColumnData(groupDescription.getName() + '_' + propertyName2, value2));
                        return;
                    }
                    throw new UnexpectedException();
                });
                return;
            }
            throw new UnexpectedException();
        });
        if (entityId == null) {
            tablesData.forEach((tableName, columnsData) -> {
                String parameters = columnsData.values().stream()
                        .map(columnData -> ':' + columnData.parameterName)
                        .collect(Collectors.joining(", "));
                MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
                columnsData.values().forEach(columnData -> sqlParameterSource.addValue(columnData.parameterName, columnData.value));
                namedParameterJdbcTemplate.update("insert into " + tableName + '(' + String.join(", ", columnsData.keySet()) + ") values (" + parameters + ')', sqlParameterSource);
            });
        } else {
            tablesData.forEach((tableName, columnsData) -> {
                String columnValues = columnsData.entrySet().stream()
                        .skip(1)
                        .map(entry -> entry.getKey() + " = :" + entry.getValue().parameterName)
                        .collect(Collectors.joining(", "));
                MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
                columnsData.values().forEach(columnData -> sqlParameterSource.addValue(columnData.parameterName, columnData.value));
                namedParameterJdbcTemplate.update("update " + tableName + " set " + columnValues + " where " + columnsData.keySet().iterator().next() + " = :id", sqlParameterSource);
            });
        }
        return result;
    }

    /**
     * Perform in transaction
     *
     * @param code Код
     */
    @Transactional
    public void executeInTransaction(Runnable code) {
        code.run();
    }

    /**
     * Create entity
     *
     * @param entityType        Entity type
     * @param propertiesBuilder Property builder
     * @return The entity ID
     */
    public String createEntity(String entityType, PropertiesBuilder propertiesBuilder) {
        return executeSqlQueries(entityType, null, propertiesBuilder.properties);
    }

    /**
     * Update entity
     *
     * @param entityType        Entity type
     * @param entityId          Entity ID
     * @param propertiesBuilder Property builder
     */
    public void updateEntity(String entityType, String entityId, PropertiesBuilder propertiesBuilder) {
        executeSqlQueries(entityType, entityId, propertiesBuilder.properties);
    }

    /**
     * Get string from resource
     *
     * @param class0       Class
     * @param resourceName Resource name
     */
    public static String getStringFromResource(Class<?> class0, String resourceName) {
        return wrapR(() -> class0.getResourceAsStream(resourceName), inputStream -> inputStream == null ? null : new Scanner(inputStream, StandardCharsets.UTF_8.name()).useDelimiter("\\Z").next());
    }

    /**
     * Make sure that the exception occurred due to
     *
     * @param exceptionClass Exception class
     * @param code           Код
     */
    public static void assertThrowsCausedBy(Class<? extends Throwable> exceptionClass, Supplier<?> code) {
        try {
            code.get();
            fail("An exception was expected");
        } catch (Exception e) {
            while (e.getCause() != null) {
                e = (Exception) e.getCause();
            }
            assertEquals(exceptionClass, e.getClass());
        }
    }
}
