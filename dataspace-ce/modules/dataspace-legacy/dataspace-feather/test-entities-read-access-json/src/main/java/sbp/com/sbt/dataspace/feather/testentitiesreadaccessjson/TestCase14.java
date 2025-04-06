package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Action;
import sbp.com.sbt.dataspace.feather.testmodel.ActionParameter;
import sbp.com.sbt.dataspace.feather.testmodel.ActionParameterSpecial;
import sbp.com.sbt.dataspace.feather.testmodel.ActionSpecial;
import sbp.com.sbt.dataspace.feather.testmodel.Agreement;
import sbp.com.sbt.dataspace.feather.testmodel.Operation;
import sbp.com.sbt.dataspace.feather.testmodel.Parameter;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.ProductPlus;
import sbp.com.sbt.dataspace.feather.testmodel.Request;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 14<ul>
 * <li>Testing access to intermediate elements of collections</li>
 * </ul>
 */
class TestCase14 extends TestCase {

    TestCase14() {
        super(false);
    }

    @Override
    void createEntities() {
        String operation1Id = createEntity(Operation.TYPE0, "operation1", propBuilder());

        String action1Id = createEntity(Action.TYPE0, "action1", propBuilder()
                .add(Action.OPERATION, operation1Id));

        String actionParameter1Id = createEntity(ActionParameter.TYPE0, "actionParameter1", propBuilder()
                .add(Parameter.ENTITY, action1Id));

        createEntity(Parameter.TYPE0, "parameter1", propBuilder()
                .add(Parameter.ENTITY, actionParameter1Id));

        String actionSpecial1Id = createEntity(ActionSpecial.TYPE0, "actionSpecial1", propBuilder()
                .add(Action.ALGORITHM_CODE, 1)
                .add(ActionSpecial.SPECIAL_OFFER, "parameter")
                .add(Action.OPERATION, operation1Id));

        String actionParameterSpecial1Id = createEntity(ActionParameterSpecial.TYPE0, "actionParameterSpecial1", propBuilder()
                .add(ActionParameterSpecial.SPECIAL_OFFER, "2")
                .add(Parameter.ENTITY, actionSpecial1Id));

        createEntity(Parameter.TYPE0, "parameter2", propBuilder()
                .add(Parameter.ENTITY, actionParameterSpecial1Id));
        createEntity(Parameter.TYPE0, "parameter3", propBuilder()
                .add(Parameter.ENTITY, actionParameterSpecial1Id));
        createEntity(Parameter.TYPE0, "parameter20", propBuilder()
                .add(Parameter.ENTITY, actionParameterSpecial1Id));

        String actionParameterSpecial2Id = createEntity(ActionParameterSpecial.TYPE0, "actionParameterSpecial2", propBuilder()
                .add(Parameter.ENTITY, actionSpecial1Id)
                .add(ActionParameterSpecial.SPECIAL_OFFER, "2"));

        createEntity(Parameter.TYPE0, "parameter4", propBuilder()
                .add(Parameter.ENTITY, actionParameterSpecial2Id));
        createEntity(Parameter.TYPE0, "parameter5", propBuilder()
                .add(Parameter.ENTITY, actionParameterSpecial2Id));

        String product1Id = createEntity(Product.TYPE0, "product1", propBuilder()
                .add(Product.ALIASES, Arrays.asList("product1_alias1", "alias2")));
        String product2Id = createEntity(Product.TYPE0, "product2", propBuilder()
                .add(Product.ALIASES, Arrays.asList("alias3", "alias4")));

        String productPlus1Id = createEntity(ProductPlus.TYPE0, "productPlus1", propBuilder()
                .add(ProductPlus.AFFECTED_PRODUCTS, Arrays.asList(product1Id, product2Id)));

        String parameter6Id = createEntity(Parameter.TYPE0, "parameter6", propBuilder()
                .add(Parameter.ENTITY, productPlus1Id));
        createEntity(Parameter.TYPE0, "parameter7", propBuilder()
                .add(Parameter.VALUE, "parameter6")
                .add(Parameter.ENTITY, parameter6Id));

        String agreement1Id = createEntity(Agreement.TYPE0, "agreement1", propBuilder());
        createEntity(Agreement.TYPE0, "request1agreement1_1", propBuilder()
                .add(Agreement.DOCUMENT, agreement1Id));

        createEntity(Request.TYPE0, "request1", propBuilder()
                .add(Request.CREATED_ENTITY, operation1Id)
                .add(Request.AGREEMENT, agreement1Id));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Reading", "read"),
            testData("Reading (2)", "read2"),
            testData("Reading (3)", "read3"));
    }
}
