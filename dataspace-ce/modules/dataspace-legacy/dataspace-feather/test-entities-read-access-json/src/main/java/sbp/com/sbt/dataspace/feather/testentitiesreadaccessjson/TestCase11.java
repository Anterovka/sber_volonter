package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Agreement;
import sbp.com.sbt.dataspace.feather.testmodel.Document;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.Request;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 11<ul>
 * <li>Testing invalid data in the database</li>
 * </ul>
 */
class TestCase11 extends TestCase {

    TestCase11() {
        super(false);
    }

    @Override
    void createEntities() {
        String product1Id = createEntity(Product.TYPE0, "product1", propBuilder());
        String product2Id = createEntity(Product.TYPE0, "product2", propBuilder()
                .add(Product.RELATED_PRODUCT, product1Id));

        String document1Id = createEntity(Document.TYPE0, "document1", propBuilder()
                .add(Document.PRODUCT, product2Id));

        String agreement1Id = createEntity(Agreement.TYPE0, "agreement1", propBuilder()
                .add(Agreement.DOCUMENT, document1Id));

        String request1Id = createEntity(Request.TYPE0, "request1", propBuilder()
                .add(Request.AGREEMENT, agreement1Id));

        namedParameterJdbcTemplate.update("update f_document set type = 'Product' where id = :document1Id", new MapSqlParameterSource().addValue("document1Id", document1Id));
        namedParameterJdbcTemplate.update("delete from f_product where id = :product1Id", new MapSqlParameterSource().addValue("product1Id", product1Id));
        namedParameterJdbcTemplate.update("update f_request set type = 'Product' where id = :request1Id", new MapSqlParameterSource().addValue("request1Id", request1Id));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Reading", "read"),
            testData("Reading (2)", "read2"),
            testData("Reading (3)", "read3"));
    }
}
