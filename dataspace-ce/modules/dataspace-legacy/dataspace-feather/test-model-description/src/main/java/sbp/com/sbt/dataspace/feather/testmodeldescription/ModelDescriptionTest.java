package sbp.com.sbt.dataspace.feather.testmodeldescription;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.InheritanceStrategy;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.TableType;
import sbp.com.sbt.dataspace.feather.testmodeldescriptioncommon.ModelDescriptionCheck;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * Testing the model description
 */
public abstract class ModelDescriptionTest {

    @Autowired
    protected ModelDescription modelDescription;

    @DisplayName("Test of test model")
    @Test
    public void testModelTest() {
        new ModelDescriptionCheck(modelDescription)
                .setEnumDescriptionCheck("Status", enumDescriptionCheck -> enumDescriptionCheck
                        .setValues(new LinkedHashSet<>(Arrays.asList("OPEN", "CLOSED"))))
                .setEnumDescriptionCheck("Attribute", enumDescriptionCheck -> enumDescriptionCheck
                        .setValues(new LinkedHashSet<>(Arrays.asList("TOP_PRIORITY", "FORBIDDEN"))))
                .setEntityDescriptionCheck("TestEntity", entityDescriptionCheck -> entityDescriptionCheck
                        .setAggregateEntityType("TestEntity")
                        .setFinal()
                        .setAggregate()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("F_TEST_ENTITY")
                        .setIdColumnName("ID")
                        .setAggregateColumnName("AGGREGATE")
                        .setSystemLocksTableName("F_TEST_ENTITY_SYSTEM_LOCKS")
                        .setSystemLocksAggregateColumnName("AGGREGATE")
                        .setSystemLocksVersionColumnName("VERSION")
                        .setPrimitiveDescriptionCheck("code", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("CODE")
                                .setType(DataType.STRING)
                                .setMandatory())
                        .setPrimitiveDescriptionCheck("code_2", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("CODE")
                                .setType(DataType.STRING)
                                .setMandatory())
                        .setPrimitiveDescriptionCheck("p1", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("P1")
                                .setType(DataType.STRING))
                        .setPrimitiveDescriptionCheck("p2", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("P2")
                                .setType(DataType.BYTE))
                        .setPrimitiveDescriptionCheck("p3", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("P3")
                                .setType(DataType.SHORT))
                        .setPrimitiveDescriptionCheck("p4", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("P4")
                                .setType(DataType.INTEGER))
                        .setPrimitiveDescriptionCheck("p5", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("P5")
                                .setType(DataType.LONG))
                        .setPrimitiveDescriptionCheck("p6", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("P6")
                                .setType(DataType.DOUBLE))
                        .setPrimitiveDescriptionCheck("p7", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("P7")
                                .setType(DataType.DATETIME))
                        .setPrimitiveDescriptionCheck("p8", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("P8")
                                .setType(DataType.BOOLEAN))
                        .setPrimitiveDescriptionCheck("p9", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("P9")
                                .setType(DataType.BYTE_ARRAY))
                        .setPrimitiveDescriptionCheck("p10", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("P10")
                                .setType(DataType.BIG_DECIMAL))
                        .setPrimitiveDescriptionCheck("p11", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("P11")
                                .setType(DataType.TEXT))
                        .setPrimitiveDescriptionCheck("p12", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("P12")
                                .setType(DataType.FLOAT))
                        .setPrimitiveDescriptionCheck("p13", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("P13")
                                .setType(DataType.CHARACTER))
                        .setPrimitiveDescriptionCheck("p14", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("P14")
                                .setType(DataType.DATE))
                        .setPrimitiveDescriptionCheck("p15", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("P15")
                                .setType(DataType.OFFSET_DATETIME))
                        .setPrimitivesCollectionDescriptionCheck("ps1", primitivesCollectionDescriptionCheck -> primitivesCollectionDescriptionCheck
                                .setColumnName("ELEMENT")
                                .setTableName("F_TEST_ENTITY_PS1")
                                .setOwnerColumnName("OWNER")
                                .setType(DataType.STRING))
                        .setPrimitivesCollectionDescriptionCheck("ps2", primitivesCollectionDescriptionCheck -> primitivesCollectionDescriptionCheck
                                .setColumnName("ELEMENT")
                                .setTableName("F_TEST_ENTITY_PS2")
                                .setOwnerColumnName("OWNER")
                                .setType(DataType.INTEGER))
                        .setPrimitivesCollectionDescriptionCheck("ps3", primitivesCollectionDescriptionCheck -> primitivesCollectionDescriptionCheck
                                .setColumnName("ELEMENT")
                                .setTableName("F_TEST_ENTITY_PS3")
                                .setOwnerColumnName("OWNER")
                                .setType(DataType.DATETIME))
                        .setPrimitivesCollectionDescriptionCheck("ps4", primitivesCollectionDescriptionCheck -> primitivesCollectionDescriptionCheck
                                .setColumnName("ELEMENT")
                                .setTableName("F_TEST_ENTITY_PS4")
                                .setOwnerColumnName("OWNER")
                                .setType(DataType.BOOLEAN))
                        .setReferenceDescriptionCheck("r1", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("R1")
                                .setEntityType("TestEntity")
                                .setEntityReferencesCollectionPropertyName("brec1"))
                        .setReferencesCollectionDescriptionCheck("rc1", referencesCollectionDescriptionCheck -> referencesCollectionDescriptionCheck
                                .setColumnName("ELEMENT")
                                .setTableName("F_TEST_ENTITY_RC1")
                                .setOwnerColumnName("OWNER")
                                .setEntityType("TestEntity"))
                        .setReferencesCollectionBackReferenceDescriptionCheck("brec1", referencesCollectionBackReferenceDescriptionCheck -> referencesCollectionBackReferenceDescriptionCheck
                                .setBackReferenceOwnerEntityType("TestEntity")
                                .setBackReferencePropertyName("r1"))
                        .setGroupDescriptionCheck("g1", groupDescriptionCheck -> groupDescriptionCheck
                                .setGroupName("Group1")
                                .setPrimitiveDescriptionCheck("p1", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("G1_P1")
                                        .setType(DataType.STRING)))
                        .setGroupDescriptionCheck("g2", groupDescriptionCheck -> groupDescriptionCheck
                                .setGroupName("Group2")
                                .setReferenceDescriptionCheck("r1", groupReferenceDescriptionCheck -> groupReferenceDescriptionCheck
                                        .setColumnName("R1")
                                        .setEntityType("TestEntity")
                                        .setMandatory()))
                        .setGroupDescriptionCheck("g3", groupDescriptionCheck -> groupDescriptionCheck
                                .setGroupName("Group3")
                                .setReferenceDescriptionCheck("r1", groupReferenceDescriptionCheck -> groupReferenceDescriptionCheck
                                        .setColumnName("G3_R1")
                                        .setEntityType("Product"))))
                .setEntityDescriptionCheck("Entity", entityDescriptionCheck -> entityDescriptionCheck
                        .setAggregate()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("F_ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")
                        .setSystemLocksTableName("F_ENTITY_SYSTEM_LOCKS")
                        .setSystemLocksAggregateColumnName("AGGREGATE")
                        .setSystemLocksVersionColumnName("VERSION")
                        .setPrimitiveDescriptionCheck("code", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("CODE")
                                .setType(DataType.STRING)
                                .setMandatory())
                        .setPrimitiveDescriptionCheck("name", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("NAME")
                                .setType(DataType.STRING))
                        .setPrimitivesCollectionDescriptionCheck("attributes", primitivesCollectionDescriptionCheck -> primitivesCollectionDescriptionCheck
                                .setColumnName("ELEMENT")
                                .setTableName("F_ENTITY_ATTRIBUTES")
                                .setOwnerColumnName("OWNER")
                                .setType(DataType.STRING)
                                .setEnumType("Attribute"))
                        .setReferenceBackReferenceDescriptionCheck("request", referenceBackReferenceDescriptionCheck -> referenceBackReferenceDescriptionCheck
                                .setBackReferenceOwnerEntityType("Request")
                                .setBackReferencePropertyName("createdEntity"))
                        .setReferencesCollectionBackReferenceDescriptionCheck("parameters", referencesCollectionBackReferenceDescriptionCheck -> referencesCollectionBackReferenceDescriptionCheck
                                .setBackReferenceOwnerEntityType("Parameter")
                                .setBackReferencePropertyName("entity")))
                .setEntityDescriptionCheck("Request", entityDescriptionCheck -> entityDescriptionCheck
                        .setAggregateEntityType("Entity")
                        .setInheritanceStrategy(InheritanceStrategy.SINGLE_TABLE)
                        .setTableName("F_REQUEST")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")
                        .setAggregateColumnName("AGGREGATE")
                        .setPrimitiveDescriptionCheck("code", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("CODE")
                                .setType(DataType.STRING)
                                .setMandatory())
                        .setReferenceDescriptionCheck("createdEntity", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("CREATED_ENTITY")
                                .setEntityType("Entity")
                                .setEntityReferencePropertyName("request"))
                        .setReferenceDescriptionCheck("agreement", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("AGREEMENT")
                                .setEntityType("Agreement")
                                .setEntityReferencePropertyName("request"))
                        .setGroupDescriptionCheck("initiator", groupDescriptionCheck -> groupDescriptionCheck
                                .setGroupName("Person")
                                .setPrimitiveDescriptionCheck("firstName", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("INITIATOR_FIRST_NAME")
                                        .setType(DataType.STRING)
                                        .setMandatory())
                                .setPrimitiveDescriptionCheck("lastName", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("INITIATOR_LAST_NAME")
                                        .setType(DataType.STRING))
                                .setPrimitiveDescriptionCheck("age", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("INITIATOR_AGE")
                                        .setType(DataType.INTEGER))
                                .setReferenceDescriptionCheck("document", groupReferenceDescriptionCheck -> groupReferenceDescriptionCheck
                                        .setColumnName("INITIATOR_DOCUMENT")
                                        .setEntityType("Document"))))
                .setEntityDescriptionCheck("RequestPlus", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("Request")
                        .setPrimitiveDescriptionCheck("description", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("DESCRIPTION")
                                .setType(DataType.STRING)))
                .setEntityDescriptionCheck("RequestSpecial", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("Request")
                        .setPrimitiveDescriptionCheck("specialCode", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("SPECIAL_CODE")
                                .setType(DataType.STRING)))
                .setEntityDescriptionCheck("Parameter", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("Entity")
                        .setTableName("F_PARAMETER")
                        .setIdColumnName("ID")
                        .setPrimitiveDescriptionCheck("value", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("VALUE")
                                .setType(DataType.STRING))
                        .setReferenceDescriptionCheck("entity", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("ENTITY")
                                .setEntityType("Entity")
                                .setMandatory()
                                .setEntityReferencesCollectionPropertyName("parameters")))
                .setEntityDescriptionCheck("ActionParameter", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("Parameter", "Entity")
                        .setTableName("F_ACTION_PARAMETER")
                        .setIdColumnName("ID")
                        .setPrimitiveDescriptionCheck("executorName", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("EXECUTOR_NAME")
                                .setType(DataType.STRING)))
                .setEntityDescriptionCheck("ActionParameterSpecial", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("ActionParameter", "Parameter", "Entity")
                        .setTableName("F_ACTION_PARAMETER_SPECIAL")
                        .setIdColumnName("ID")
                        .setPrimitiveDescriptionCheck("specialOffer", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("SPECIAL_OFFER")
                                .setType(DataType.STRING)))
                .setEntityDescriptionCheck("Service", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("Entity")
                        .setTableName("F_SERVICE")
                        .setIdColumnName("ID")
                        .setPrimitiveDescriptionCheck("managerPersonalCode", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("MANAGER_PERSONAL_CODE")
                                .setType(DataType.INTEGER))
                        .setReferenceDescriptionCheck("startAction", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("START_ACTION")
                                .setEntityType("Action"))
                        .setReferencesCollectionBackReferenceDescriptionCheck("operations", referencesCollectionBackReferenceDescriptionCheck -> referencesCollectionBackReferenceDescriptionCheck
                                .setBackReferenceOwnerEntityType("Operation")
                                .setBackReferencePropertyName("service"))
                        .setGroupDescriptionCheck("initiator", groupDescriptionCheck -> groupDescriptionCheck
                                .setGroupName("Person")
                                .setPrimitiveDescriptionCheck("firstName", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("INITIATOR_FIRST_NAME")
                                        .setType(DataType.STRING)
                                        .setMandatory())
                                .setPrimitiveDescriptionCheck("lastName", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("INITIATOR_LAST_NAME")
                                        .setType(DataType.STRING))
                                .setPrimitiveDescriptionCheck("age", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("INITIATOR_AGE")
                                        .setType(DataType.INTEGER))
                                .setReferenceDescriptionCheck("document", groupReferenceDescriptionCheck -> groupReferenceDescriptionCheck
                                        .setColumnName("INITIATOR_DOCUMENT")
                                        .setEntityType("Document"))))
                .setEntityDescriptionCheck("Operation", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("Entity")
                        .setTableName("F_OPERATION")
                        .setIdColumnName("ID")
                        .setReferenceDescriptionCheck("service", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("SERVICE")
                                .setEntityType("Service")
                                .setEntityReferencesCollectionPropertyName("operations"))
                        .setReferencesCollectionBackReferenceDescriptionCheck("actions", referencesCollectionBackReferenceDescriptionCheck -> referencesCollectionBackReferenceDescriptionCheck
                                .setBackReferenceOwnerEntityType("Action")
                                .setBackReferencePropertyName("operation")))
                .setEntityDescriptionCheck("OperationSpecial", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("Operation", "Entity")
                        .setTableName("F_OPERATION_SPECIAL")
                        .setIdColumnName("ID")
                        .setPrimitiveDescriptionCheck("specialOffer", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("SPECIAL_OFFER")
                                .setType(DataType.STRING))
                        .setReferenceDescriptionCheck("product", groupReferenceDescriptionCheck -> groupReferenceDescriptionCheck
                                .setColumnName("PRODUCT")
                                .setEntityType("Product"))
                        .setGroupDescriptionCheck("initiator", groupDescriptionCheck -> groupDescriptionCheck
                                .setGroupName("Person")
                                .setPrimitiveDescriptionCheck("firstName", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("INITIATOR_FIRST_NAME")
                                        .setType(DataType.STRING)
                                        .setMandatory())
                                .setPrimitiveDescriptionCheck("lastName", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("INITIATOR_LAST_NAME")
                                        .setType(DataType.STRING))
                                .setPrimitiveDescriptionCheck("age", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("INITIATOR_AGE")
                                        .setType(DataType.INTEGER))
                                .setReferenceDescriptionCheck("document", groupReferenceDescriptionCheck -> groupReferenceDescriptionCheck
                                        .setColumnName("INITIATOR_DOCUMENT")
                                        .setEntityType("Document"))))
                .setEntityDescriptionCheck("OperationLimited", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("Operation", "Entity")
                        .setTableName("F_OPERATION_LIMITED")
                        .setIdColumnName("ID")
                        .setPrimitiveDescriptionCheck("limitedOffer", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("LIMITED_OFFER")
                                .setType(DataType.STRING))
                        .setPrimitiveDescriptionCheck("endDate", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("END_DATE")
                                .setType(DataType.DATETIME))
                        .setReferenceDescriptionCheck("product", groupReferenceDescriptionCheck -> groupReferenceDescriptionCheck
                                .setColumnName("PRODUCT")
                                .setEntityType("Product")))
                .setEntityDescriptionCheck("Action", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("Entity")
                        .setTableName("F_ACTION")
                        .setIdColumnName("ID")
                        .setPrimitiveDescriptionCheck("algorithmCode", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("ALGORITHM_CODE")
                                .setType(DataType.INTEGER))
                        .setReferenceDescriptionCheck("operation", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("OPERATION")
                                .setEntityType("Operation")
                                .setEntityReferencesCollectionPropertyName("actions")))
                .setEntityDescriptionCheck("ActionSpecial", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("Action", "Entity")
                        .setTableName("F_ACTION_SPECIAL")
                        .setIdColumnName("ID")
                        .setPrimitiveDescriptionCheck("specialOffer", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("SPECIAL_OFFER")
                                .setType(DataType.STRING)))
                .setEntityDescriptionCheck("Event", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("Entity")
                        .setTableName("F_EVENT")
                        .setIdColumnName("ID")
                        .setPrimitiveDescriptionCheck("author", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("AUTHOR")
                                .setType(DataType.STRING)))
                .setEntityDescriptionCheck("Product", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("Entity")
                        .setTableName("F_PRODUCT")
                        .setIdColumnName("ID")
                        .setPrimitiveDescriptionCheck("creatorCode", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("CREATOR_CODE")
                                .setType(DataType.INTEGER))
                        .setPrimitivesCollectionDescriptionCheck("aliases", primitivesCollectionDescriptionCheck -> primitivesCollectionDescriptionCheck
                                .setColumnName("ELEMENT")
                                .setTableName("F_PRODUCT_ALIASES")
                                .setOwnerColumnName("OWNER")
                                .setType(DataType.STRING))
                        .setPrimitivesCollectionDescriptionCheck("rates", primitivesCollectionDescriptionCheck -> primitivesCollectionDescriptionCheck
                                .setColumnName("ELEMENT")
                                .setTableName("F_PRODUCT_RATES")
                                .setOwnerColumnName("OWNER")
                                .setType(DataType.DOUBLE))
                        .setReferenceDescriptionCheck("relatedProduct", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("RELATED_PRODUCT")
                                .setEntityType("Product"))
                        .setReferenceDescriptionCheck("mainDocument", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("MAIN_DOCUMENT")
                                .setEntityType("Document"))
                        .setReferencesCollectionDescriptionCheck("services", referencesCollectionDescriptionCheck -> referencesCollectionDescriptionCheck
                                .setColumnName("ELEMENT")
                                .setTableName("F_PRODUCT_SERVICES")
                                .setOwnerColumnName("OWNER")
                                .setEntityType("Service"))
                        .setReferencesCollectionDescriptionCheck("events", referencesCollectionDescriptionCheck -> referencesCollectionDescriptionCheck
                                .setColumnName("ELEMENT")
                                .setTableName("F_PRODUCT_EVENTS")
                                .setOwnerColumnName("OWNER")
                                .setEntityType("Event"))
                        .setReferencesCollectionBackReferenceDescriptionCheck("documents", referencesCollectionBackReferenceDescriptionCheck -> referencesCollectionBackReferenceDescriptionCheck
                                .setBackReferenceOwnerEntityType("Document")
                                .setBackReferencePropertyName("product")))
                .setEntityDescriptionCheck("ProductPlus", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("Product", "Entity")
                        .setTableName("F_PRODUCT_PLUS")
                        .setIdColumnName("ID")
                        .setReferencesCollectionDescriptionCheck("affectedProducts", referencesCollectionDescriptionCheck -> referencesCollectionDescriptionCheck
                                .setColumnName("ELEMENT")
                                .setTableName("F_PRODUCT_PLUS_AFFECTED_PRODUCTS")
                                .setOwnerColumnName("OWNER")
                                .setEntityType("Product")))
                .setEntityDescriptionCheck("ProductLimited", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("ProductPlus", "Product", "Entity")
                        .setTableName("F_PRODUCT_LIMITED")
                        .setIdColumnName("ID")
                        .setPrimitiveDescriptionCheck("limitedOffer", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("LIMITED_OFFER")
                                .setType(DataType.STRING)))
                .setEntityDescriptionCheck("Document", entityDescriptionCheck -> entityDescriptionCheck
                        .setInheritanceStrategy(InheritanceStrategy.SINGLE_TABLE)
                        .setTableName("F_DOCUMENT")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")
                        .setPrimitiveDescriptionCheck("code", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("CODE")
                                .setType(DataType.STRING)
                                .setMandatory())
                        .setPrimitiveDescriptionCheck("status", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("STATUS")
                                .setType(DataType.STRING)
                                .setEnumType("Status"))
                        .setReferenceDescriptionCheck("product", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("PRODUCT")
                                .setEntityType("Product")
                                .setEntityReferencesCollectionPropertyName("documents"))
                        .setReferencesCollectionBackReferenceDescriptionCheck("agreements", referencesCollectionBackReferenceDescriptionCheck -> referencesCollectionBackReferenceDescriptionCheck
                                .setBackReferenceOwnerEntityType("Agreement")
                                .setBackReferencePropertyName("document")))
                .setEntityDescriptionCheck("Permission", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("Document")
                        .setPrimitiveDescriptionCheck("number", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("NUMBER_")
                                .setType(DataType.INTEGER)))
                .setEntityDescriptionCheck("Agreement", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("Document")
                        .setPrimitiveDescriptionCheck("participant", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("PARTICIPANT")
                                .setType(DataType.STRING))
                        .setReferenceDescriptionCheck("document", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("DOCUMENT")
                                .setEntityType("Document")
                                .setEntityReferencesCollectionPropertyName("agreements"))
                        .setReferenceBackReferenceDescriptionCheck("request", referenceBackReferenceDescriptionCheck -> referenceBackReferenceDescriptionCheck
                                .setBackReferenceOwnerEntityType("Request")
                                .setBackReferencePropertyName("agreement")))
                .setEntityDescriptionCheck("AgreementSpecial", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("Agreement", "Document")
                        .setPrimitiveDescriptionCheck("specialConditions", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("SPECIAL_CONDITIONS")
                                .setType(DataType.STRING)))
                .setEntityDescriptionCheck("UserProduct", entityDescriptionCheck -> entityDescriptionCheck
                        .setFinal()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableType(TableType.QUERY)
                        .setIdColumnName("ID")
                        .setParamDescriptionCheck("firstName", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.STRING))
                        .setParamDescriptionCheck("lastName", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.STRING))
                        .setPrimitiveDescriptionCheck("code", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("CODE")
                                .setType(DataType.STRING)
                                .setMandatory())
                        .setPrimitivesCollectionDescriptionCheck("aliases", primitivesCollectionDescriptionCheck -> primitivesCollectionDescriptionCheck
                                .setColumnName("ELEMENT")
                                .setTableName("F_PRODUCT_ALIASES")
                                .setOwnerColumnName("OWNER")
                                .setType(DataType.STRING))
                        .setReferenceDescriptionCheck("relatedProduct", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("RELATED_PRODUCT")
                                .setEntityType("Product"))
                        .setReferencesCollectionDescriptionCheck("services", referencesCollectionDescriptionCheck -> referencesCollectionDescriptionCheck
                                .setColumnName("ELEMENT")
                                .setTableName("F_PRODUCT_SERVICES")
                                .setOwnerColumnName("OWNER")
                                .setEntityType("Service"))
                        .setGroupDescriptionCheck("initiator", groupDescriptionCheck -> groupDescriptionCheck
                                .setGroupName("Person")
                                .setPrimitiveDescriptionCheck("firstName", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("INITIATOR_FIRST_NAME")
                                        .setType(DataType.STRING)
                                        .setMandatory())
                                .setPrimitiveDescriptionCheck("lastName", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("INITIATOR_LAST_NAME")
                                        .setType(DataType.STRING))
                                .setPrimitiveDescriptionCheck("age", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("INITIATOR_AGE")
                                        .setType(DataType.INTEGER))
                                .setReferenceDescriptionCheck("document", groupReferenceDescriptionCheck -> groupReferenceDescriptionCheck
                                        .setColumnName("INITIATOR_DOCUMENT")
                                        .setEntityType("Document"))))
                .setEntityDescriptionCheck("ProductCopy", entityDescriptionCheck -> entityDescriptionCheck
                        .setFinal()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableType(TableType.QUERY)
                        .setPrimitiveDescriptionCheck("idCopy", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("ID")
                                .setType(DataType.STRING)
                                .setMandatory()))
                .setEntityDescriptionCheck("Test2Entity", entityDescriptionCheck -> entityDescriptionCheck
                        .setFinal()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableType(TableType.QUERY)
                        .setIdColumnName("ID")
                        .setParamDescriptionCheck("character", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.CHARACTER)
                                .setDefaultValue('a'))
                        .setParamDescriptionCheck("string", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.STRING)
                                .setDefaultValue("Test"))
                        .setParamDescriptionCheck("byte", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.BYTE)
                                .setDefaultValue((byte) 1))
                        .setParamDescriptionCheck("short", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.SHORT)
                                .setDefaultValue((short) 1))
                        .setParamDescriptionCheck("integer", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.INTEGER)
                                .setDefaultValue(1))
                        .setParamDescriptionCheck("long", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.LONG)
                                .setDefaultValue(1L))
                        .setParamDescriptionCheck("float", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.FLOAT)
                                .setDefaultValue(1.1f))
                        .setParamDescriptionCheck("double", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.DOUBLE)
                                .setDefaultValue(1.1))
                        .setParamDescriptionCheck("bigDecimal", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.BIG_DECIMAL)
                                .setDefaultValue(new BigDecimal("1.1")))
                        .setParamDescriptionCheck("date", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.DATE)
                                .setDefaultValue(LocalDate.of(2022, 6, 27)))
                        .setParamDescriptionCheck("datetime", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.DATETIME)
                                .setDefaultValue(LocalDateTime.of(2022, 6, 27, 14, 58, 10, 123000000)))
                        .setParamDescriptionCheck("offsetDatetime", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.OFFSET_DATETIME)
                                .setDefaultValue(OffsetDateTime.of(2022, 6, 27, 14, 58, 10, 123000000, ZoneOffset.of("+06:00"))))
                        .setParamDescriptionCheck("boolean", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.BOOLEAN)
                                .setDefaultValue(true))
                        .setParamDescriptionCheck("strings", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.STRING)
                                .setCollection()))
                .setEntityDescriptionCheck("Test3Entity", entityDescriptionCheck -> entityDescriptionCheck
                        .setFinal()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableType(TableType.QUERY)
                        .setIdColumnName("ID"))
                .setEntityDescriptionCheck("EntityA", entityDescriptionCheck -> entityDescriptionCheck
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("F_ENTITY_A")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")
                        .setPrimitiveDescriptionCheck("code", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("CODE")
                                .setType(DataType.STRING)
                                .setMandatory())
                        .setReferenceDescriptionCheck("refB", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("REF_B")
                                .setEntityType("EntityB")
                                .setMandatory()
                                .setEntityReferencesCollectionPropertyName("refsA"))
                        .setReferenceDescriptionCheck("ref2B", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("REF2_B")
                                .setEntityType("EntityB")
                                .setMandatory()))
                .setEntityDescriptionCheck("EntityAA", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("EntityA")
                        .setTableName("F_ENTITY_AA")
                        .setIdColumnName("ID")
                        .setPrimitiveDescriptionCheck("codeAA", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("CODE_AA")
                                .setType(DataType.STRING)))
                .setEntityDescriptionCheck("EntityAAA", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("EntityAA", "EntityA")
                        .setTableName("F_ENTITY_AAA")
                        .setIdColumnName("ID")
                        .setPrimitiveDescriptionCheck("codeAAA", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("CODE_AAA")
                                .setType(DataType.STRING))
                        .setReferenceDescriptionCheck("refE", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("REF_E")
                                .setEntityType("EntityE")))
                .setEntityDescriptionCheck("EntityB", entityDescriptionCheck -> entityDescriptionCheck
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("F_ENTITY_B")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")
                        .setPrimitiveDescriptionCheck("code", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("CODE")
                                .setType(DataType.STRING)
                                .setMandatory())
                        .setReferenceDescriptionCheck("refC", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("REF_C")
                                .setEntityType("EntityC")
                                .setMandatory()))
                .setEntityDescriptionCheck("EntityC", entityDescriptionCheck -> entityDescriptionCheck
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("F_ENTITY_C")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")
                        .setPrimitiveDescriptionCheck("code", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("CODE")
                                .setType(DataType.STRING)
                                .setMandatory())
                        .setReferenceDescriptionCheck("refD", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("REF_D")
                                .setEntityType("EntityD")))
                .setEntityDescriptionCheck("EntityD", entityDescriptionCheck -> entityDescriptionCheck
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("F_ENTITY_D")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")
                        .setPrimitiveDescriptionCheck("code", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("CODE")
                                .setType(DataType.STRING)
                                .setMandatory())
                        .setReferenceDescriptionCheck("refE", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("REF_E")
                                .setEntityType("EntityE")
                                .setMandatory()))
                .setEntityDescriptionCheck("EntityE", entityDescriptionCheck -> entityDescriptionCheck
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("F_ENTITY_E")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")
                        .setPrimitiveDescriptionCheck("code", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("CODE")
                                .setType(DataType.STRING)
                                .setMandatory()))
                .check();
    }
}
