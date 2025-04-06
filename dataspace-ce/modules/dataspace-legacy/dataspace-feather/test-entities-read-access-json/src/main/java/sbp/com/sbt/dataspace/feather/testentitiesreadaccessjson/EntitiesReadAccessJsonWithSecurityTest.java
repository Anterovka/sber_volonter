package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import sbp.com.sbt.dataspace.feather.testmodel.Action;
import sbp.com.sbt.dataspace.feather.testmodel.ActionParameterSpecial;
import sbp.com.sbt.dataspace.feather.testmodel.Entity;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.ProductLimited;
import sbp.com.sbt.dataspace.feather.testmodel.RequestPlus;
import sbp.com.sbt.dataspace.feather.testmodel.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Testing access to entities for reading through JSON with security
 */
public abstract class EntitiesReadAccessJsonWithSecurityTest extends CommonEntitiesReadAccessJsonWithSecurityTest {

    @Override
    Map<String, String> getEntityRestrictions() {
        Map<String, String> result = new LinkedHashMap<>();
        result.put(Entity.TYPE0, "it.name $like 'entity%'");
        result.put(Product.TYPE0, "it.code $like 'product%' && (it.creatorCode == 1 || it.parameters{type = ActionParameter}.request{type = RequestPlus}.$count == 1)");
        result.put(ProductLimited.TYPE0, "it.limitedOffer $like 'limitedOffer%'");
        result.put(ActionParameterSpecial.TYPE0, "it.specialOffer $like 'specialOffer%'");
        result.put(RequestPlus.TYPE0, "it.initiator.lastName == 'Ivanov'");
        result.put(Service.TYPE0, "it.startAction.algorithmCode == 1");
        result.put(Action.TYPE0, "it.algorithmCode == 1");
        return result;
    }

    @DisplayName("Test Case 16")
    @TestFactory
    public Stream<DynamicTest> testCase16() {
        return getDynamicTests(TestCase16::new);
    }

    @DisplayName("Test Case 19")
    @TestFactory
    public Stream<DynamicTest> testCase19() {
        return getDynamicTests(TestCase19::new);
    }

    @DisplayName("Test Case 23")
    @TestFactory
    public Stream<DynamicTest> testCase23() {
        return getDynamicTests(TestCase23::new);
    }

    @DisplayName("Test case 24")
    @TestFactory
    public Stream<DynamicTest> testCase24() {
        return getDynamicTests(TestCase24::new);
    }

    @DisplayName("Bug-case 7")
    @TestFactory
    public Stream<DynamicTest> bugCase7() {
        return getDynamicTests(BugCase7::new);
    }
}
