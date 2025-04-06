package sbp.com.sbt.dataspace.feather.stringexpressions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sbp.com.sbt.dataspace.feather.expressions.ExpressionsProcessor;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpressionBuilder;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.testmodel.ActionSpecial;
import sbp.com.sbt.dataspace.feather.testmodel.Entity;
import sbp.com.sbt.dataspace.feather.testmodel.Operation;
import sbp.com.sbt.dataspace.feather.testmodel.Parameter;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.TestEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sbp.com.sbt.dataspace.feather.testcommon.TestHelper.assertThrowsCausedBy;

@DisplayName("Testing the primitive expression builder based on a string")
@SpringJUnitConfig(StringExpressionsTestConfiguration.class)
public class StringPrimitiveExpressionBuilderTest {

    static final String PRIMITIVE_EXPRESSION_STRING1 = "root.parameters{type=ActionParameter,cond=it.executorName=='Vasya'}.value.$max";
    static final String PRIMITIVE_EXPRESSION_STRING2 = "it.value";
    static final String PRIMITIVE_EXPRESSION_STRING3 = "root{type=ProductLimited}.limitedOffer";
    static final String PRIMITIVE_EXPRESSION_STRING4 = "it";
    static final String PRIMITIVE_EXPRESSION_STRING5 = "it{type=ProductLimited}.limitedOffer";
    static final String PRIMITIVE_EXPRESSION_STRING6 = "1";
    static final String PRIMITIVE_EXPRESSION_STRING7 = "root.rates.$avg";
    static final String PRIMITIVE_EXPRESSION_STRING8 = "root.actions{type=ActionSpecial,elemAlias=action,cond=it.parameters{type=ActionParameter,elemAlias=parameter,cond=it.parameters{cond=it.code==@action.specialOffer+@parameter{type=ActionParameterSpecial}.specialOffer}.$exists}.$exists}.$count";
    static final String PRIMITIVE_EXPRESSION_STRING9 = "it.parameters{type=ActionParameter,elemAlias=parameter,cond=it.parameters{cond=it.code==@action.specialOffer+@parameter{type=ActionParameterSpecial}.specialOffer}.$exists}.$count";
    static final String PRIMITIVE_EXPRESSION_STRING10 = "coalesce(root.p1,root.code)";
    static final String PRIMITIVE_EXPRESSION_STRING11 = "entities{type=Product,cond=it.code==root.code}.$count";
    static final String PRIMITIVE_EXPRESSION_STRING12 = "(root.code=='testEntity1').$asBoolean";

    @Autowired
    ModelDescription modelDescription;
    @Autowired
    @Qualifier(StringExpressionsConfiguration.EXPRESSIONS_PROCESSOR_BEAN_NAME)
    ExpressionsProcessor expressionsProcessor;

    /**
     * Check primitive expression builder
     *
     * @param expressionsProcessor       The expressions processor
     * @param expectedString             Expected string
     * @param primitiveExpressionBuilder Primitive expression builder
     */
    static void assertPrimitiveExpressionBuilder(ExpressionsProcessor expressionsProcessor, String expectedString, PrimitiveExpressionBuilder primitiveExpressionBuilder) {
        assertEquals(expectedString, primitiveExpressionBuilder.build(expressionsProcessor).toString());
    }

    @DisplayName("Test 1")
    @Test
    public void test1() {
        assertPrimitiveExpressionBuilder(expressionsProcessor, PRIMITIVE_EXPRESSION_STRING1, new StringPrimitiveExpressionBuilder(PRIMITIVE_EXPRESSION_STRING1, modelDescription, Entity.TYPE0));
    }

    @DisplayName("Test 2")
    @Test
    public void test2() {
        assertPrimitiveExpressionBuilder(expressionsProcessor, PRIMITIVE_EXPRESSION_STRING2, new StringPrimitiveExpressionBuilder(PRIMITIVE_EXPRESSION_STRING2, modelDescription, Entity.TYPE0, Parameter.TYPE0));
    }

    @DisplayName("Test 3")
    @Test
    public void test3() {
        assertPrimitiveExpressionBuilder(expressionsProcessor, PRIMITIVE_EXPRESSION_STRING3, new StringPrimitiveExpressionBuilder(PRIMITIVE_EXPRESSION_STRING3, modelDescription, Entity.TYPE0));
    }

    @DisplayName("Test 4")
    @Test
    public void test4() {
        assertPrimitiveExpressionBuilder(expressionsProcessor, PRIMITIVE_EXPRESSION_STRING4, new StringPrimitiveExpressionBuilder(PRIMITIVE_EXPRESSION_STRING4, modelDescription, Entity.TYPE0, true));
    }

    @DisplayName("Test 5")
    @Test
    public void test5() {
        assertPrimitiveExpressionBuilder(expressionsProcessor, PRIMITIVE_EXPRESSION_STRING5, new StringPrimitiveExpressionBuilder(PRIMITIVE_EXPRESSION_STRING5, modelDescription, Entity.TYPE0, Product.TYPE0));
    }

    @DisplayName("Test 6")
    @Test
    public void test6() {
        assertPrimitiveExpressionBuilder(expressionsProcessor, PRIMITIVE_EXPRESSION_STRING6, new StringPrimitiveExpressionBuilder(PRIMITIVE_EXPRESSION_STRING6, modelDescription, Entity.TYPE0));
    }

    @DisplayName("Test 7")
    @Test
    public void test7() {
        assertPrimitiveExpressionBuilder(expressionsProcessor, PRIMITIVE_EXPRESSION_STRING7, new StringPrimitiveExpressionBuilder(PRIMITIVE_EXPRESSION_STRING7, modelDescription, Product.TYPE0));
    }

    @DisplayName("Test 8")
    @Test
    public void test8() {
        assertPrimitiveExpressionBuilder(expressionsProcessor, "root.$id", new StringPrimitiveExpressionBuilder("root{}.$id", modelDescription, Product.TYPE0));
    }

    @DisplayName("Test 9")
    @Test
    public void test9() {
        assertPrimitiveExpressionBuilder(expressionsProcessor, PRIMITIVE_EXPRESSION_STRING8, new StringPrimitiveExpressionBuilder(PRIMITIVE_EXPRESSION_STRING8, modelDescription, Operation.TYPE0));
    }

    @DisplayName("Test 10")
    @Test
    public void test10() {
        assertPrimitiveExpressionBuilder(expressionsProcessor, PRIMITIVE_EXPRESSION_STRING9, new StringPrimitiveExpressionBuilder(PRIMITIVE_EXPRESSION_STRING9, modelDescription, Operation.TYPE0, ActionSpecial.TYPE0)
                .setAliasedEntityDescription("action", ActionSpecial.TYPE0));
    }

    @DisplayName("Test 11")
    @Test
    public void test11() {
        assertPrimitiveExpressionBuilder(expressionsProcessor, PRIMITIVE_EXPRESSION_STRING10, new StringPrimitiveExpressionBuilder(PRIMITIVE_EXPRESSION_STRING10, modelDescription, TestEntity.TYPE0));
    }

    @DisplayName("Test 12")
    @Test
    public void test12() {
        assertPrimitiveExpressionBuilder(expressionsProcessor, PRIMITIVE_EXPRESSION_STRING11, new StringPrimitiveExpressionBuilder(PRIMITIVE_EXPRESSION_STRING11, modelDescription, TestEntity.TYPE0));
    }

    @DisplayName("Test 13")
    @Test
    public void test13() {
        assertPrimitiveExpressionBuilder(expressionsProcessor, PRIMITIVE_EXPRESSION_STRING12, new StringPrimitiveExpressionBuilder(PRIMITIVE_EXPRESSION_STRING12, modelDescription, TestEntity.TYPE0));
    }

    @DisplayName("Test 14")
    @Test
    public void test14() {
        assertPrimitiveExpressionBuilder(expressionsProcessor, "now", new StringPrimitiveExpressionBuilder("now", modelDescription, TestEntity.TYPE0));
    }

    @DisplayName("Test 15")
    @Test
    public void test15() {
        assertPrimitiveExpressionBuilder(expressionsProcessor, "('1'==any(root.aliases)&&'1'!=any(['1','2','3'])&&'1'>all(root.aliases)&&'1'<=all(['1','2','3'])).$asBoolean", new StringPrimitiveExpressionBuilder("('1'==any(root.aliases)&&'1'!=any(['1','2','3'])&&'1'>all(root.aliases)&&'1'<=all(['1','2','3'])).$asBoolean", modelDescription, Product.TYPE0));
    }

@DisplayName("Test for exception 'Expression is not a primitive expression'")
    @Test
    public void notPrimitiveExpressionExceptionTest() {
        assertThrowsCausedBy(NotPrimitiveExpressionException.class, () -> new StringPrimitiveExpressionBuilder("root", modelDescription, Entity.TYPE0).build(expressionsProcessor));
    }
}
