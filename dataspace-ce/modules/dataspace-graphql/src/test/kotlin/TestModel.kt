import org.assertj.core.api.Assertions.assertThat
import ru.sbertech.dataspace.model.InheritanceStrategy
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.aggregates.Aggregate
import ru.sbertech.dataspace.model.aggregates.AggregatesModel
import ru.sbertech.dataspace.model.aggregates.Leaf
import ru.sbertech.dataspace.model.dictionaries.DictionariesModel
import ru.sbertech.dataspace.model.dictionaries.Dictionary
import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.property.PrimitiveCollectionProperty
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.model.property.ReferenceProperty
import ru.sbertech.dataspace.model.type.EmbeddableType
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.primitive.type.PrimitiveType

val modelBuilder =
    Model
        .Builder()
        .apply {
            name = "model"
            types =
                arrayListOf(
                    EmbeddableType.Builder().apply {
                        name = "ExternalAggregateReference"
                        properties =
                            arrayListOf(
                                PrimitiveProperty.Builder().apply {
                                    name = "entityId"
                                    type = PrimitiveType.String
                                    isOptional = false
                                    column = "entityId"
                                },
                            )
                    },
                    EmbeddableType.Builder().apply {
                        name = "ExternalLeafReference"
                        properties =
                            arrayListOf(
                                PrimitiveProperty.Builder().apply {
                                    name = "entityId"
                                    type = PrimitiveType.String
                                    isOptional = false
                                    column = "entityId"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "rootEntityId"
                                    type = PrimitiveType.String
                                    isOptional = false
                                    column = "rootEntityId"
                                },
                            )
                    },
                    EntityType.Builder().apply {
                        name = "ExternalAggregateReferenceCollection"
                        properties =
                            mutableListOf(
                                PrimitiveProperty.Builder().apply {
                                    name = "id"
                                    type = PrimitiveType.String
                                    isOptional = false
                                    column = "ID"
                                },
                                ReferenceProperty.Builder().apply {
                                    name = "backReference"
                                    typeName = "TestEntity"
                                    idPropertyOverride =
                                        PrimitiveProperty.Override.Builder().apply {
                                            column = "test_entity"
                                        }
                                },
                                EmbeddedProperty.Builder().apply {
                                    name = "reference"
                                    typeName = "ExternalAggregateReference"
                                },
                            )
                        idPropertyName = "id"
                        table = "external_aggregate_reference_collection"
                    },
                    EntityType.Builder().apply {
                        name = "TestEntityRef"
                        properties =
                            mutableListOf(
                                PrimitiveProperty.Builder().apply {
                                    name = "id"
                                    type = PrimitiveType.String
                                    isOptional = false
                                    column = "ID"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "pString"
                                    type = PrimitiveType.String
                                    column = "pString"
                                },
                                ReferenceProperty.Builder().apply {
                                    name = "testEntity"
                                    typeName = "TestEntity"
                                    isOptional = false
                                    idPropertyOverride =
                                        PrimitiveProperty.Override.Builder().apply {
                                            column = "test_entity"
                                        }
                                },
                                ReferenceProperty.Builder().apply {
                                    name = "aggregateRoot"
                                    typeName = "TestEntity"
                                    isOptional = false
                                    idPropertyOverride =
                                        PrimitiveProperty.Override.Builder().apply {
                                            column = "aggregate_root"
                                        }
                                },
                            )
                        idPropertyName = "id"
                        table = "test_entity_ref"
                    },
                    EmbeddableType.Builder().apply {
                        name = "Person"
                        properties =
                            arrayListOf(
                                PrimitiveProperty.Builder().apply {
                                    name = "firstName"
                                    type = PrimitiveType.String
                                    isOptional = false
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "lastName"
                                    type = PrimitiveType.String
                                    isOptional = true
                                },
                                PrimitiveCollectionProperty.Builder().apply {
                                    name = "nicknames"
                                    type = PrimitiveType.String
                                },
                            )
                    },
                    EntityType.Builder().apply {
                        name = "TestEntity"
                        properties =
                            arrayListOf(
                                PrimitiveProperty.Builder().apply {
                                    name = "id"
                                    type = PrimitiveType.String
                                    isOptional = false
                                    column = "OBJECT_ID"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "pString"
                                    type = PrimitiveType.String
                                    column = "pString"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "pText"
                                    type = PrimitiveType.Text
                                    column = "pText"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "pChar"
                                    type = PrimitiveType.Char
                                    column = "pChar"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "pByte"
                                    type = PrimitiveType.Byte
                                    column = "pByte"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "pShort"
                                    type = PrimitiveType.Short
                                    column = "pShort"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "pInteger"
                                    type = PrimitiveType.Int
                                    column = "pInteger"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "pLong"
                                    type = PrimitiveType.Long
                                    column = "pLong"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "pDouble"
                                    type = PrimitiveType.Double
                                    column = "pDouble"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "pFloat"
                                    type = PrimitiveType.Float
                                    column = "pFloat"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "pBigDecimal"
                                    type = PrimitiveType.BigDecimal
                                    column = "pBigDecimal"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "pBoolean"
                                    type = PrimitiveType.Boolean
                                    column = "pBoolean"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "pByteArray"
                                    type = PrimitiveType.ByteArray
                                    column = "pByteArray"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "pDate"
                                    type = PrimitiveType.LocalDateTime
                                    column = "pDate"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "pLocalDateTime"
                                    type = PrimitiveType.LocalDateTime
                                    column = "pLocalDateTime"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "pLocalDate"
                                    type = PrimitiveType.LocalDate
                                    column = "pLocalDate"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "pOffsetDateTime"
                                    type = PrimitiveType.OffsetDateTime
                                    column = "pOffsetDateTime"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "pLocalTime"
                                    type = PrimitiveType.LocalTime
                                    column = "pLocalTime"
                                },
                                ReferenceProperty.Builder().apply {
                                    name = "testEntityRef"
                                    typeName = "TestEntityRef"
                                    idPropertyOverride =
                                        PrimitiveProperty.Override.Builder().apply {
                                            column = "testEntityRef"
                                        }
                                },
                                ReferenceProperty.Builder().apply {
                                    name = "aggregateRoot"
                                    typeName = "TestEntity"
                                    isOptional = false
                                    idPropertyOverride =
                                        PrimitiveProperty.Override.Builder().apply {
                                            column = "aggregate_root"
                                        }
                                },
                                PrimitiveCollectionProperty.Builder().apply {
                                    name = "pStringCollection"
                                    type = PrimitiveType.String
                                    table = "lc_testentity_pStringCollection"
                                    ownerIdPropertyOverride =
                                        PrimitiveProperty.Override.Builder().apply {
                                            column = "testentity_id"
                                        }
                                    elementColumn = "pStringCollection"
                                },
                                PrimitiveCollectionProperty.Builder().apply {
                                    name = "pCharCollection"
                                    type = PrimitiveType.Char
                                    table = "lc_testentity_pCharCollection"
                                    ownerIdPropertyOverride =
                                        PrimitiveProperty.Override.Builder().apply {
                                            column = "testentity_id"
                                        }
                                    elementColumn = "pCharCollection"
                                },
                                PrimitiveCollectionProperty.Builder().apply {
                                    name = "pByteCollection"
                                    type = PrimitiveType.Byte
                                    table = "lc_testentity_pByteCollection"
                                    ownerIdPropertyOverride =
                                        PrimitiveProperty.Override.Builder().apply {
                                            column = "testentity_id"
                                        }
                                    elementColumn = "pByteCollection"
                                },
                                PrimitiveCollectionProperty.Builder().apply {
                                    name = "pShortCollection"
                                    type = PrimitiveType.Short
                                    table = "lc_testentity_pShortCollection"
                                    ownerIdPropertyOverride =
                                        PrimitiveProperty.Override.Builder().apply {
                                            column = "testentity_id"
                                        }
                                    elementColumn = "pShortCollection"
                                },
                                PrimitiveCollectionProperty.Builder().apply {
                                    name = "pIntegerCollection"
                                    type = PrimitiveType.Int
                                    table = "lc_testentity_pIntegerCollection"
                                    ownerIdPropertyOverride =
                                        PrimitiveProperty.Override.Builder().apply {
                                            column = "testentity_id"
                                        }
                                    elementColumn = "pIntegerCollection"
                                },
                                PrimitiveCollectionProperty.Builder().apply {
                                    name = "pLongCollection"
                                    type = PrimitiveType.Long
                                    table = "lc_testentity_pLongCollection"
                                    ownerIdPropertyOverride =
                                        PrimitiveProperty.Override.Builder().apply {
                                            column = "testentity_id"
                                        }
                                    elementColumn = "pLongCollection"
                                },
                                PrimitiveCollectionProperty.Builder().apply {
                                    name = "pDoubleCollection"
                                    type = PrimitiveType.Double
                                    table = "lc_testentity_pDoubleCollection"
                                    ownerIdPropertyOverride =
                                        PrimitiveProperty.Override.Builder().apply {
                                            column = "testentity_id"
                                        }
                                    elementColumn = "pDoubleCollection"
                                },
                                PrimitiveCollectionProperty.Builder().apply {
                                    name = "pFloatCollection"
                                    type = PrimitiveType.Float
                                    table = "lc_testentity_pFloatCollection"
                                    ownerIdPropertyOverride =
                                        PrimitiveProperty.Override.Builder().apply {
                                            column = "testentity_id"
                                        }
                                    elementColumn = "pFloatCollection"
                                },
                                PrimitiveCollectionProperty.Builder().apply {
                                    name = "pBigDecimalCollection"
                                    type = PrimitiveType.BigDecimal
                                    table = "lc_testentity_pBigDecimalCollection"
                                    ownerIdPropertyOverride =
                                        PrimitiveProperty.Override.Builder().apply {
                                            column = "testentity_id"
                                        }
                                    elementColumn = "pBigDecimalCollection"
                                },
                                PrimitiveCollectionProperty.Builder().apply {
                                    name = "pDateCollection"
                                    type = PrimitiveType.LocalDateTime
                                    table = "lc_testentity_pDateCollection"
                                    ownerIdPropertyOverride =
                                        PrimitiveProperty.Override.Builder().apply {
                                            column = "testentity_id"
                                        }
                                    elementColumn = "pDateCollection"
                                },
                                PrimitiveCollectionProperty.Builder().apply {
                                    name = "pLocalDateTimeCollection"
                                    type = PrimitiveType.LocalDateTime
                                    table = "lc_testentity_pLocalDateTimeCollection"
                                    ownerIdPropertyOverride =
                                        PrimitiveProperty.Override.Builder().apply {
                                            column = "testentity_id"
                                        }
                                    elementColumn = "pLocalDateTimeCollection"
                                },
                                PrimitiveCollectionProperty.Builder().apply {
                                    name = "pLocalDateCollection"
                                    type = PrimitiveType.LocalDate
                                    table = "lc_testentity_pLocalDateCollection"
                                    ownerIdPropertyOverride =
                                        PrimitiveProperty.Override.Builder().apply {
                                            column = "testentity_id"
                                        }
                                    elementColumn = "pLocalDateCollection"
                                },
                                PrimitiveCollectionProperty.Builder().apply {
                                    name = "pOffsetDateTimeCollection"
                                    type = PrimitiveType.OffsetDateTime
                                    table = "lc_testentity_pOffsetDateTimeCollection"
                                    ownerIdPropertyOverride =
                                        PrimitiveProperty.Override.Builder().apply {
                                            column = "testentity_id"
                                        }
                                    elementColumn = "pOffsetDateTimeCollection"
                                },
                                EmbeddedProperty.Builder().apply {
                                    name = "owner"
                                    typeName = "Person"
                                    propertyOverrides =
                                        arrayListOf(
                                            PrimitiveProperty.Override.Builder().apply {
                                                propertyName = "firstName"
                                                column = "OWNER_FIRSTNAME"
                                            },
                                            PrimitiveProperty.Override.Builder().apply {
                                                propertyName = "lastName"
                                                column = "OWNER_LASTNAME"
                                            },
                                            PrimitiveCollectionProperty.Override.Builder().apply {
                                                propertyName = "nicknames"
                                                table = "PRODUCT_OWNER_NICKNAMES"
                                                ownerIdPropertyOverride =
                                                    PrimitiveProperty.Override.Builder().apply {
                                                        column = "PRODUCT_ID"
                                                    }
                                                elementColumn = "NICKNAME"
                                            },
                                        )
                                },
                            )
                        idPropertyName = "id"
                        table = "T_TESTENTITY"
                        inheritanceStrategy = InheritanceStrategy.JOINED
                        discriminatorColumn = "TYPE"
                    },
                    EntityType.Builder().apply {
                        name = "TestEntityApiCall"
                        properties =
                            mutableListOf(
                                PrimitiveProperty.Builder().apply {
                                    name = "id"
                                    type = PrimitiveType.String
                                    isOptional = false
                                    column = "ID"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "data"
                                    type = PrimitiveType.String
                                    isOptional = false
                                    column = "DATA"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "firstCallDate"
                                    type = PrimitiveType.OffsetDateTime
                                    isOptional = false
                                    column = "first_call_date"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "testEntity"
                                    type = PrimitiveType.String
                                    isOptional = false
                                    column = "test_entity"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "aggregateRoot"
                                    type = PrimitiveType.String
                                    isOptional = false
                                    column = "aggregate_root"
                                },
                                // TODO
//                                ReferenceProperty.Builder().apply {
//                                    name = "testEntity"
//                                    typeName = "TestEntity"
//                                    isOptional = false
//                                    idPropertyOverride =
//                                        PrimitiveProperty.Override.Builder().apply {
//                                            column = "test_entity"
//                                        }
//                                },
//                                ReferenceProperty.Builder().apply {
//                                    name = "aggregateRoot"
//                                    typeName = "TestEntity"
//                                    isOptional = false
//                                    idPropertyOverride =
//                                        PrimitiveProperty.Override.Builder().apply {
//                                            column = "aggregate_root"
//                                        }
//                                },
                            )
                        idPropertyName = "id"
                        table = "T_TEST_ENTITY_API_CALL"
                    },
                    EntityType.Builder().apply {
                        name = "TestEntityExt"
                        properties =
                            mutableListOf(
                                PrimitiveProperty.Builder().apply {
                                    name = "pStringExt"
                                    type = PrimitiveType.String
                                    column = "pStringExt"
                                },
                            )
                        parentTypeName = "TestEntity"
                        table = "T_TEST_ENTITY_EXT"
                    },
                    EntityType.Builder().apply {
                        name = "TestEntitySingleTable"
                        properties =
                            mutableListOf(
                                PrimitiveProperty.Builder().apply {
                                    name = "id"
                                    type = PrimitiveType.String
                                    isOptional = false
                                    column = "ID"
                                },
                            )
                        idPropertyName = "id"
                        inheritanceStrategy = InheritanceStrategy.SINGLE_TABLE
                        table = "T_TEST_ENTITY_SINGLE_TABLE"
                        discriminatorColumn = "TYPE"
                    },
                    EntityType.Builder().apply {
                        name = "TestEntityExtSingleTable"
                        properties =
                            mutableListOf(
                                PrimitiveProperty.Builder().apply {
                                    name = "pStringExtSingleTable"
                                    type = PrimitiveType.String
                                    column = "pStringExtSingleTable"
                                },
                            )
                        parentTypeName = "TestEntitySingleTable"
                    },
                    EntityType.Builder().apply {
                        name = "Currency"
                        properties =
                            mutableListOf(
                                PrimitiveProperty.Builder().apply {
                                    name = "id"
                                    type = PrimitiveType.String
                                    isOptional = false
                                    column = "ID"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "name"
                                    type = PrimitiveType.String
                                    isOptional = false
                                    column = "name"
                                },
                                PrimitiveProperty.Builder().apply {
                                    name = "aggregateRoot"
                                    type = PrimitiveType.String
                                    column = "aggregate_root"
                                },
                                // TODO
//                                ReferenceProperty.Builder().apply {
//                                    name = "aggregateRoot"
//                                    typeName = "RootDictionary"
//                                    idPropertyOverride =
//                                        PrimitiveProperty.Override.Builder().apply {
//                                            column = "aggregate_root"
//                                        }
//                                },
                            )
                        idPropertyName = "id"
                        table = "CURRENCY"
                    },
                    EntityType.Builder().apply {
                        name = "RootDictionary"
                        properties =
                            mutableListOf(
                                PrimitiveProperty.Builder().apply {
                                    name = "id"
                                    type = PrimitiveType.String
                                    isOptional = false
                                    column = "ID"
                                },
                            )
                        idPropertyName = "id"
                        table = "ROOT_DICTIONARY"
                    },
                )
        }.also {
            assertThat(it.validate()).isEmpty()
        }

val model = modelBuilder.build()
val modelWithAggregates = modelBuilder.build()

val aggregatesModel =
    AggregatesModel
        .Builder()
        .apply {
            aggregates =
                mutableListOf(
                    Aggregate.Builder().apply {
                        name = "TestEntity"
                        idempotenceDataEntityTypeName = "TestEntityApiCall"
                        leaves =
                            mutableListOf(
                                Leaf.Builder().apply {
                                    name = "TestEntityApiCall"
                                    parentProperty = "testEntity"
                                    aggregateRootProperty = "aggregateRoot"
                                },
                            )
                    },
                    Aggregate.Builder().apply {
                        name = "TestEntitySingleTable"
                    },
                    Aggregate.Builder().apply {
                        name = "RootDictionary"
                        leaves =
                            mutableListOf(
                                Leaf.Builder().apply {
                                    name = "Currency"
                                    aggregateRootProperty = "aggregateRoot"
                                },
                            )
                    },
                )
        }.build(modelWithAggregates)

val dictionariesModel =
    DictionariesModel
        .Builder()
        .apply {
            dictionaries =
                mutableListOf(
                    Dictionary
                        .Builder()
                        .apply {
                            name = "Currency"
                        },
                )
        }.build(modelWithAggregates)
