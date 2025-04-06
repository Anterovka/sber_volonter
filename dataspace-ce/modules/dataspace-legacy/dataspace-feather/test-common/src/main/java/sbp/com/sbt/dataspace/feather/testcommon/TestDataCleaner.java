package sbp.com.sbt.dataspace.feather.testcommon;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.InheritanceStrategy;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;

import java.util.stream.Stream;

/**
 * Test data cleaner
 */
public class TestDataCleaner implements DisposableBean {

    ModelDescription modelDescription;
    TestHelper testHelper;
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * @param modelDescription           Description of the model
     * @param testHelper                 Tester's helper
     * @param namedParameterJdbcTemplate The JDBC template with named parameters
     */
    TestDataCleaner(ModelDescription modelDescription, TestHelper testHelper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.modelDescription = modelDescription;
        this.testHelper = testHelper;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void destroy() {
        testHelper.executeInTransaction(() -> modelDescription.getEntityDescriptions().values().forEach(entityDescription -> {
            EntityDescription rootEntityDescription = entityDescription.getRootEntityDescription();
            MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                    .addValue("idTemplate", TestHelper.UNIQUE_PREFIX + '%');
            if (rootEntityDescription.getInheritanceStrategy() == InheritanceStrategy.JOINED || entityDescription.equals(rootEntityDescription)) {
                namedParameterJdbcTemplate.update("delete from " + entityDescription.getTableName() + " where " + entityDescription.getIdColumnName() + " like :idTemplate", sqlParameterSource);
            }
            if (entityDescription.getSystemLocksTableName() != null) {
                namedParameterJdbcTemplate.update("delete from " + entityDescription.getSystemLocksTableName() + " where " + entityDescription.getSystemLocksAggregateColumnName() + " like :idTemplate", sqlParameterSource);
            }
            Stream.concat(
                    entityDescription.getDeclaredPrimitivesCollectionDescriptions().values().stream(),
                    entityDescription.getDeclaredReferencesCollectionDescriptions().values().stream())
                    .forEach(collectionDescription -> namedParameterJdbcTemplate.update("delete from " + collectionDescription.getTableName() + " where " + collectionDescription.getOwnerColumnName() + " like :idTemplate", sqlParameterSource));
        }));
    }
}
