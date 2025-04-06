package sbp.com.sbt.dataspace.feather.stringexpressions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sbp.com.sbt.dataspace.feather.common.Pointer;
import sbp.com.sbt.dataspace.feather.expressions.Condition;
import sbp.com.sbt.dataspace.feather.expressions.ConditionBuilder;
import sbp.com.sbt.dataspace.feather.expressions.ConditionalGroup;
import sbp.com.sbt.dataspace.feather.expressions.ExpressionsProcessor;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpression;
import sbp.com.sbt.dataspace.feather.testmodel.Action;
import sbp.com.sbt.dataspace.feather.testmodel.ActionParameter;
import sbp.com.sbt.dataspace.feather.testmodel.ActionParameterSpecial;
import sbp.com.sbt.dataspace.feather.testmodel.ActionSpecial;
import sbp.com.sbt.dataspace.feather.testmodel.Agreement;
import sbp.com.sbt.dataspace.feather.testmodel.Document;
import sbp.com.sbt.dataspace.feather.testmodel.Entity;
import sbp.com.sbt.dataspace.feather.testmodel.Operation;
import sbp.com.sbt.dataspace.feather.testmodel.OperationLimited;
import sbp.com.sbt.dataspace.feather.testmodel.Permission;
import sbp.com.sbt.dataspace.feather.testmodel.Person;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.ProductLimited;
import sbp.com.sbt.dataspace.feather.testmodel.Request;
import sbp.com.sbt.dataspace.feather.testmodel.Service;
import sbp.com.sbt.dataspace.feather.testmodel.Test2Entity;
import sbp.com.sbt.dataspace.feather.testmodel.TestEntity;
import sbp.com.sbt.dataspace.feather.testmodel.UserProduct;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.wrap;
import static sbp.com.sbt.dataspace.feather.stringexpressions.TestHelper.assertConditionBuilder;

@DisplayName("Testing the expression handler based on a string")
@SpringJUnitConfig(StringExpressionsTestConfiguration.class)
public class StringExpressionsProcessorTest {

    @Autowired
    @Qualifier(StringExpressionsConfiguration.EXPRESSIONS_PROCESSOR_BEAN_NAME)
    ExpressionsProcessor expressionsProcessor;

    @DisplayName("Checking overridden methods")
    @Test
    public void overrodeMethodsCheck() {
        Arrays.stream(ExpressionsProcessor.class.getMethods()).forEach(conditionsProcessorMethod -> assertNotNull(wrap(() -> StringExpressionsProcessor.class.getDeclaredMethod(conditionsProcessorMethod.getName(), conditionsProcessorMethod.getParameterTypes()))));
    }

    @DisplayName("Test for converting primitive values to strings")
    @Test
    public void testPrimitiveValueToStringConversations() {
        assertEquals("'A'", expressionsProcessor.prim('A').toString());
        assertEquals("''''", expressionsProcessor.prim('\'').toString());
        assertEquals("'I''m the test!'", expressionsProcessor.prim("I'm the test!").toString());
        assertEquals("127", expressionsProcessor.prim(Byte.MAX_VALUE).toString());
        assertEquals("-128", expressionsProcessor.prim(Byte.MIN_VALUE).toString());
        assertEquals("32767", expressionsProcessor.prim(Short.MAX_VALUE).toString());
        assertEquals("-32768", expressionsProcessor.prim(Short.MIN_VALUE).toString());
        assertEquals("2147483647", expressionsProcessor.prim(Integer.MAX_VALUE).toString());
        assertEquals("-2147483648", expressionsProcessor.prim(Integer.MIN_VALUE).toString());
        assertEquals("9223372036854775807", expressionsProcessor.prim(Long.MAX_VALUE).toString());
        assertEquals("-9223372036854775808", expressionsProcessor.prim(Long.MIN_VALUE).toString());
        assertEquals("0.1234567", expressionsProcessor.prim(0.1234567F).toString());
        assertEquals("-0.1234567", expressionsProcessor.prim(-0.1234567F).toString());
        assertEquals("123.4567", expressionsProcessor.prim(123.4567F).toString());
        assertEquals("-123.4567", expressionsProcessor.prim(-123.4567F).toString());
        assertEquals("1234567.0", expressionsProcessor.prim(1234567F).toString());
        assertEquals("-1234567.0", expressionsProcessor.prim(-1234567F).toString());
        assertEquals("1234567.890123456", expressionsProcessor.prim(1234567.890123456).toString());
        assertEquals("-1234567.890123456", expressionsProcessor.prim(-1234567.890123456).toString());
        assertEquals("0.1234567890123456", expressionsProcessor.prim(0.1234567890123456).toString());
        assertEquals("-0.1234567890123456", expressionsProcessor.prim(-0.1234567890123456).toString());
        assertEquals("1.23456789012345E14", expressionsProcessor.prim(123456789012345.0).toString());
        assertEquals("-1.23456789012345E14", expressionsProcessor.prim(-123456789012345.0).toString());
        assertEquals("92233720368547758079223372036854775807.92233720368547758079223372036854775807", expressionsProcessor.prim(new BigDecimal("92233720368547758079223372036854775807.92233720368547758079223372036854775807")).toString());
        assertEquals("-92233720368547758079223372036854775807.92233720368547758079223372036854775807", expressionsProcessor.prim(new BigDecimal("-92233720368547758079223372036854775807.92233720368547758079223372036854775807")).toString());
        assertEquals("3.0E+7", expressionsProcessor.prim(BigDecimal.valueOf(30000000.0)).toString());
        assertEquals("D2020-03-05", expressionsProcessor.prim(LocalDate.of(2020, 3, 5)).toString());
        assertEquals("D2020-03-05T10:12:34.567", expressionsProcessor.prim(LocalDateTime.of(2020, 3, 5, 10, 12, 34, 567000000)).toString());
        assertEquals("D2020-03-05T10:12:34.567+06:00", expressionsProcessor.prim(OffsetDateTime.of(2020, 3, 5, 10, 12, 34, 567000000, ZoneOffset.of("+06:00"))).toString());
        assertEquals("true", expressionsProcessor.prim(true).toString());
        assertEquals("false", expressionsProcessor.prim(false).toString());
    }

    @DisplayName("Test 1")
    @Test
    public void test1() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING1, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().id().eq(prim("123"));
            }
        });
    }

    @DisplayName("Test 2")
    @Test
    public void test2() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING2, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().prim(Entity.NAME).like(prim("product%"))
                    .and(prim(-123).minus(prim(7).neg()).plus(prim(8)).neg().mul(prim(10)).plus(prim(4).div(prim(3)).div(prim(5).div(prim(6))))
                            .gt(prim(7).div(prim(2)).minus(root().prim(Product.CREATOR_CODE))),
                        root().prim(Entity.CODE).isNotNull(),
                        prim(-123).neg().eq(prim(123)),
                        root().prim(Product.CREATOR_CODE).gt(prim(new BigDecimal("9223372036854775808"))));
            }
        });
    }

    @DisplayName("Test 3")
    @Test
    public void test3() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING3, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().prim(Entity.NAME).eq(prim("product1"))
                    .and(root(specification -> specification.setType(Service.TYPE0)).prim(Service.MANAGER_PERSONAL_CODE).between(prim(1), prim(3)));
            }
        });
    }

    @DisplayName("Test 4")
    @Test
    public void test4() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING4, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().prim(Entity.NAME).eq(prim("product1"))
                    .and(root().type().like(prim("Product%")));
            }
        });
    }

    @DisplayName("Test 5")
    @Test
    public void test5() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING5, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root(specification -> specification.setType(ProductLimited.TYPE0)).prim(Entity.NAME).eq(prim("product1"))
                    .and(prim("alias1").in(root().prims(Product.ALIASES, specification -> specification.setCondition(elemPE().in(prim("alias2"), prim("alias3"))))).not(),
                        root().ref(Product.RELATED_PRODUCT).type().eq(prim(ProductLimited.TYPE0)),
                        root().ref(Product.RELATED_PRODUCT, specification -> specification.setType(ProductLimited.TYPE0)).type().like(prim("ProductL%")),
                        root().ref(Product.RELATED_PRODUCT).id().in(prim("10"), prim("13")),
                        root().ref(Product.RELATED_PRODUCT, specification -> specification.setType(ProductLimited.TYPE0)).prim(ProductLimited.LIMITED_OFFER).like(prim("limitedOffer%")),
                        prim("alias1").in(root().prims(Product.ALIASES, specification -> specification.setCondition(elemPE().notEq(prim("alias2"))))),
                        root().ref(Product.RELATED_PRODUCT).prims(Product.ALIASES, specification -> specification.setCondition(elemPE().like(prim("alias1%")))).exists(),
                        root().ref(Product.RELATED_PRODUCT).ref(Product.RELATED_PRODUCT).prim(Product.CREATOR_CODE).eq(prim(13)),
                        root().ref(Product.RELATED_PRODUCT).ref(Product.RELATED_PRODUCT).prims(Product.ALIASES, specification -> specification.setCondition(elemPE().eq(prim("alias41")))).exists(),
                        root().refs(Product.SERVICES, specification -> specification.setCondition(elemE().prim(Service.MANAGER_PERSONAL_CODE).eq(prim(2)))).exists(),
                        root().ref(Product.RELATED_PRODUCT).refs(Product.SERVICES).exists(),
                        prim(2).in(root().refs(Product.SERVICES).prim(Service.MANAGER_PERSONAL_CODE)),
                        prim("action2").in(root().refs(Product.SERVICES, specification -> specification.setCondition(elemE(specification2 -> specification2.setType(Service.TYPE0)).ref(Service.START_ACTION).prim(Entity.CODE).in(prim("action2")))).ref(Service.START_ACTION).prim(Entity.CODE)),
                        root().refs(Product.SERVICES).ref(Service.START_ACTION, specification -> specification.setType(Action.TYPE0)).prim(Action.ALGORITHM_CODE).max().eq(prim(100)),
                        prim(101).eq(root().refs(Product.SERVICES).ref(Service.START_ACTION).prim(Action.ALGORITHM_CODE).sum()),
                        prim(Action.TYPE0).in(root().refs(Product.SERVICES).ref(Service.START_ACTION).type()),
                        prim("14").in(root().refs(Product.SERVICES).ref(Service.START_ACTION).id()),
                        root().refs(Product.SERVICES, specification -> specification.setCondition(prim("1").in(elemE(specification2 -> specification2.setType(Service.TYPE0)).refsB(Service.OPERATIONS, specification2 -> specification2.setType(OperationLimited.TYPE0)).prim(OperationLimited.LIMITED_OFFER)))).exists(),
                        prim(Product.CREATOR_CODE).mul(prim(2)).neg().plus(root().ref(Product.RELATED_PRODUCT).prim(Product.CREATOR_CODE).div(prim(3))).gtOrEq(prim(0)),
                        root().prim(Entity.CODE).upper().eq(prim("ProductLimited1").upper()),
                        root().prim(Entity.CODE).lower().eq(prim("ProductLimited1").lower()),
                        root().refB(Entity.REQUEST).id().eq(prim("15")),
                        prim("16").in(root().refs(Product.SERVICES).refB(Entity.REQUEST).id()));
            }
        });
    }

    @DisplayName("Test 6")
    @Test
    public void test6() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING6, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return prim("t' '1").gt(prim("t2"))
                    .and(prim(123).ltOrEq(prim(124)))
                    .or(prim(123.456F).lt(prim(123.457F).neg())
                        .and(prim(LocalDateTime.of(2020, 3, 5, 10, 12, 34, 567000000)).gtOrEq(prim(LocalDateTime.of(2020, 3, 5, 10, 12, 34, 577000000))))).not()
                    .and(prim(true).eq(prim(false))
                        .or(prim(true).notEq(prim(false))))
                    .and(prim("t1").isNull())
                    .and(prim(OffsetDateTime.of(2020, 3, 26, 10, 12, 34, 0, ZoneOffset.UTC)).gt(prim(OffsetDateTime.of(2020, 3, 26, 10, 12, 34, 123456000, ZoneOffset.of("-08:00")))))
                    .and(prim(OffsetDateTime.of(2020, 3, 26, 10, 12, 34, 123456789, ZoneOffset.UTC)).gt(prim(OffsetDateTime.of(2020, 3, 26, 10, 12, 34, 123000000, ZoneOffset.of("+01:00")))))
                    .and(prim(OffsetDateTime.of(2020, 3, 26, 10, 12, 34, 123000000, ZoneOffset.UTC)).gt(prim(OffsetDateTime.of(2020, 3, 26, 10, 12, 34, 123456000, ZoneOffset.UTC))))
                    .and(prim(OffsetDateTime.of(2020, 3, 26, 10, 12, 34, 123456789, ZoneOffset.of("+01:00"))).gt(prim(OffsetDateTime.of(2020, 3, 26, 10, 12, 34, 0, ZoneOffset.of("+02:00")))))
                    .and(prim(LocalDateTime.of(2020, 12, 23, 15, 37, 10, 123000000)).plus(prim(1)).isNotNull())
                    .and(prim(LocalDateTime.of(2021, 1, 13, 14, 41, 10, 0)).addSeconds(prim(1)).isNotNull())
                    .and(prim(OffsetDateTime.of(2021, 1, 13, 14, 41, 10, 0, ZoneOffset.of("+08:00"))).addSeconds(prim(1)).isNotNull());
            }
        });
    }

    @DisplayName("Test 7")
    @Test
    public void test7() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING7, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().refs(Product.SERVICES).id().min().eq(prim(1))
                    .and(root().refs(Product.SERVICES).id().max().eq(prim(1)))
                    .and(root().refs(Product.SERVICES).id().sum().eq(prim(1)))
                    .and(root().refs(Product.SERVICES).id().avg().eq(prim(1)))
                    .and(root().refs(Product.SERVICES).id().count().eq(prim(1)))
                    .and(root().refs(Product.SERVICES).count().eq(prim(1)))
                    .and(root().ref(Product.RELATED_PRODUCT).isNotNull())
                    .and(root().ref(Product.RELATED_PRODUCT).ref(Product.RELATED_PRODUCT).isNull())
                    .and(root().ref(Product.RELATED_PRODUCT).eq(root().ref(Product.RELATED_PRODUCT)))
                    .and(root().ref(Product.RELATED_PRODUCT).notEq(root().ref(Product.RELATED_PRODUCT).ref(Product.RELATED_PRODUCT)))
                    .and(root().refs(Product.SERVICES).exists())
                    .and(root().ref(Product.RELATED_PRODUCT).in(root().ref(Product.RELATED_PRODUCT), root().ref(Product.RELATED_PRODUCT).ref(Product.RELATED_PRODUCT)))
                    .and(root().ref(Product.RELATED_PRODUCT).in(root().refs(Product.SERVICES)).not());
            }
        });
    }

    @DisplayName("Test 8")
    @Test
    public void test8() {
        assertConditionBuilder(expressionsProcessor, "(3+13+19)*7*6==7||3==14||5==16", new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return prim(3).plus(prim(13), prim(19)).mul(prim(7), prim(6)).eq(prim(7))
                    .or(prim(3).eq(prim(14)),
                        prim(5).eq(prim(16)));
            }
        });
    }

    @DisplayName("Test 9")
    @Test
    public void test9() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING8, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().refB(Entity.REQUEST).group(Request.INITIATOR).prim(Person.FIRST_NAME).like(prim("Iv%"))
                    .and(root().refB(Entity.REQUEST).group(Request.INITIATOR).prim(Person.LAST_NAME).like(prim("Iv%")),
                        prim("Ivan").in(root().refs(Product.SERVICES).group(Request.INITIATOR).prim(Person.FIRST_NAME)),
                        root().refs(Product.SERVICES, specification -> specification.setCondition(elemE().group(Request.INITIATOR).prim(Person.FIRST_NAME).like(prim("Iv%")))).exists(),
                        root().refB(Entity.REQUEST).group(Request.INITIATOR).isNull(),
                        root().refB(Entity.REQUEST).group(Request.INITIATOR).isNotNull());
            }
        });
    }

    @DisplayName("Test 10")
    @Test
    public void test10() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING9, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return elemPE().like(prim("alias%"));
            }
        });
    }

    @DisplayName("Test 11")
    @Test
    public void test11() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING10, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return elemE().prim(Entity.CODE).like(prim("service%"));
            }
        });
    }

    @DisplayName("Test 12")
    @Test
    public void test12() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING11, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().prim(TestEntity.P1).length().gt(prim(3))
                    .and(root().prim(TestEntity.CODE).trim().eq(prim("Bye")),
                        root().prim(TestEntity.CODE).ltrim().eq(prim("Bye  ")),
                        root().prim(TestEntity.CODE).rtrim().eq(prim("  Bye")),
                        root().prim(TestEntity.P1).substr(root().prim(TestEntity.P2), root().prim(TestEntity.P3)).eq(root().prim(TestEntity.P1).substr(root().prim(TestEntity.P4))),
                        root().prim(TestEntity.P1).replace(root().prim(TestEntity.CODE), prim("Hey")).like(prim("HeyHey%")),
                        root().prim(TestEntity.P6).round().gt(prim(0)),
                        root().prim(TestEntity.P6).ceil().gt(prim(0)),
                        root().prim(TestEntity.P6).floor().gt(prim(0)),
                        root().prim(TestEntity.P1).hash().gt(prim(1)),
                        root().prim(TestEntity.P2).div(prim(2)).asString().eq(root().prim(TestEntity.P1).asBigDecimal().mul(prim(2)).asString()),
                        root().prim(TestEntity.P2).abs().eq(prim(4)));
            }
        });
    }

    @DisplayName("Test 13")
    @Test
    public void test13() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING12, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().prim(TestEntity.P7).addMilliseconds(root().prim(TestEntity.P2)).eq(root().prim(TestEntity.P14))
                    .and(root().prim(TestEntity.P7).addSeconds(root().prim(TestEntity.P2)).eq(root().prim(TestEntity.P14)),
                        root().prim(TestEntity.P7).addMinutes(root().prim(TestEntity.P2)).eq(root().prim(TestEntity.P14)),
                        root().prim(TestEntity.P7).addHours(root().prim(TestEntity.P2)).eq(root().prim(TestEntity.P14)),
                        root().prim(TestEntity.P7).addDays(root().prim(TestEntity.P2)).eq(root().prim(TestEntity.P14)),
                        root().prim(TestEntity.P7).addMonths(root().prim(TestEntity.P2)).eq(root().prim(TestEntity.P14)),
                        root().prim(TestEntity.P7).addYears(root().prim(TestEntity.P2)).eq(root().prim(TestEntity.P14)),
                        root().prim(TestEntity.P7)
                            .subMilliseconds(root().prim(TestEntity.P2))
                            .subSeconds(root().prim(TestEntity.P2))
                            .subMinutes(root().prim(TestEntity.P2))
                            .subHours(root().prim(TestEntity.P2))
                            .subDays(root().prim(TestEntity.P2))
                            .subMonths(root().prim(TestEntity.P2))
                            .subYears(root().prim(TestEntity.P2))
                            .eq(root().prim(TestEntity.P14)));
            }
        });
    }

    @DisplayName("Test 14")
    @Test
    public void test14() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING13, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().refsB(Operation.ACTIONS, specificationCode -> specificationCode
                    .setType(ActionSpecial.TYPE0)
                    .setElementAlias("action")
                    .setCondition(elemE().refsB(Entity.PARAMETERS, specificationCode2 -> specificationCode2
                        .setType(ActionParameter.TYPE0)
                        .setElementAlias("parameter")
                        .setCondition(elemE().refsB(Entity.PARAMETERS, specificationCode3 -> specificationCode3
                            .setCondition(elemE().prim(Entity.CODE).eq(aliasedEntity("action").prim(ActionSpecial.SPECIAL_OFFER).plus(aliasedEntity("parameter", specificationCode4 -> specificationCode4.setType(ActionParameterSpecial.TYPE0)).prim(ActionParameterSpecial.SPECIAL_OFFER))))).exists())).exists())).exists();
            }
        });
    }

    @DisplayName("Test 15")
    @Test
    public void test15() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING14, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return elemE().refsB(Entity.PARAMETERS, specificationCode2 -> specificationCode2
                    .setType(ActionParameter.TYPE0)
                    .setElementAlias("parameter")
                    .setCondition(elemE().refsB(Entity.PARAMETERS, specificationCode3 -> specificationCode3
                        .setCondition(elemE().prim(Entity.CODE).eq(aliasedEntity("action").prim(ActionSpecial.SPECIAL_OFFER).plus(aliasedEntity("parameter", specificationCode4 -> specificationCode4.setType(ActionParameterSpecial.TYPE0)).prim(ActionParameterSpecial.SPECIAL_OFFER))))).exists())).exists();
            }
        });
    }

    @DisplayName("Test 16")
    @Test
    public void test16() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING15, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().refsB(Operation.ACTIONS, specificationCode -> specificationCode
                        .setElementAlias("action")
                        .setCondition(elemE().refsB(Entity.PARAMETERS, specificationCode2 -> specificationCode2
                            .setCondition(elemE().prim(Entity.CODE).eq(aliasedEntity("action").prim(Entity.CODE)))).exists())).exists()
                    .and(root().refsB(Operation.ACTIONS, specificationCode -> specificationCode
                            .setElementAlias("action")
                            .setCondition(elemE().refsB(Entity.PARAMETERS, specificationCode2 -> specificationCode2
                                .setCondition(elemE().prim(Entity.CODE).notEq(aliasedEntity("action").prim(Entity.CODE)))).exists())).exists(),
                        root().ref(Operation.SERVICE, specificationCode -> specificationCode.setAlias("service")).ref(Service.START_ACTION, specificationCode -> specificationCode.setAlias("action")).refsB(Entity.PARAMETERS, specificationCode -> specificationCode
                            .setCondition(elemE().prim(Entity.CODE).like(aliasedEntity("service").prim(Entity.CODE).plus(aliasedEntity("action").prim(Entity.CODE)).plus(prim("%"))))).count().eq(prim(3)));
            }
        });
    }

    @DisplayName("Test 17")
    @Test
    public void test17() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING16, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return coalesce(root().prim("code"), root().prim("p1"), root().prim("p11")).eq(prim("testSTR"));
            }
        });
    }

    @DisplayName("Test 18")
    @Test
    public void test18() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING17, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().refB(Entity.REQUEST).group(Request.INITIATOR).ref(Person.DOCUMENT).prim(Document.CODE).eq(prim("document1"))
                    .and(root().refs(Product.SERVICES).group(Service.INITIATOR).ref(Person.DOCUMENT).prim(Document.CODE).exists(),
                        root().refB(Entity.REQUEST).group(Request.INITIATOR).ref(Person.DOCUMENT, specification -> specification.setType(Agreement.TYPE0).setAlias("document")).ref(Agreement.DOCUMENT).ref(Document.PRODUCT).refs(Product.SERVICES, specification -> specification.setCondition(elemE().prim(Entity.CODE).eq(aliasedEntity("document").prim(Document.CODE)))).exists(),
                        root().refs(Product.SERVICES).group(Service.INITIATOR).ref(Person.DOCUMENT, specification -> specification.setType(Permission.TYPE0)).prim(Permission.NUMBER).exists(),
                        root().ref(Product.RELATED_PRODUCT).exists());
            }
        });
    }

    @DisplayName("Test 19")
    @Test
    public void test19() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING18, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().prim(TestEntity.CODE).in(entities(specification -> specification.setType(Product.TYPE0).setElementAlias("product").setCondition(
                    elemE().refs(Product.SERVICES, specification2 -> specification2.setCondition(
                        elemE().prim(Entity.CODE).eq(aliasedEntity("product").prim(Entity.CODE)))).exists())).prim(Entity.CODE));
            }
        });
    }

    @DisplayName("Test 20")
    @Test
    public void test20() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING19, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return prim(7.5).mod(prim(2)).eq(prim(1.5));
            }
        });
    }

    @DisplayName("Test 21")
    @Test
    public void test21() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING20, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().prim("code").min().plus(
                    root().prim("code").max(),
                    root().prim("code").sum(),
                    root().prim("code").avg(),
                    root().prim("code").count()).eq(prim(1));
            }
        });
    }

    @DisplayName("Test 22")
    @Test
    public void test22() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING21, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().refs("services").map(elemE().refs("operations").count()).sum().eq(prim(1));
            }
        });
    }

    @DisplayName("Test 23")
    @Test
    public void test23() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING22, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().prims("aliases").map(prim("alias: ").plus(elemPE())).sum().eq(prim(1));
            }
        });
    }

    @DisplayName("Test 24")
    @Test
    public void test24() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING23, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return entities(specification -> specification
                    .setType(Test2Entity.TYPE0)
                    .setParameter(Test2Entity.Params.CHARACTER, 'A')
                    .setParameter(Test2Entity.Params.STRING, "Test")
                    .setParameter(Test2Entity.Params.BYTE, Byte.MAX_VALUE)
                    .setParameter(Test2Entity.Params.SHORT, Short.MAX_VALUE)
                    .setParameter(Test2Entity.Params.INTEGER, Integer.MAX_VALUE)
                    .setParameter(Test2Entity.Params.LONG, Long.MAX_VALUE)
                    .setParameter(Test2Entity.Params.FLOAT, 123.4567f)
                    .setParameter(Test2Entity.Params.DOUBLE, 1234567.890123456)
                    .setParameter(Test2Entity.Params.BIG_DECIMAL, new BigDecimal("92233720368547758079223372036854775807.92233720368547758079223372036854775807"))
                    .setParameter(Test2Entity.Params.DATE, LocalDate.of(2020, 3, 5))
                    .setParameter(Test2Entity.Params.DATETIME, LocalDateTime.of(2020, 3, 5, 10, 12, 34, 567000000))
                    .setParameter(Test2Entity.Params.OFFSET_DATETIME, OffsetDateTime.of(2020, 3, 5, 10, 12, 34, 567000000, ZoneOffset.of("+06:00")))
                    .setParameter(Test2Entity.Params.BOOLEAN, true)
                    .setParameter(Test2Entity.Params.STRINGS, Arrays.asList("Test1", "Test2", "Test3"))).exists();
            }
        });
    }

    @DisplayName("Test 25")
    @Test
    public void test25() {
        assertConditionBuilder(expressionsProcessor, "entities.$exists", new ConditionBuilder() {
            @Override
            protected Condition condition() {
                List<String> strings = new ArrayList<>();
                strings.add(null);
                return entities(null).exists();
            }
        });
    }

    @DisplayName("Test 26")
    @Test
    public void test26() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING25, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return now().isNotNull();
            }
        });
    }

    @DisplayName("Test 27")
    @Test
    public void test27() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING26, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return now().year().isNotNull()
                    .and(now().month().isNotNull())
                    .and(now().day().isNotNull())
                    .and(now().hour().isNotNull())
                    .and(now().minute().isNotNull())
                    .and(now().second().isNotNull())
                    .and(now().offsetHour().isNotNull())
                    .and(now().offsetMinute().isNotNull())
                    .and(prim("2023-08-21").asDate().isNotNull())
                    .and(prim("2023-08-21T14:24:10.123456").asDateTime().isNotNull())
                    .and(prim("2023-08-21T14:24:10.123456+08:00").asOffsetDateTime().isNotNull());
            }
        });
    }

    @DisplayName("Test 28")
    @Test
    public void test28() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING27, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return now().date().isNotNull()
                    .and(now().time().isNotNull())
                    .and(prim(LocalTime.of(12, 46)).asString().isNotNull())
                    .and(prim("12:46").asTime().isNotNull())
                    .and(now().offset().isNotNull());
            }
        });
    }

    @DisplayName("Test 29")
    @Test
    public void test29() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING28, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return prim(1).bitAnd(prim(2)).bitOr(prim(3).bitNot().bitXor(prim(4))).isNotNull()
                    .and(prim("123").lpad(prim(8), prim('0')).rpad(prim(10), prim('0')).isNotNull())
                    .and(prim(1).eq(prim(1)).not().not())
                    .and(prim(1).bitAnd(prim(1)).isNotNull())
                    .and(prim(1).bitAnd(prim(1)).eq(prim(1)))
                    .and(prim(1).bitAnd(prim(1)).between(prim(1), prim(2)))
                    .and(prim(1).bitAnd(prim(1)).in(prim(1), prim(2)))
                    .and(prim(1).shiftRight(prim(1)).eq(prim(0)))
                    .and(prim(1).shiftLeft(prim(1)).eq(prim(2)));
            }
        });
    }

    @DisplayName("Test 30")
    @Test
    public void test30() {
        assertEquals("${var}==1", new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return rawPE("${var}").eq(prim(1));
            }
        }.build(expressionsProcessor).toString());
    }

    @DisplayName("Test 31")
    @Test
    public void test31() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING29, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return prim("1").eq(any(root().prims("aliases")))
                    .and(prim("1").notEq(any(new Object[]{"1", "2", "3"})))
                    .and(prim("1").gt(all(root().prims("aliases"))))
                    .and(prim("1").ltOrEq(all(new Object[]{"1", "2", "3"})))
                    .and(prim(1).lt(any(new Object[]{1})))
                    .and(prim(LocalDate.of(2024, 2, 12)).gtOrEq(all(new Object[]{LocalDate.of(2024, 2, 12), LocalDate.of(2024, 2, 13)})));
            }
        });
    }

    @DisplayName("Test 32")
    @Test
    public void test32() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING30, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return prim(2).power(prim(3)).notEq(prim(8).log(prim(2)));
            }
        });
    }

    @DisplayName("Test 33")
    @Test
    public void test33() {
        assertConditionBuilder(expressionsProcessor, TestHelper.CONDITION_STRING31, new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().prim("p15").dateTime()
                    .eq(prim(LocalDateTime.of(2020, 10, 5, 13, 11, 0, 123450000)));
            }
        });
    }

    @DisplayName("Test for exception 'Using the wrong implementation'")
    @Test
    public void usageOfNotTargetImplementationExceptionTest() {
        assertThrows(UsageOfNotTargetImplementationException.class, () -> new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().prim(Entity.CODE).eq((PrimitiveExpression) null);
            }
        }.build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Using the wrong target implementation' (2)")
    @Test
    public void usageOfNotTargetImplementationExceptionTest2() {
        assertThrows(UsageOfNotTargetImplementationException.class, () -> new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().prim(Entity.CODE).in(null);
            }
        }.build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Using the wrong implementation' (3)")
    @Test
    public void usageOfNotTargetImplementationExceptionTest3() {
        assertThrows(UsageOfNotTargetImplementationException.class, () -> new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().ref(Product.RELATED_PRODUCT).eq(null);
            }
        }.build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Using the wrong target implementation' (4)")
    @Test
    public void usageOfNotTargetImplementationExceptionTest4() {
        assertThrows(UsageOfNotTargetImplementationException.class, () -> new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().ref(Product.RELATED_PRODUCT).in(null);
            }
        }.build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Using the wrong implementation' (5)")
    @Test
    public void usageOfNotTargetImplementationExceptionTest5() {
        assertThrows(UsageOfNotTargetImplementationException.class, () -> new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().prim(Entity.CODE).isNotNull().and(null);
            }
        }.build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Using the wrong target implementation' (6)")
    @Test
    public void usageOfNotTargetImplementationExceptionTest6() {
        assertThrows(UsageOfNotTargetImplementationException.class, () -> new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return prim("1").eq((ConditionalGroup) null);
            }
        }.build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Unexpected primitive value class'")
    @Test
    public void unexpectedPrimitiveValueClassExceptionTest() {
        assertThrows(UnexpectedPrimitiveValueClassException.class, () -> new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().id().eq(prim(new Pointer<>()));
            }
        }.build(expressionsProcessor));
    }

    @DisplayName("Test for the exception 'Unexpected primitive value class' (2)")
    @Test
    public void unexpectedPrimitiveValueClassExceptionTest2() {
        assertThrows(UnexpectedPrimitiveValueClassException.class, () -> new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().id().eq(prim(new ArrayList<String>()));
            }
        }.build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Unexpected primitive value class' (3)")
    @Test
    public void unexpectedPrimitiveValueClassExceptionTest3() {
        assertThrows(UnexpectedPrimitiveValueClassException.class, () -> new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return entities(specification -> specification
                    .setType(UserProduct.TYPE0)
                    .setParameter(UserProduct.Params.FIRST_NAME, new Pointer<>())
                ).exists();
            }
        }.build(expressionsProcessor));
    }

    @DisplayName("Test for exception 'Unexpected RAW object class'")
    @Test
    public void unexpectedRawClassExceptionTest() {
        assertThrows(UnexpectedRawClassException.class, () -> new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return rawPE(1).eq(prim(1));
            }
        }.build(expressionsProcessor));
    }
}
