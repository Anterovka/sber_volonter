import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.sbertech.dataspace.EntityWithStatus
import ru.sbertech.dataspace.StatusModel
import ru.sbertech.dataspace.StatusProperty
import ru.sbertech.dataspace.StatusType
import ru.sbertech.dataspace.data.Status
import ru.sbertech.dataspace.data.StatusGroup
import ru.sbertech.dataspace.data.StatusTransition
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.model.property.ReferenceProperty
import ru.sbertech.dataspace.model.type.EmbeddableType
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.statusInfo
import ru.sbertech.dataspace.statusModel
import ru.sbertech.dataspace.statusProperty

class StatusModelTest {
    private val model =
        Model
            .Builder()
            .apply {
                name = "model"
                types =
                    mutableListOf(
                        EmbeddableType.Builder().apply {
                            name = "StatusFields"
                            properties =
                                arrayListOf(
                                    PrimitiveProperty.Builder().apply {
                                        name = "code"
                                        type = PrimitiveType.String
                                    },
                                    PrimitiveProperty.Builder().apply {
                                        name = "reason"
                                        type = PrimitiveType.String
                                    },
                                )
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
                                    ReferenceProperty.Builder().apply {
                                        name = "statusForService"
                                        typeName = "Status"
                                        isOptional = false
                                        idPropertyOverride =
                                            PrimitiveProperty.Override.Builder().apply {
                                                column = "statusForService"
                                            }
                                    },
                                    PrimitiveProperty.Builder().apply {
                                        name = "statusCodeForService"
                                        type = PrimitiveType.String
                                        isOptional = false
                                        column = "statusCodeForService"
                                    },
                                    PrimitiveProperty.Builder().apply {
                                        name = "statusReasonForService"
                                        type = PrimitiveType.String
                                        isOptional = false
                                        column = "statusReasonForService"
                                    },
                                    ReferenceProperty.Builder().apply {
                                        name = "statusForPlatform"
                                        typeName = "Status"
                                        isOptional = false
                                        idPropertyOverride =
                                            PrimitiveProperty.Override.Builder().apply {
                                                column = "statusForPlatform"
                                            }
                                    },
                                    PrimitiveProperty.Builder().apply {
                                        name = "statusCodeForPlatform"
                                        type = PrimitiveType.String
                                        isOptional = false
                                        column = "statusCodeForPlatform"
                                    },
                                    PrimitiveProperty.Builder().apply {
                                        name = "statusReasonForPlatform"
                                        type = PrimitiveType.String
                                        isOptional = false
                                        column = "statusReasonForPlatform"
                                    },
                                )
                            idPropertyName = "id"
                            table = "PRODUCT"
                        },
                        EntityType.Builder().apply {
                            name = "Status"
                            properties =
                                mutableListOf(
                                    PrimitiveProperty.Builder().apply {
                                        name = "id"
                                        type = PrimitiveType.String
                                        isOptional = false
                                        column = "ID"
                                    },
                                    PrimitiveProperty.Builder().apply {
                                        name = "code"
                                        type = PrimitiveType.String
                                        isOptional = false
                                        column = "code"
                                    },
                                    ReferenceProperty.Builder().apply {
                                        name = "group"
                                        typeName = "StatusGroup"
                                        isOptional = false
                                        idPropertyOverride =
                                            PrimitiveProperty.Override.Builder().apply {
                                                column = "group"
                                            }
                                    },
                                )
                            idPropertyName = "id"
                            table = "T_STATUS"
                        },
                        EntityType.Builder().apply {
                            name = "StatusGraph"
                            properties =
                                mutableListOf(
                                    PrimitiveProperty.Builder().apply {
                                        name = "id"
                                        type = PrimitiveType.String
                                        isOptional = false
                                        column = "ID"
                                    },
                                    ReferenceProperty.Builder().apply {
                                        name = "statusFrom"
                                        typeName = "Status"
                                        isOptional = false
                                        idPropertyOverride =
                                            PrimitiveProperty.Override.Builder().apply {
                                                column = "statusFrom"
                                            }
                                    },
                                    ReferenceProperty.Builder().apply {
                                        name = "statusTo"
                                        typeName = "Status"
                                        isOptional = false
                                        idPropertyOverride =
                                            PrimitiveProperty.Override.Builder().apply {
                                                column = "statusTo"
                                            }
                                    },
                                )
                            idPropertyName = "id"
                            table = "T_STATUSGRAPH"
                        },
                        EntityType.Builder().apply {
                            name = "StatusGroup"
                            properties =
                                mutableListOf(
                                    PrimitiveProperty.Builder().apply {
                                        name = "id"
                                        type = PrimitiveType.String
                                        isOptional = false
                                        column = "ID"
                                    },
                                    PrimitiveProperty.Builder().apply {
                                        name = "code"
                                        type = PrimitiveType.String
                                        isOptional = false
                                        column = "code"
                                    },
                                )
                            idPropertyName = "id"
                            table = "T_STATUSGROUP"
                        },
                    )
            }.build()

    init {
        StatusModel
            .Builder()
            .apply {
                statusType =
                    StatusType.Builder().apply {
                        type = "StatusFields"
                        codeProperty = "code"
                        reasonProperty = "reason"
                    }
                statusGroups =
                    mutableListOf(
                        StatusGroup.Builder().apply {
                            code = "service"
                        },
                        StatusGroup.Builder().apply {
                            code = "platform"
                        },
                    )
                entitiesWithStatus =
                    mutableListOf(
                        EntityWithStatus.Builder().apply {
                            entityType = "Product"
                            statusProperties =
                                mutableListOf(
                                    StatusProperty.Builder().apply {
                                        propertyName = "statusForService"
                                        groupCode = "service"
                                    },
                                    StatusProperty.Builder().apply {
                                        propertyName = "statusForPlatform"
                                        groupCode = "platform"
                                    },
                                )
                            statuses =
                                mutableListOf(
                                    Status.Builder().apply {
                                        isInitial = true
                                        code = "open"
                                        groupCode = "service"
                                        transitions =
                                            mutableListOf(
                                                StatusTransition.Builder().apply {
                                                    statusTo = "closed"
                                                },
                                            )
                                    },
                                    Status.Builder().apply {
                                        isInitial = false
                                        code = "closed"
                                        groupCode = "service"
                                    },
                                )
                        },
                    )
            }.build(model)
    }

    @Test
    fun test() {
        val productType = model.type("Product") as EntityType

        assertThat(productType.statusInfo?.statusProperties?.size).isEqualTo(2)

        assertThat(
            productType
                .property("statusForService")
                .statusProperty
                ?.group
                ?.code,
        ).isEqualTo("service")
        assertThat(
            productType
                .property("statusForPlatform")
                .statusProperty
                ?.group
                ?.code,
        ).isEqualTo("platform")

        assertThat(productType.statusInfo?.statuses?.size).isEqualTo(2)
        assertThat(model.statusModel?.statusGroups?.size).isEqualTo(2)
    }
}
