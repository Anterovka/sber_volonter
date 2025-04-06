import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.aggregates.Aggregate
import ru.sbertech.dataspace.model.aggregates.AggregatesModel
import ru.sbertech.dataspace.model.aggregates.Leaf
import ru.sbertech.dataspace.model.aggregates.aggregatesModel
import ru.sbertech.dataspace.model.aggregates.isExternalReference
import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.model.property.ReferenceProperty
import ru.sbertech.dataspace.model.type.EmbeddableType
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.primitive.type.PrimitiveType

class AggregatesModelTest {
    private val model =
        Model
            .Builder()
            .apply {
                name = "model"
                types =
                    mutableListOf(
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
                                        typeName = "Product"
                                        idPropertyOverride =
                                            PrimitiveProperty.Override.Builder().apply {
                                                column = "product"
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
                            name = "Product"
                            properties =
                                mutableListOf(
                                    PrimitiveProperty.Builder().apply {
                                        name = "id"
                                        type = PrimitiveType.String
                                        isOptional = false
                                        column = "ID"
                                    },
                                    PrimitiveProperty.Builder().apply {
                                        name = "aggregateVersion"
                                        type = PrimitiveType.String
                                        isOptional = false
                                        column = "aggregateVersion"
                                    },
                                    EmbeddedProperty.Builder().apply {
                                        name = "externalAggregateReference"
                                        typeName = "ExternalAggregateReference"
                                    },
                                    EmbeddedProperty.Builder().apply {
                                        name = "externalLeafReference"
                                        typeName = "ExternalLeafReference"
                                    },
                                )
                            idPropertyName = "id"
                            table = "PRODUCT"
                        },
                        EntityType.Builder().apply {
                            name = "Service"
                            properties =
                                mutableListOf(
                                    PrimitiveProperty.Builder().apply {
                                        name = "id"
                                        type = PrimitiveType.String
                                        isOptional = false
                                        column = "ID"
                                    },
                                    EmbeddedProperty.Builder().apply {
                                        name = "externalLeafReference"
                                        typeName = "ExternalLeafReference"
                                    },
                                    ReferenceProperty.Builder().apply {
                                        name = "product"
                                        typeName = "Product"
                                        isOptional = false
                                        idPropertyOverride =
                                            PrimitiveProperty.Override.Builder().apply {
                                                column = "product"
                                            }
                                    },
                                    ReferenceProperty.Builder().apply {
                                        name = "aggregateRoot"
                                        typeName = "Product"
                                        isOptional = false
                                        idPropertyOverride =
                                            PrimitiveProperty.Override.Builder().apply {
                                                column = "aggregateRoot"
                                            }
                                    },
                                )
                            idPropertyName = "id"
                            table = "SERVICE"
                        },
                        EntityType.Builder().apply {
                            name = "Operation"
                            properties =
                                mutableListOf(
                                    PrimitiveProperty.Builder().apply {
                                        name = "id"
                                        type = PrimitiveType.String
                                        isOptional = false
                                        column = "ID"
                                    },
                                    ReferenceProperty.Builder().apply {
                                        name = "service"
                                        typeName = "Service"
                                        isOptional = false
                                        idPropertyOverride =
                                            PrimitiveProperty.Override.Builder().apply {
                                                column = "service"
                                            }
                                    },
                                    ReferenceProperty.Builder().apply {
                                        name = "aggregateRoot"
                                        typeName = "Product"
                                        isOptional = false
                                        idPropertyOverride =
                                            PrimitiveProperty.Override.Builder().apply {
                                                column = "aggregateRoot"
                                            }
                                    },
                                )
                            idPropertyName = "id"
                            table = "OPERATION"
                        },
                        EntityType.Builder().apply {
                            name = "ProductApiCall"
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
                                    ReferenceProperty.Builder().apply {
                                        name = "product"
                                        typeName = "Product"
                                        isOptional = false
                                        idPropertyOverride =
                                            PrimitiveProperty.Override.Builder().apply {
                                                column = "product"
                                            }
                                    },
                                    ReferenceProperty.Builder().apply {
                                        name = "aggregateRoot"
                                        typeName = "Product"
                                        isOptional = false
                                        idPropertyOverride =
                                            PrimitiveProperty.Override.Builder().apply {
                                                column = "aggregateRoot"
                                            }
                                    },
                                )
                            idPropertyName = "id"
                            table = "PRODUCT_API_CALL"
                        },
                    )
            }.build()

    init {
        AggregatesModel
            .Builder()
            .apply {
                externalReferenceTypes = mutableListOf()
                aggregates =
                    mutableListOf(
                        Aggregate.Builder().apply {
                            name = "Product"
                            aggregateVersionPropertyName = "aggregateVersion"
                            idempotenceDataEntityTypeName = "ProductApiCall"
                            externalReferences = mutableListOf("externalAggregateReference" to false, "externalLeafReference" to false)
                            leaves =
                                mutableListOf(
                                    Leaf.Builder().apply {
                                        name = "Service"
                                        parentProperty = "product"
                                        aggregateRootProperty = "aggregateRoot"
                                        externalReferences = mutableListOf("externalLeafReference" to false)
                                    },
                                    Leaf.Builder().apply {
                                        name = "Operation"
                                        parentProperty = "service"
                                    },
                                )
                        },
                    )
                defaultIdempotenceDataEntityTypeName = "ApiCall"
                defaultAggregateRootPropertyName = "aggregateRoot"
            }.build(model)
    }

    @Test
    fun test() {
        assertThat(model.aggregatesModel?.aggregateOrLeaf("Service")).isExactlyInstanceOf(Leaf::class.java)
        assertThat(model.aggregatesModel?.aggregateOrLeaf("Product")).isExactlyInstanceOf(Aggregate::class.java)
        assertThat((model.aggregatesModel?.aggregateOrLeaf("Service") as Leaf).aggregate)
            .isEqualTo(model.aggregatesModel?.aggregateOrLeaf("Product"))
        assertThat((model.aggregatesModel?.aggregateOrLeaf("Product") as Aggregate).aggregateVersionPropertyName)
            .isEqualTo("aggregateVersion")

        val productType = model.type("Product") as EntityType

        assertThat(productType.property("externalAggregateReference").isExternalReference).isTrue()
        assertThat(productType.property("externalLeafReference").isExternalReference).isTrue()

        val serviceType = model.type("Service") as EntityType
        assertThat(serviceType.property("externalLeafReference").isExternalReference).isTrue()
        assertThat(serviceType.property("product").isExternalReference).isFalse()
    }
}
