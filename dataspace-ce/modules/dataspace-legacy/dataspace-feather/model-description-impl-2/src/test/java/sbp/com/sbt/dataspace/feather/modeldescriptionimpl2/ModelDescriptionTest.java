package sbp.com.sbt.dataspace.feather.modeldescriptionimpl2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
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

@DisplayName("Model Description Testing")
@SpringJUnitConfig(ModelDescriptionTestConfiguration.class)
public class ModelDescriptionTest {

    @Autowired
    ModelDescription modelDescription;

@DisplayName("Test of test model")
    @Test
    public void testModelTest() {
// TODO: 28.08.2024 Disabling work with aggregates, since there is no system-locks in pdm.
        new ModelDescriptionCheck(modelDescription)
                .setEnumDescriptionCheck("MyStatus", enumDescriptionCheck -> enumDescriptionCheck
                        .setValues(new LinkedHashSet<>(Arrays.asList("OPENED", "CLOSED"))))
                .setEnumDescriptionCheck("Gender", enumDescriptionCheck -> enumDescriptionCheck
                        .setValues(new LinkedHashSet<>(Arrays.asList("MALE", "FEMALE"))))
                .setEnumDescriptionCheck("Attribute", enumDescriptionCheck -> enumDescriptionCheck
                        .setValues(new LinkedHashSet<>(Arrays.asList("TOP_PRIORITY", "FORBIDDEN"))))
                .setEntityDescriptionCheck("TestEntity", entityDescriptionCheck -> entityDescriptionCheck
                        .setFinal()
//                        .setAggregate()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("T_TESTENTITY")
                        .setIdColumnName("OBJECT_ID")
//                        .setSystemLocksTableName("T_REPL_AGGLOCK_TESTENTITY")
//                        .setSystemLocksAggregateColumnName("ROOTID")
//                        .setSystemLocksVersionColumnName("VERSION")
                        .setPrimitiveDescriptionCheck("code", primitiveDescriptionCheck -> primitiveDescriptionCheck
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
                        .setReferenceDescriptionCheck("r1", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("R1_ID")
                                .setEntityType("TestEntity")
                                .setEntityReferencesCollectionPropertyName("brec1"))
                        .setReferencesCollectionBackReferenceDescriptionCheck("brec1", referencesCollectionBackReferenceDescriptionCheck -> referencesCollectionBackReferenceDescriptionCheck
                                .setBackReferenceOwnerEntityType("TestEntity")
                                .setBackReferencePropertyName("r1"))
                        .setPrimitiveDescriptionCheck("e1", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("E1")
                                .setType(DataType.DATETIME))
                        .setPrimitiveDescriptionCheck("e2", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("E2")
                                .setType(DataType.STRING)
                                .setEnumType("MyStatus")))
                .setEntityDescriptionCheck("HistEntity", entityDescriptionCheck -> entityDescriptionCheck
//                        .setAggregate()
                        .setTableName("T_HISTENTITY")
                        .setIdColumnName("OBJECT_ID")
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTypeColumnName("TYPE")
//                        .setSystemLocksTableName("T_REPL_AGGLOCK_HISTENTITY")
//                        .setSystemLocksAggregateColumnName("ROOTID")
//                        .setSystemLocksVersionColumnName("VERSION")
                )
                .setEntityDescriptionCheck("HistEntityHistory", entityDescriptionCheck -> entityDescriptionCheck
//                        .setAggregateEntityType("HistEntity")
                        .setTableName("T_HISTENTITYHISTORY")
                        .setIdColumnName("OBJECT_ID")
                        .setTypeColumnName("TYPE")
                        .setInheritanceStrategy(InheritanceStrategy.SINGLE_TABLE)
//                        .setAggregateColumnName("AGGREGATEROOT_ID")
                        .setReferenceDescriptionCheck("sysHistoryOwner", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("SYSHISTORYOWNER_ID")
                                .setEntityType("HistEntity")
                        )
                )
                .setEntityDescriptionCheck("Client", entityDescriptionCheck -> entityDescriptionCheck
//                        .setAggregate()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("T_CLIENT")
                        .setIdColumnName("OBJECT_ID")
                        .setTypeColumnName("TYPE")
//                        .setSystemLocksTableName("T_REPL_AGGLOCK_CLIENT")
//                        .setSystemLocksAggregateColumnName("ROOTID")
//                        .setSystemLocksVersionColumnName("VERSION")
                        .setReferencesCollectionBackReferenceDescriptionCheck("entities", referencesCollectionBackReferenceDescriptionCheck -> referencesCollectionBackReferenceDescriptionCheck
                                .setBackReferenceOwnerEntityType("RootEntity")
                                .setBackReferencePropertyName("owner")))
                .setEntityDescriptionCheck("PrivilegedClient", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("Client")
                        .setTableName("T_PRIVILEGEDCLIENT")
                        .setIdColumnName("OBJECT_ID")
                        .setReferencesCollectionBackReferenceDescriptionCheck("documents", referencesCollectionBackReferenceDescriptionCheck -> referencesCollectionBackReferenceDescriptionCheck
                                .setBackReferenceOwnerEntityType("Document")
                                .setBackReferencePropertyName("owner"))
                )
                .setEntityDescriptionCheck("RootEntity", entityDescriptionCheck -> entityDescriptionCheck
//                        .setAggregateEntityType("Client")
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("T_ROOTENTITY")
                        .setIdColumnName("OBJECT_ID")
                        .setTypeColumnName("TYPE")
//                        .setAggregateColumnName("AGGREGATEROOT_ID")
                        .setReferenceDescriptionCheck("owner", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("OWNER_ID")
                                .setEntityType("Client")
                                .setMandatory()
                                .setEntityReferencesCollectionPropertyName("entities"))
                        .setPrimitiveDescriptionCheck("code", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("CODE")
                                .setType(DataType.STRING)
                                .setMandatory())
                        .setPrimitiveDescriptionCheck("name", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("NAME")
                                .setType(DataType.STRING))
                        .setPrimitivesCollectionDescriptionCheck("attributes", primitivesCollectionDescriptionCheck -> primitivesCollectionDescriptionCheck
                                .setColumnName("ATTRIBUTES")
                                .setTableName("LC_ROOTENTITY_ATTRIBUTES")
                                .setOwnerColumnName("ROOTENTITY_ID")
                                .setType(DataType.STRING)
                                .setEnumType("Attribute"))
                        .setReferenceBackReferenceDescriptionCheck("request", referenceBackReferenceDescriptionCheck -> referenceBackReferenceDescriptionCheck
                                .setBackReferenceOwnerEntityType("Request")
                                .setBackReferencePropertyName("createdEntity"))
                        .setReferencesCollectionBackReferenceDescriptionCheck("parameters", referencesCollectionBackReferenceDescriptionCheck -> referencesCollectionBackReferenceDescriptionCheck
                                .setBackReferenceOwnerEntityType("Parameter")
                                .setBackReferencePropertyName("entity")))
                .setEntityDescriptionCheck("Request", entityDescriptionCheck -> entityDescriptionCheck
//                        .setAggregateEntityType("Client")
                        .setInheritanceStrategy(InheritanceStrategy.SINGLE_TABLE)
                        .setTableName("T_REQUEST")
                        .setIdColumnName("OBJECT_ID")
                        .setTypeColumnName("TYPE")
//                        .setAggregateColumnName("AGGREGATEROOT_ID")
                        .setPrimitiveDescriptionCheck("code", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("CODE")
                                .setType(DataType.STRING)
                                .setMandatory())
                        .setReferenceDescriptionCheck("createdEntity", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("CREATEDENTITY_ID")
                                .setEntityType("RootEntity")
                                .setMandatory()
                                .setEntityReferencePropertyName("request"))
                        .setGroupDescriptionCheck("initiator", groupDescriptionCheck -> groupDescriptionCheck
                                .setGroupName("Person")
                                .setPrimitiveDescriptionCheck("firstName", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("INITIATOR_FIRSTNAME")
                                        .setType(DataType.STRING)
                                        .setMandatory())
                                .setPrimitiveDescriptionCheck("lastName", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("INITIATOR_LASTNAME")
                                        .setType(DataType.STRING))
                                .setPrimitiveDescriptionCheck("gender", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("INITIATOR_GENDER")
                                        .setType(DataType.STRING)
                                        .setEnumType("Gender"))))
                .setEntityDescriptionCheck("RequestPlus", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("Request")
                        .setPrimitiveDescriptionCheck("description", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("DESCRIPTION")
                                .setType(DataType.STRING)))
                .setEntityDescriptionCheck("Parameter", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("RootEntity")
                        .setTableName("T_PARAMETER")
                        .setIdColumnName("OBJECT_ID")
                        .setPrimitiveDescriptionCheck("value", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("VALUE_")
                                .setType(DataType.STRING))
                        .setReferenceDescriptionCheck("entity", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("ENTITY_ID")
                                .setEntityType("RootEntity")
                                .setEntityReferencesCollectionPropertyName("parameters")))
                .setEntityDescriptionCheck("ActionParameter", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("Parameter", "RootEntity")
                        .setTableName("T_ACTIONPARAMETER")
                        .setIdColumnName("OBJECT_ID")
                        .setPrimitiveDescriptionCheck("executorName", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("EXECUTORNAME")
                                .setType(DataType.STRING)))
                .setEntityDescriptionCheck("ActionParameterSpecial", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("ActionParameter", "Parameter", "RootEntity")
                        .setTableName("T_ACTIONPARAMETERSPECIAL")
                        .setIdColumnName("OBJECT_ID")
                        .setPrimitiveDescriptionCheck("specialOffer", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("SPECIALOFFER")
                                .setType(DataType.STRING)))
                .setEntityDescriptionCheck("Service", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("RootEntity")
                        .setTableName("T_SERVICE")
                        .setIdColumnName("OBJECT_ID")
                        .setPrimitiveDescriptionCheck("managerPersonalCode", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("MANAGERPERSONALCODE")
                                .setType(DataType.INTEGER))
                        .setReferenceDescriptionCheck("product", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("PRODUCT_ID")
                                .setEntityType("Product")
                                .setEntityReferencesCollectionPropertyName("services"))
                        .setReferenceDescriptionCheck("startAction", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("STARTACTION_ID")
                                .setEntityType("Action"))
                        .setReferencesCollectionBackReferenceDescriptionCheck("operations", referencesCollectionBackReferenceDescriptionCheck -> referencesCollectionBackReferenceDescriptionCheck
                                .setBackReferenceOwnerEntityType("Operation")
                                .setBackReferencePropertyName("service"))
                        .setGroupDescriptionCheck("initiator", groupDescriptionCheck -> groupDescriptionCheck
                                .setGroupName("Person")
                                .setPrimitiveDescriptionCheck("firstName", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("INITIATOR_FIRSTNAME")
                                        .setType(DataType.STRING)
                                        .setMandatory())
                                .setPrimitiveDescriptionCheck("lastName", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("INITIATOR_LASTNAME")
                                        .setType(DataType.STRING))
                                .setPrimitiveDescriptionCheck("gender", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("INITIATOR_GENDER")
                                        .setType(DataType.STRING)
                                        .setEnumType("Gender"))))
                .setEntityDescriptionCheck("Operation", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("RootEntity")
                        .setTableName("T_OPERATION")
                        .setIdColumnName("OBJECT_ID")
                        .setReferenceDescriptionCheck("service", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("SERVICE_ID")
                                .setEntityType("Service")
                                .setEntityReferencesCollectionPropertyName("operations"))
                        .setReferencesCollectionBackReferenceDescriptionCheck("actions", referencesCollectionBackReferenceDescriptionCheck -> referencesCollectionBackReferenceDescriptionCheck
                                .setBackReferenceOwnerEntityType("Action")
                                .setBackReferencePropertyName("operation")))
                .setEntityDescriptionCheck("OperationSpecial", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("Operation", "RootEntity")
                        .setTableName("T_OPERATIONSPECIAL")
                        .setIdColumnName("OBJECT_ID")
                        .setPrimitiveDescriptionCheck("specialOffer", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("SPECIALOFFER")
                                .setType(DataType.STRING)))
                .setEntityDescriptionCheck("OperationLimited", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("Operation", "RootEntity")
                        .setTableName("T_OPERATIONLIMITED")
                        .setIdColumnName("OBJECT_ID")
                        .setPrimitiveDescriptionCheck("limitedOffer", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("LIMITEDOFFER")
                                .setType(DataType.STRING))
                        .setPrimitiveDescriptionCheck("endDate", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("ENDDATE")
                                .setType(DataType.DATETIME)))
                .setEntityDescriptionCheck("Action", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("RootEntity")
                        .setTableName("T_ACTION")
                        .setIdColumnName("OBJECT_ID")
                        .setPrimitiveDescriptionCheck("algorithmCode", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("ALGORITHMCODE")
                                .setType(DataType.INTEGER))
                        .setReferenceDescriptionCheck("operation", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("OPERATION_ID")
                                .setEntityType("Operation")
                                .setEntityReferencesCollectionPropertyName("actions")))
                .setEntityDescriptionCheck("ActionSpecial", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("Action", "RootEntity")
                        .setTableName("T_ACTIONSPECIAL")
                        .setIdColumnName("OBJECT_ID")
                        .setPrimitiveDescriptionCheck("specialOffer", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("SPECIALOFFER")
                                .setType(DataType.STRING)))
                .setEntityDescriptionCheck("Event", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("RootEntity")
                        .setTableName("T_EVENT")
                        .setIdColumnName("OBJECT_ID")
                        .setPrimitiveDescriptionCheck("author", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("AUTHOR")
                                .setType(DataType.STRING))
                        .setReferenceDescriptionCheck("product", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("PRODUCT_ID")
                                .setEntityType("Product")
                                .setEntityReferencesCollectionPropertyName("events")))
                .setEntityDescriptionCheck("Product", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("RootEntity")
                        .setTableName("T_PRODUCT")
                        .setIdColumnName("OBJECT_ID")
                        .setPrimitiveDescriptionCheck("creatorCode", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("CREATORCODE")
                                .setType(DataType.INTEGER))
                        .setPrimitivesCollectionDescriptionCheck("aliases", primitivesCollectionDescriptionCheck -> primitivesCollectionDescriptionCheck
                                .setColumnName("ALIASES")
                                .setTableName("LC_PRODUCT_ALIASES")
                                .setOwnerColumnName("PRODUCT_ID")
                                .setType(DataType.STRING))
                        .setPrimitivesCollectionDescriptionCheck("rates", primitivesCollectionDescriptionCheck -> primitivesCollectionDescriptionCheck
                                .setColumnName("RATES")
                                .setTableName("LC_PRODUCT_RATES")
                                .setOwnerColumnName("PRODUCT_ID")
                                .setType(DataType.DOUBLE))
                        .setReferenceDescriptionCheck("affectedByProduct", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("AFFECTEDBYPRODUCT_ID")
                                .setEntityType("ProductPlus")
                                .setEntityReferencesCollectionPropertyName("affectedProducts"))
                        .setReferenceDescriptionCheck("relatedProduct", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("RELATEDPRODUCT_ID")
                                .setEntityType("Product"))
                        .setReferencesCollectionBackReferenceDescriptionCheck("services", referencesCollectionBackReferenceDescriptionCheck -> referencesCollectionBackReferenceDescriptionCheck
                                .setBackReferenceOwnerEntityType("Service")
                                .setBackReferencePropertyName("product"))
                        .setReferencesCollectionBackReferenceDescriptionCheck("events", referencesCollectionBackReferenceDescriptionCheck -> referencesCollectionBackReferenceDescriptionCheck
                                .setBackReferenceOwnerEntityType("Event")
                                .setBackReferencePropertyName("product")))
                .setEntityDescriptionCheck("ProductPlus", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("Product", "RootEntity")
                        .setTableName("T_PRODUCTPLUS")
                        .setIdColumnName("OBJECT_ID")
                        .setReferencesCollectionBackReferenceDescriptionCheck("affectedProducts", referencesCollectionBackReferenceDescriptionCheck -> referencesCollectionBackReferenceDescriptionCheck
                                .setBackReferenceOwnerEntityType("Product")
                                .setBackReferencePropertyName("affectedByProduct")))
                .setEntityDescriptionCheck("ProductLimited", entityDescriptionCheck -> entityDescriptionCheck
                        .setParentEntityTypes("ProductPlus", "Product", "RootEntity")
                        .setTableName("T_PRODUCTLIMITED")
                        .setIdColumnName("OBJECT_ID")
                        .setPrimitiveDescriptionCheck("limitedOffer", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("LIMITEDOFFER")
                                .setType(DataType.STRING)))
                .setEntityDescriptionCheck("Document", entityDescriptionCheck -> entityDescriptionCheck
//                        .setAggregateEntityType("Client")
                        .setInheritanceStrategy(InheritanceStrategy.SINGLE_TABLE)
                        .setTableName("T_DOCUMENT")
                        .setIdColumnName("OBJECT_ID")
                        .setTypeColumnName("TYPE")
//                        .setAggregateColumnName("AGGREGATEROOT_ID")
                        .setReferenceDescriptionCheck("owner", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("OWNER_ID")
                                .setEntityType("PrivilegedClient")
                                .setMandatory()
                                .setEntityReferencesCollectionPropertyName("documents"))
                        .setPrimitiveDescriptionCheck("code", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("CODE")
                                .setType(DataType.STRING)
                                .setMandatory())
                        .setReferencesCollectionBackReferenceDescriptionCheck("agreements", referencesCollectionBackReferenceDescriptionCheck -> referencesCollectionBackReferenceDescriptionCheck
                                .setBackReferenceOwnerEntityType("Agreement")
                                .setBackReferencePropertyName("document"))
                        .setReferencesCollectionBackReferenceDescriptionCheck("products", referencesCollectionBackReferenceDescriptionCheck -> referencesCollectionBackReferenceDescriptionCheck
                                .setBackReferenceOwnerEntityType("RciDocumentProducts")
                                .setBackReferencePropertyName("backReference"))
                        .setReferencesCollectionBackReferenceDescriptionCheck("services", referencesCollectionBackReferenceDescriptionCheck -> referencesCollectionBackReferenceDescriptionCheck
                                .setBackReferenceOwnerEntityType("RciDocumentServices")
                                .setBackReferencePropertyName("backReference"))
                        .setReferencesCollectionBackReferenceDescriptionCheck("externalDocuments", referencesCollectionBackReferenceDescriptionCheck -> referencesCollectionBackReferenceDescriptionCheck
                                .setBackReferenceOwnerEntityType("RciDocumentExternalDocuments")
                                .setBackReferencePropertyName("backReference"))
                        .setGroupDescriptionCheck("product", groupDescriptionCheck -> groupDescriptionCheck
                                .setGroupName("ProductReference")
                                .setPrimitiveDescriptionCheck("rootEntityId", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("PRODUCT_ROOTENTITYID")
                                        .setType(DataType.STRING))
                                .setPrimitiveDescriptionCheck("entityId", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("PRODUCT_ENTITYID")
                                        .setType(DataType.STRING))
                                .setReferenceDescriptionCheck("entity", groupReferenceDescriptionCheck -> groupReferenceDescriptionCheck
                                        .setColumnName("PRODUCT_ENTITYID")
                                        .setEntityType("Product")))
                        .setGroupDescriptionCheck("service", groupDescriptionCheck -> groupDescriptionCheck
                                .setGroupName("ServiceReference")
                                .setPrimitiveDescriptionCheck("rootEntityId", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("SERVICE_ROOTENTITYID")
                                        .setType(DataType.STRING))
                                .setPrimitiveDescriptionCheck("entityId", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("SERVICE_ENTITYID")
                                        .setType(DataType.STRING))
                                .setReferenceDescriptionCheck("entity", groupReferenceDescriptionCheck -> groupReferenceDescriptionCheck
                                        .setColumnName("SERVICE_ENTITYID")
                                        .setEntityType("Service")))
                        .setGroupDescriptionCheck("externalDocument", groupDescriptionCheck -> groupDescriptionCheck
                                .setGroupName("ExternalDocumentReference")
                                .setPrimitiveDescriptionCheck("entityId", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("EXTERNALDOCUMENT_ENTITYID")
                                        .setType(DataType.STRING))))
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
                                .setColumnName("DOCUMENT_ID")
                                .setEntityType("Document")
                                .setEntityReferencesCollectionPropertyName("agreements"))
                        .setReferenceBackReferenceDescriptionCheck("details", referenceBackReferenceDescriptionCheck -> referenceBackReferenceDescriptionCheck
                                .setBackReferenceOwnerEntityType("AgreementDetails")
                                .setBackReferencePropertyName("agreement")))
                .setEntityDescriptionCheck("AgreementDetails", entityDescriptionCheck -> entityDescriptionCheck
//                        .setAggregateEntityType("Client")
                        .setInheritanceStrategy(InheritanceStrategy.SINGLE_TABLE)
                        .setTableName("T_AGREEMENTDETAILS")
                        .setIdColumnName("OBJECT_ID")
                        .setTypeColumnName("TYPE")
//                        .setAggregateColumnName("AGGREGATEROOT_ID")
                        .setPrimitiveDescriptionCheck("content", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setColumnName("CONTENT")
                                .setType(DataType.STRING))
                        .setReferenceDescriptionCheck("agreement", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("AGREEMENT_ID")
                                .setEntityType("Agreement")
                                .setMandatory()
                                .setEntityReferencePropertyName("details")))
                .setEntityDescriptionCheck("RciDocumentProducts", entityDescriptionCheck -> entityDescriptionCheck
                        .setFinal()
//                        .setAggregateEntityType("Client")
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("T_RCIDOCUMENTPRODUCTS")
                        .setIdColumnName("OBJECT_ID")
//                        .setAggregateColumnName("AGGREGATEROOT_ID")
                        .setReferenceDescriptionCheck("backReference", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("BACKREFERENCE_ID")
                                .setEntityType("Document")
                                .setMandatory()
                                .setEntityReferencesCollectionPropertyName("products"))
                        .setGroupDescriptionCheck("reference", groupDescriptionCheck -> groupDescriptionCheck
                                .setGroupName("ProductReference")
                                .setPrimitiveDescriptionCheck("rootEntityId", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("REFERENCE_ROOTENTITYID")
                                        .setType(DataType.STRING))
                                .setPrimitiveDescriptionCheck("entityId", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("REFERENCE_ENTITYID")
                                        .setType(DataType.STRING))
                                .setReferenceDescriptionCheck("entity", groupReferenceDescriptionCheck -> groupReferenceDescriptionCheck
                                        .setColumnName("REFERENCE_ENTITYID")
                                        .setEntityType("Product"))))
                .setEntityDescriptionCheck("RciDocumentServices", entityDescriptionCheck -> entityDescriptionCheck
                        .setFinal()
//                        .setAggregateEntityType("Client")
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("T_RCIDOCUMENTSERVICES")
                        .setIdColumnName("OBJECT_ID")
//                        .setAggregateColumnName("AGGREGATEROOT_ID")
                        .setReferenceDescriptionCheck("backReference", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("BACKREFERENCE_ID")
                                .setEntityType("Document")
                                .setMandatory()
                                .setEntityReferencesCollectionPropertyName("services"))
                        .setGroupDescriptionCheck("reference", groupDescriptionCheck -> groupDescriptionCheck
                                .setGroupName("ServiceReference")
                                .setPrimitiveDescriptionCheck("rootEntityId", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("REFERENCE_ROOTENTITYID")
                                        .setType(DataType.STRING))
                                .setPrimitiveDescriptionCheck("entityId", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("REFERENCE_ENTITYID")
                                        .setType(DataType.STRING))
                                .setReferenceDescriptionCheck("entity", groupReferenceDescriptionCheck -> groupReferenceDescriptionCheck
                                        .setColumnName("REFERENCE_ENTITYID")
                                        .setEntityType("Service"))))
                .setEntityDescriptionCheck("RciDocumentExternalDocuments", entityDescriptionCheck -> entityDescriptionCheck
                        .setFinal()
//                        .setAggregateEntityType("Client")
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("T_RCIDOCUMENTEXTERNALDOCUMENTS")
                        .setIdColumnName("OBJECT_ID")
//                        .setAggregateColumnName("AGGREGATEROOT_ID")
                        .setReferenceDescriptionCheck("backReference", referenceDescriptionCheck -> referenceDescriptionCheck
                                .setColumnName("BACKREFERENCE_ID")
                                .setEntityType("Document")
                                .setMandatory()
                                .setEntityReferencesCollectionPropertyName("externalDocuments"))
                        .setGroupDescriptionCheck("reference", groupDescriptionCheck -> groupDescriptionCheck
                                .setGroupName("ExternalDocumentsReference")
                                .setPrimitiveDescriptionCheck("entityId", groupPrimitiveDescriptionCheck -> groupPrimitiveDescriptionCheck
                                        .setColumnName("REFERENCE_ENTITYID")
                                        .setType(DataType.STRING))))
                .setEntityDescriptionCheck("PersonInfo", entityDescriptionCheck -> entityDescriptionCheck
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setIdColumnName("id")
                        .setFinal()
                        .setTableType(TableType.QUERY)
                        .setParamDescriptionCheck("firstName", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.STRING))
                        .setParamDescriptionCheck("lastName", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.STRING))
                        .setParamDescriptionCheck("phones", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.STRING)
                                .setCollection())
                        .setPrimitiveDescriptionCheck("firstName", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setType(DataType.STRING)
                                .setColumnName("firstName"))
                        .setPrimitiveDescriptionCheck("lastName", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setType(DataType.STRING)
                                .setColumnName("lastName"))
                        .setPrimitiveDescriptionCheck("patronymic", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setType(DataType.STRING)
                                .setColumnName("patronymic")))
                .setEntityDescriptionCheck("Statistic", entityDescriptionCheck -> entityDescriptionCheck
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setFinal()
                        .setTableType(TableType.QUERY)
                        .setPrimitiveDescriptionCheck("personsCount", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setType(DataType.LONG)
                                .setColumnName("personsCount")))
                .setEntityDescriptionCheck("TestQuery", entityDescriptionCheck -> entityDescriptionCheck
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setFinal()
                        .setTableType(TableType.QUERY)
                        .setParamDescriptionCheck("characterParam", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.CHARACTER)
                                .setDefaultValue('a'))
                        .setParamDescriptionCheck("stringParam", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.STRING)
                                .setDefaultValue("Test"))
                        .setParamDescriptionCheck("byteParam", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.BYTE)
                                .setDefaultValue((byte) 1))
                        .setParamDescriptionCheck("shortParam", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.SHORT)
                                .setDefaultValue((short) 1))
                        .setParamDescriptionCheck("integerParam", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.INTEGER)
                                .setDefaultValue(1))
                        .setParamDescriptionCheck("longParam", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.LONG)
                                .setDefaultValue(1L))
                        .setParamDescriptionCheck("floatParam", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.FLOAT)
                                .setDefaultValue(1.1f))
                        .setParamDescriptionCheck("doubleParam", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.DOUBLE)
                                .setDefaultValue(1.1))
                        .setParamDescriptionCheck("bigDecimalParam", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.BIG_DECIMAL)
                                .setDefaultValue(new BigDecimal("1.1")))
                        .setParamDescriptionCheck("localDateParam", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.DATE)
                                .setDefaultValue(LocalDate.of(2022, 6, 27)))
                        .setParamDescriptionCheck("localDateTimeParam", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.DATETIME)
                                .setDefaultValue(LocalDateTime.of(2022, 6, 27, 14, 58, 10, 123000000)))
                        .setParamDescriptionCheck("offsetDateTimeParam", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.OFFSET_DATETIME)
                                .setDefaultValue(OffsetDateTime.of(2022, 6, 27, 14, 58, 10, 123000000, ZoneOffset.of("+06:00"))))
                        .setParamDescriptionCheck("booleanParam", paramDescriptionCheck -> paramDescriptionCheck
                                .setType(DataType.BOOLEAN)
                                .setDefaultValue(true))
                        .setPrimitiveDescriptionCheck("test", primitiveDescriptionCheck -> primitiveDescriptionCheck
                                .setType(DataType.STRING)
                                .setColumnName("test")))
                .check();
    }
}
