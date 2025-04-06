package sbp.com.sbt.dataspace.feather.stringexpressions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sbp.com.sbt.dataspace.feather.expressions.ExpressionsProcessor;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.testmodel.ActionSpecial;
import sbp.com.sbt.dataspace.feather.testmodel.Entity;
import sbp.com.sbt.dataspace.feather.testmodel.Operation;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.ProductLimited;
import sbp.com.sbt.dataspace.feather.testmodel.ProductPlus;
import sbp.com.sbt.dataspace.feather.testmodel.Service;
import sbp.com.sbt.dataspace.feather.testmodel.Test2Entity;
import sbp.com.sbt.dataspace.feather.testmodel.TestEntity;

import static sbp.com.sbt.dataspace.feather.stringexpressions.TestHelper.assertConditionBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestHelper.assertThrowsCausedBy;

@DisplayName("Testing the condition builder based on a string")
@SpringJUnitConfig(StringExpressionsTestConfiguration.class)
public class StringConditionBuilderTest {

    @Autowired
    ModelDescription modelDescription;
    @Autowired
    @Qualifier(StringExpressionsConfiguration.EXPRESSIONS_PROCESSOR_BEAN_NAME)
    ExpressionsProcessor expressionsProcessor;

    @DisplayName("Test 1")
    @Test
    public void test1() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING1, new StringConditionBuilder(TestHelper.CONDITION_STRING1, modelDescription, Entity.TYPE0));
    }

    @DisplayName("Test 2")
    @Test
    public void test2() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING2, new StringConditionBuilder(TestHelper.CONDITION_STRING2, modelDescription, Product.TYPE0));
    }

    @DisplayName("Test 3")
    @Test
    public void test3() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING3, new StringConditionBuilder(TestHelper.CONDITION_STRING3, modelDescription, Entity.TYPE0));
    }

    @DisplayName("Test 4")
    @Test
    public void test4() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING4, new StringConditionBuilder(TestHelper.CONDITION_STRING4, modelDescription, Entity.TYPE0));
    }

    @DisplayName("Test 5")
    @Test
    public void test5() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING5, new StringConditionBuilder(TestHelper.CONDITION_STRING5, modelDescription, ProductLimited.TYPE0));
    }

    @DisplayName("Test 6")
    @Test
    public void test6() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING6, new StringConditionBuilder(TestHelper.CONDITION_STRING6, modelDescription, Entity.TYPE0));
    }

    @DisplayName("Test 7")
    @Test
    public void test7() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING7, new StringConditionBuilder(TestHelper.CONDITION_STRING7, modelDescription, Product.TYPE0));
    }

    @DisplayName("Test 8")
    @Test
    public void test8() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING8, new StringConditionBuilder(TestHelper.CONDITION_STRING8, modelDescription, Product.TYPE0));
    }

    @DisplayName("Test 9")
    @Test
    public void test9() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING9, new StringConditionBuilder(TestHelper.CONDITION_STRING9, modelDescription, Product.TYPE0, true));
    }

    @DisplayName("Test 10")
    @Test
    public void test10() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING10, new StringConditionBuilder(TestHelper.CONDITION_STRING10, modelDescription, Product.TYPE0, Service.TYPE0));
    }

    @DisplayName("Test 11")
    @Test
    public void test11() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING11, new StringConditionBuilder(TestHelper.CONDITION_STRING11, modelDescription, TestEntity.TYPE0));
    }

    @DisplayName("Test 12")
    @Test
    public void test12() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING12, new StringConditionBuilder(TestHelper.CONDITION_STRING12, modelDescription, TestEntity.TYPE0));
    }

    @DisplayName("Test 13")
    @Test
    public void test13() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING13, new StringConditionBuilder(TestHelper.CONDITION_STRING13, modelDescription, Operation.TYPE0));
    }

    @DisplayName("Test 14")
    @Test
    public void test14() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING14, new StringConditionBuilder(TestHelper.CONDITION_STRING14, modelDescription, Operation.TYPE0, ActionSpecial.TYPE0)
            .setAliasedEntityDescription("action", ActionSpecial.TYPE0));
    }

    @DisplayName("Test 15")
    @Test
    public void test15() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING15, new StringConditionBuilder(TestHelper.CONDITION_STRING15, modelDescription, Operation.TYPE0));
    }

    @DisplayName("Test 16")
    @Test
    public void test16() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING16, new StringConditionBuilder(TestHelper.CONDITION_STRING16, modelDescription, TestEntity.TYPE0));
    }

    @DisplayName("Test 17")
    @Test
    public void test17() {
        assertConditionBuilder(expressionsProcessor, "root.p7==D2020-10-05&&root.code_2=='123'&&root.$id==123+15.$abs+19.12", new StringConditionBuilder("root.p7 == D2020-10-05 && root.code_2 == '123' && root.$id ==\n123\t+\f15.$abs\r+19.12", modelDescription, TestEntity.TYPE0));
    }

    @DisplayName("Test 18")
    @Test
    public void test18() {
        assertConditionBuilder(expressionsProcessor, "root.p7==D2020-10-05", new StringConditionBuilder("root.p7 == D2020-10-05", modelDescription, TestEntity.TYPE0));
    }

    @DisplayName("Test 19")
    @Test
    public void test19() {
        assertConditionBuilder(expressionsProcessor, "'name'$inroot.services.request.initiator.firstName", new StringConditionBuilder("'name' $in root.services.request.initiator.firstName", modelDescription, Product.TYPE0));
    }

    @DisplayName("Test 20")
    @Test
    public void test20() {
        assertConditionBuilder(expressionsProcessor, "root.p7==D2020-10-05T17:47:23.123", new StringConditionBuilder("it.p7 == D2020-10-05T17:47:23.123", modelDescription, TestEntity.TYPE0));
    }

    @DisplayName("Test 21")
    @Test
    public void test21() {
        assertConditionBuilder(expressionsProcessor, "root.p7==D2020-10-05T17:47:23", new StringConditionBuilder("it.p7 == D2020-10-05T17:47:23", modelDescription, TestEntity.TYPE0));
    }

    @DisplayName("Test 22")
    @Test
    public void test22() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING17, new StringConditionBuilder(TestHelper.CONDITION_STRING17, modelDescription, Product.TYPE0));
    }

    @DisplayName("Test 23")
    @Test
    public void test23() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING18, new StringConditionBuilder(TestHelper.CONDITION_STRING18, modelDescription, Product.TYPE0));
    }

    @DisplayName("Test 24")
    @Test
    public void test24() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING19, new StringConditionBuilder(TestHelper.CONDITION_STRING19, modelDescription, Product.TYPE0));
    }

    @DisplayName("Test 25")
    @Test
    public void test25() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING20, new StringConditionBuilder(TestHelper.CONDITION_STRING20, modelDescription, Product.TYPE0));
    }

    @DisplayName("Test 26")
    @Test
    public void test26() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING21, new StringConditionBuilder(TestHelper.CONDITION_STRING21, modelDescription, Product.TYPE0));
    }

    @DisplayName("Test 27")
    @Test
    public void test27() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING22, new StringConditionBuilder(TestHelper.CONDITION_STRING22, modelDescription, Product.TYPE0));
    }

    @DisplayName("Test 28")
    @Test
    public void test28() {
        assertConditionBuilder(expressionsProcessor, "root.$id!=null", new StringConditionBuilder("it.id != null", modelDescription, TestEntity.TYPE0));
    }

    @DisplayName("Test 29")
    @Test
    public void test29() {
        assertConditionBuilder(expressionsProcessor, "root.services.$id.$exists", new StringConditionBuilder("it.services.id.$exists", modelDescription, Product.TYPE0));
    }

    @DisplayName("Test 30")
    @Test
    public void test30() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING23, new StringConditionBuilder(TestHelper.CONDITION_STRING23, modelDescription, Test2Entity.TYPE0));
    }

    @DisplayName("Test 31")
    @Test
    public void test31() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING24, new StringConditionBuilder(TestHelper.CONDITION_STRING24, modelDescription, Test2Entity.TYPE0));
    }

    @DisplayName("Test 32")
    @Test
    public void test32() {
        assertConditionBuilder(expressionsProcessor, "3.0E+71!=null", new StringConditionBuilder("3.0E71 != null", modelDescription, Test2Entity.TYPE0));
    }

    @DisplayName("Test 33")
    @Test
    public void test33() {
        assertConditionBuilder(expressionsProcessor, "3.0E+71+8!=null", new StringConditionBuilder("3.0E+71 + 8 != null", modelDescription, Test2Entity.TYPE0));
    }

    @DisplayName("Test 34")
    @Test
    public void test34() {
        assertConditionBuilder(expressionsProcessor, "3.0E-7!=null", new StringConditionBuilder("3.0E-7 != null", modelDescription, Test2Entity.TYPE0));
    }

    @DisplayName("Test 35")
    @Test
    public void test35() {
        assertConditionBuilder(expressionsProcessor, "5!=3.0E-71", new StringConditionBuilder("5 != 3.0E-71", modelDescription, Test2Entity.TYPE0));
    }

    @DisplayName("Test 36")
    @Test
    public void test36() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING25, new StringConditionBuilder(TestHelper.CONDITION_STRING25, modelDescription, TestEntity.TYPE0));
    }

    @DisplayName("Test 37")
    @Test
    public void test37() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING26, new StringConditionBuilder(TestHelper.CONDITION_STRING26, modelDescription, TestEntity.TYPE0));
    }

    @DisplayName("Test 38")
    @Test
    public void test38() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING27, new StringConditionBuilder(TestHelper.CONDITION_STRING27, modelDescription, TestEntity.TYPE0));
    }

    @DisplayName("Test 39")
    @Test
    public void test39() {
        assertConditionBuilder(expressionsProcessor, "root.p7.$time==T13:11:00", new StringConditionBuilder("it.p7.$time == T13:11", modelDescription, TestEntity.TYPE0));
    }

    @DisplayName("Test 40")
    @Test
    public void test40() {
        assertConditionBuilder(expressionsProcessor, "root.p1==T13:11:00.$asString", new StringConditionBuilder("it.p1 == T13:11.$asString", modelDescription, TestEntity.TYPE0));
    }

    @DisplayName("Test 41")
    @Test
    public void test41() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING28, new StringConditionBuilder(TestHelper.CONDITION_STRING28, modelDescription, TestEntity.TYPE0));
    }

    @DisplayName("Test 42")
    @Test
    public void test42() {
        assertConditionBuilder(expressionsProcessor, "1%2==3", new StringConditionBuilder("1 $mod 2 == 3", modelDescription, TestEntity.TYPE0));
    }

    @DisplayName("Test 43")
    @Test
    public void test43() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING29, new StringConditionBuilder(TestHelper.CONDITION_STRING29, modelDescription, Product.TYPE0));
    }

    @DisplayName("Test 44")
    @Test
    public void test44() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING30, new StringConditionBuilder(TestHelper.CONDITION_STRING30, modelDescription, Product.TYPE0));
    }

    @DisplayName("Test 45")
    @Test
    public void test45() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING31, new StringConditionBuilder(TestHelper.CONDITION_STRING31, modelDescription, TestEntity.TYPE0));
    }

    @DisplayName("Test for the exception 'Unexpected end of line'")
    @Test
    public void unexpectedEndOfStringExceptionTest() {
        assertThrowsCausedBy(UnexpectedEndOfStringException.class, () -> new StringConditionBuilder("root.$id == 'Hello, ", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Expected digit'")
    @Test
    public void digitExpectedExceptionTest() {
        assertThrowsCausedBy(DigitExpectedException.class, () -> new StringConditionBuilder("root.p7 == D2020-05-1a", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Unexpected symbol'")
    @Test
    public void unexpectedCharacterExceptionTest() {
        assertThrowsCausedBy(UnexpectedCharacterException.class, () -> new StringConditionBuilder("root.p7 == D2020.05.10", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Unexpected symbol' (2)")
    @Test
    public void unexpectedCharacterExceptionTest2() {
        assertThrowsCausedBy(UnexpectedCharacterException.class, () -> new StringConditionBuilder("root.p7 == D2020-05-10T17:55:10.123:08:00", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Token not found'")
    @Test
    public void tokenNotFoundExceptionTest() {
        assertThrowsCausedBy(TokenNotFoundException.class, () -> new StringConditionBuilder("root{type=} == null", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Token not found' (2)")
    @Test
    public void tokenNotFoundExceptionTest2() {
        assertThrowsCausedBy(TokenNotFoundException.class, () -> new StringConditionBuilder("3.0E+", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Token not found' (3)")
    @Test
    public void tokenNotFoundExceptionTest3() {
        assertThrowsCausedBy(TokenNotFoundException.class, () -> new StringConditionBuilder("3.0E+x", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Token not found' (4)")
    @Test
    public void tokenNotFoundExceptionTest4() {
        assertThrowsCausedBy(TokenNotFoundException.class, () -> new StringConditionBuilder("3.0Ex", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'End of line was expected'")
    @Test
    public void endOfStringExpectedExceptionTest() {
        assertThrowsCausedBy(EndOfStringExpectedException.class, () -> new StringConditionBuilder("root.$id == '123'?", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Expression is not a condition'")
    @Test
    public void notConditionExceptionTest() {
        assertThrowsCausedBy(NotConditionException.class, () -> new StringConditionBuilder("root", modelDescription, Entity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Setting the specification \"Type\" must be declared first'")
    @Test
    public void typeSpecificationSettingShouldBeFirstExceptionTest() {
        assertThrowsCausedBy(TypeSpecificationSettingShouldBeFirstException.class, () -> new StringConditionBuilder("root.services{cond = it.code == 'service1', type = Service}.$exists", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Setting specification type \"Type\" must be declared first' (2)")
    @Test
    public void typeSpecificationSettingShouldBeFirstExceptionTest2() {
        assertThrowsCausedBy(TypeSpecificationSettingShouldBeFirstException.class, () -> new StringConditionBuilder("entities{cond = it.code != null}.$exists", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Setting the specification type should be declared first' (3)")
    @Test
    public void typeSpecificationSettingShouldBeFirstExceptionTest3() {
        assertThrowsCausedBy(TypeSpecificationSettingShouldBeFirstException.class, () -> new StringConditionBuilder("entities{}.$exists", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Setting specification type \"Type\" must be declared first' (4)")
    @Test
    public void typeSpecificationSettingShouldBeFirstExceptionTest4() {
        assertThrowsCausedBy(TypeSpecificationSettingShouldBeFirstException.class, () -> new StringConditionBuilder("entities{elemAlias=alias}.$exists", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Setting specification \"Condition\" must be declared last'")
    @Test
    public void condSpecificationSettingShouldBeLastExceptionTest() {
        assertThrowsCausedBy(CondSpecificationSettingShouldBeLastException.class, () -> new StringConditionBuilder("root.services{cond = it.code == 'service1', elemAlias = service}.$exists", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Attempt to get element outside collection'")
    @Test
    public void getElementOutsideOfCollectionExceptionTest() {
        assertThrowsCausedBy(GetElementOutsideOfCollectionException.class, () -> new StringConditionBuilder("elem.code == 'service1'", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Attempt to get element outside collection' (2)")
    @Test
    public void getElementOutsideOfCollectionExceptionTest2() {
        assertThrowsCausedBy(GetElementOutsideOfCollectionException.class, () -> new StringConditionBuilder("elem == 'alias1'", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Duplicate settings specification found'")
        @Test
        public void duplicateSpecificationSettingExceptionTest(){
        assertThrowsCausedBy(DuplicateSpecificationSettingException.class, () -> new StringConditionBuilder("root{type = ProductPlus, type = ProductLimited}.$id == '1'", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Invalid property access'")
    @Test
    public void disallowedPropertyAccessExceptionTest() {
        assertThrowsCausedBy(DisallowedPropertyAccessException.class, () -> new StringConditionBuilder("root.code.code == 1", modelDescription, Entity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Invalid property access' (2)")
    @Test
    public void disallowedPropertyAccessExceptionTest2() {
        assertThrowsCausedBy(DisallowedPropertyAccessException.class, () -> new StringConditionBuilder("root.aliases.code == 1", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Invalid property access' (3)")
    @Test
    public void disallowedPropertyAccessExceptionTest3() {
        assertThrowsCausedBy(DisallowedPropertyAccessException.class, () -> new StringConditionBuilder("root.affectedProducts.aliases == 1", modelDescription, ProductPlus.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Invalid property access' (4)")
    @Test
    public void disallowedPropertyAccessExceptionTest4() {
        assertThrowsCausedBy(DisallowedPropertyAccessException.class, () -> new StringConditionBuilder("root.affectedProducts.services == 1", modelDescription, ProductPlus.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Invalid property access' (5)")
    @Test
    public void disallowedPropertyAccessExceptionTest5() {
        assertThrowsCausedBy(DisallowedPropertyAccessException.class, () -> new StringConditionBuilder("root.affectedProducts.parameters == 1", modelDescription, ProductPlus.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Property not found'")
    @Test
    public void propertyNotFoundExceptionTest() {
        assertThrowsCausedBy(PropertyNotFoundException.class, () -> new StringConditionBuilder("root.nonexistentProperty", modelDescription, Entity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Property not found' (2)")
    @Test
    public void propertyNotFoundExceptionTest2() {
        assertThrowsCausedBy(PropertyNotFoundException.class, () -> new StringConditionBuilder("root.parameters.nonexistentProperty", modelDescription, Entity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Property not found' (3)")
    @Test
    public void propertyNotFoundExceptionTest3() {
        assertThrowsCausedBy(PropertyNotFoundException.class, () -> new StringConditionBuilder("root.request.initiator.nonexistentProperty", modelDescription, Entity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Property not found' (4)")
    @Test
    public void propertyNotFoundExceptionTest4() {
        assertThrowsCausedBy(PropertyNotFoundException.class, () -> new StringConditionBuilder("root.parameters.request.initiator.nonexistentProperty", modelDescription, Entity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Unexpected collection element specification of primitive values'")
    @Test
    public void unexpectedPrimitiveExpressionsCollectionSpecificationExceptionTest() {
        assertThrowsCausedBy(UnexpectedPrimitiveExpressionsCollectionSpecificationException.class, () -> new StringConditionBuilder("it{type = Product} == '1'", modelDescription, Entity.TYPE0, true).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Method not found'")
    @Test
    public void methodNotFoundExceptionTest() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root == null || 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Method not found' (2)")
    @Test
    public void methodNotFoundExceptionTest2() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("1 || root == null", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (3)")
    @Test
    public void methodNotFoundExceptionTest3() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root == null && 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (4)")
    @Test
    public void methodNotFoundExceptionTest4() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("1 && root == null", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (5)")
    @Test
    public void methodNotFoundExceptionTest5() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("! 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (6)")
    @Test
    public void methodNotFoundExceptionTest6() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$exists + 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (7)")
    @Test
    public void methodNotFoundExceptionTest7() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("1 + root.rc1.$exists", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (8)")
    @Test
    public void methodNotFoundExceptionTest8() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$exists * 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Method not found' (9)")
    @Test
    public void methodNotFoundExceptionTest9() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("1 * root.rc1.$exists", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (10)")
    @Test
    public void methodNotFoundExceptionTest10() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("-root.rc1.$exists", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (11)")
    @Test
    public void methodNotFoundExceptionTest11() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$upper == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (12)")
    @Test
    public void methodNotFoundExceptionTest12() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$lower == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (13)")
    @Test
    public void methodNotFoundExceptionTest13() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$length == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (14)")
    @Test
    public void methodNotFoundExceptionTest14() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$trim == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (15)")
    @Test
    public void methodNotFoundExceptionTest15() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$ltrim == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (16)")
    @Test
    public void methodNotFoundExceptionTest16() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$rtrim == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (17)")
    @Test
    public void methodNotFoundExceptionTest17() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$round == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (18)")
    @Test
    public void methodNotFoundExceptionTest18() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$ceil == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (19)")
    @Test
    public void methodNotFoundExceptionTest19() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$floor == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (20)")
    @Test
    public void methodNotFoundExceptionTest20() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$hash == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (21)")
    @Test
    public void methodNotFoundExceptionTest21() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$asString == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Method not found' (22)")
    @Test
    public void methodNotFoundExceptionTest22() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$asBigDecimal == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Method not found' (23)")
    @Test
    public void methodNotFoundExceptionTest23() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$abs == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (24)")
    @Test
    public void methodNotFoundExceptionTest24() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$substr(1) == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Method not found' (25)")
    @Test
    public void methodNotFoundExceptionTest25() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$substr(1, 2) == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Method not found' (26)")
    @Test
    public void methodNotFoundExceptionTest26() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$replace(1, 2) == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Method not found' (27)")
    @Test
    public void methodNotFoundExceptionTest27() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$addMilliseconds(1) == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Method not found' (28)")
    @Test
    public void methodNotFoundExceptionTest28() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$addSeconds(1) == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Method not found' (29)")
    @Test
    public void methodNotFoundExceptionTest29() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$addMinutes(1) == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (30)")
    @Test
    public void methodNotFoundExceptionTest30() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$addHours(1) == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (31)")
    @Test
    public void methodNotFoundExceptionTest31() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$addDays(1) == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (32)")
    @Test
    public void methodNotFoundExceptionTest32() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$addMonths(1) == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (33)")
    @Test
    public void methodNotFoundExceptionTest33() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$addYears(1) == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (34)")
    @Test
    public void methodNotFoundExceptionTest34() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$subMilliseconds(1) == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (35)")
    @Test
    public void methodNotFoundExceptionTest35() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$subSeconds(1) == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (36)")
    @Test
    public void methodNotFoundExceptionTest36() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$subMinutes(1) == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (37)")
    @Test
    public void methodNotFoundExceptionTest37() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$subHours(1) == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (38)")
    @Test
    public void methodNotFoundExceptionTest38() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$subDays(1) == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (39)")
    @Test
    public void methodNotFoundExceptionTest39() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$subMonths(1) == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (40)")
    @Test
    public void methodNotFoundExceptionTest40() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1.$subYears(1) == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (41)")
    @Test
    public void methodNotFoundExceptionTest41() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.code.$type == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (42)")
    @Test
    public void methodNotFoundExceptionTest42() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.code.$id == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (43)")
    @Test
    public void methodNotFoundExceptionTest43() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.r1.$min == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Method not found' (44)")
    @Test
    public void methodNotFoundExceptionTest44() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.r1.$max == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (45)")
    @Test
    public void methodNotFoundExceptionTest45() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.r1.$sum == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (46)")
    @Test
    public void methodNotFoundExceptionTest46() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.r1.$avg == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (47)")
    @Test
    public void methodNotFoundExceptionTest47() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.r1.$count == 1", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (48)")
    @Test
    public void methodNotFoundExceptionTest48() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.code.$exists", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Method not found' (49)")
    @Test
    public void methodNotFoundExceptionTest49() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.rc1 $between (1, 2)", modelDescription, TestEntity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (50)")
    @Test
    public void methodNotFoundExceptionTest50() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.code $in root.services", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (51)")
    @Test
    public void methodNotFoundExceptionTest51() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.relatedProduct $in root.aliases", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (52)")
    @Test
    public void methodNotFoundExceptionTest52() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.code $in [root.code, root.relatedProduct]", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (53)")
    @Test
    public void methodNotFoundExceptionTest53() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.relatedProduct $in [root.relatedProduct, root.code]", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (54)")
    @Test
    public void methodNotFoundExceptionTest54() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.code $in [root.relatedProduct, root.relatedProduct]", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (55)")
    @Test
    public void methodNotFoundExceptionTest55() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.relatedProduct $in [root.code, root.code]", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (56)")
    @Test
    public void methodNotFoundExceptionTest56() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.code > null", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (57)")
    @Test
    public void methodNotFoundExceptionTest57() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.relatedProduct > null", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (58)")
    @Test
    public void methodNotFoundExceptionTest58() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.services == null", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (59)")
    @Test
    public void methodNotFoundExceptionTest59() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.services.$exists > 1", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (60)")
    @Test
    public void methodNotFoundExceptionTest60() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("1 > root.services.$exists", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (61)")
    @Test
    public void methodNotFoundExceptionTest61() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.relatedProduct == root.services", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (62)")
    @Test
    public void methodNotFoundExceptionTest62() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.relatedProduct > root.relatedProduct", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Method not found' (63)")
    @Test
    public void methodNotFoundExceptionTest63() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.request.initiator > null", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (64)")
    @Test
    public void methodNotFoundExceptionTest64() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.code.$asBoolean", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (65)")
    @Test
    public void methodNotFoundExceptionTest65() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("root.code.$map(it)", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (66)")
    @Test
    public void methodNotFoundExceptionTest66() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it.code.id", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (67)")
    @Test
    public void methodNotFoundExceptionTest67() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it.$asDate", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (68)")
    @Test
    public void methodNotFoundExceptionTest68() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it.$asDateTime", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (69)")
    @Test
    public void methodNotFoundExceptionTest69() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it.$asOffsetDateTime", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (70)")
    @Test
    public void methodNotFoundExceptionTest70() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it.$year", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (71)")
    @Test
    public void methodNotFoundExceptionTest71() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it.$month", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (72)")
    @Test
    public void methodNotFoundExceptionTest72() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it.$day", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (73)")
    @Test
    public void methodNotFoundExceptionTest73() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it.$hour", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (74)")
    @Test
    public void methodNotFoundExceptionTest74() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it.$minute", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (75)")
    @Test
    public void methodNotFoundExceptionTest75() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it.$second", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (76)")
    @Test
    public void methodNotFoundExceptionTest76() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it.$offsetHour", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (77)")
    @Test
    public void methodNotFoundExceptionTest77() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it.$offsetMinute", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (78)")
    @Test
    public void methodNotFoundExceptionTest78() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it.$asTime", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (79)")
    @Test
    public void methodNotFoundExceptionTest79() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it.$date", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (80)")
    @Test
    public void methodNotFoundExceptionTest80() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it.$time", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (81)")
    @Test
    public void methodNotFoundExceptionTest81() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it.$offset", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (82)")
    @Test
    public void methodNotFoundExceptionTest82() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it | 1", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (83)")
    @Test
    public void methodNotFoundExceptionTest83() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it & 1", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (84)")
    @Test
    public void methodNotFoundExceptionTest84() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it ^ 1", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (85)")
    @Test
    public void methodNotFoundExceptionTest85() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("~it", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (86)")
    @Test
    public void methodNotFoundExceptionTest86() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it >> 1", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (87)")
    @Test
    public void methodNotFoundExceptionTest87() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it << 1", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (88)")
    @Test
    public void methodNotFoundExceptionTest88() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it.$lpad(1, 1)", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (89)")
    @Test
    public void methodNotFoundExceptionTest89() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it.$rpad(1, 1)", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (90)")
    @Test
    public void methodNotFoundExceptionTest90() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("1 | it", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (91)")
    @Test
    public void methodNotFoundExceptionTest91() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("1 & it", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (92)")
    @Test
    public void methodNotFoundExceptionTest92() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("1 ^ it", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (93)")
    @Test
    public void methodNotFoundExceptionTest93() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("1 >> it", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Method not found' (94)")
    @Test
    public void methodNotFoundExceptionTest94() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("1 << it", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (95)")
    @Test
    public void methodNotFoundExceptionTest95() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("'1' $like any(['1'])", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Method not found' (96)")
    @Test
    public void methodNotFoundExceptionTest96() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it.$power(1)", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (97)")
    @Test
    public void methodNotFoundExceptionTest97() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it.$log(1)", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Method not found' (98)")
    @Test
    public void methodNotFoundExceptionTest98() {
        assertThrowsCausedBy(MethodNotFoundException.class, () -> new StringConditionBuilder("it.$dateTime", modelDescription, Product.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Unknown alias'")
    @Test
    public void unknownAliasExceptionTest() {
        assertThrowsCausedBy(UnknownAliasException.class, () -> new StringConditionBuilder("root == @product", modelDescription, Entity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Duplicate alias found'")
    @Test
    public void duplicateAliasFoundExceptionTest() {
        assertThrowsCausedBy(DuplicateAliasFoundException.class, () -> new StringConditionBuilder("root.parameters{elemAlias=parameter,cond=it.parameters{elemAlias=parameter}.$exists}.$exists", modelDescription, Entity.TYPE0).build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Expression is not a collection of primitive expressions'")
    @Test
    public void notPrimitiveExpressionsCollectionExceptionTest() {
        assertThrowsCausedBy(NotPrimitiveExpressionsCollectionException.class, () -> new StringConditionBuilder("1==any(1)", modelDescription, Entity.TYPE0).build(expressionsProcessor));
    }
}
