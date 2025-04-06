import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.property.PrimitiveCollectionProperty
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.model.type.EmbeddableType
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.primitive.type.PrimitiveType

class MyTest {
    @Test
    fun test() {
        val model =
            Model
                .Builder()
                .apply {
                    name = "model"
                    types =
                        arrayListOf(
                            EmbeddableType.Builder().apply {
                                name = "Signature"
                                properties =
                                    arrayListOf(
                                        PrimitiveProperty.Builder().apply {
                                            name = "private"
                                            type = PrimitiveType.String
                                        },
                                        PrimitiveProperty.Builder().apply {
                                            name = "public"
                                            type = PrimitiveType.String
                                        },
                                    )
                            },
                            EmbeddableType.Builder().apply {
                                name = "Document"
                                properties =
                                    arrayListOf(
                                        PrimitiveProperty.Builder().apply {
                                            name = "serial"
                                            type = PrimitiveType.String
                                            isOptional = false
                                        },
                                        PrimitiveProperty.Builder().apply {
                                            name = "number"
                                            type = PrimitiveType.String
                                            isOptional = false
                                        },
                                        PrimitiveCollectionProperty.Builder().apply {
                                            name = "states"
                                            type = PrimitiveType.String
                                        },
                                        EmbeddedProperty.Builder().apply {
                                            name = "signature"
                                            typeName = "Signature"
                                        },
                                    )
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
                                        EmbeddedProperty.Builder().apply {
                                            name = "passport"
                                            typeName = "Document"
                                        },
                                        EmbeddedProperty.Builder().apply {
                                            name = "birthCertificate"
                                            typeName = "Document"
                                        },
                                    )
                            },
                            EntityType.Builder().apply {
                                name = "Product"
                                properties =
                                    arrayListOf(
                                        PrimitiveProperty.Builder().apply {
                                            name = "id"
                                            type = PrimitiveType.String
                                            isOptional = false
                                            column = "ID"
                                        },
                                        PrimitiveProperty.Builder().apply {
                                            name = "code"
                                            type = PrimitiveType.String
                                            column = "CODE"
                                        },
                                        PrimitiveCollectionProperty.Builder().apply {
                                            name = "aliases"
                                            type = PrimitiveType.String
                                            table = "PRODUCT_ALIASES"
                                            ownerIdPropertyOverride =
                                                PrimitiveProperty.Override.Builder().apply {
                                                    column = "PRODUCT_ID"
                                                }
                                            elementColumn = "ALIAS"
                                        },
                                        EmbeddedProperty.Builder().apply {
                                            name = "owner"
                                            typeName = "Person"
                                            propertyOverrides =
                                                arrayListOf(
                                                    PrimitiveProperty.Override.Builder().apply {
                                                        propertyName = "firstName"
                                                        column = "OWNER_FIRST_NAME"
                                                    },
                                                    PrimitiveProperty.Override.Builder().apply {
                                                        propertyName = "lastName"
                                                        column = "OWNER_LAST_NAME"
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
                                                    EmbeddedProperty.Override.Builder().apply {
                                                        propertyName = "passport"
                                                        propertyOverrides =
                                                            arrayListOf(
                                                                PrimitiveProperty.Override.Builder().apply {
                                                                    propertyName = "serial"
                                                                    column = "OWNER_PASSPORT_SERIAL"
                                                                },
                                                                PrimitiveProperty.Override.Builder().apply {
                                                                    propertyName = "number"
                                                                    column = "OWNER_PASSPORT_NUMBER"
                                                                },
                                                                PrimitiveCollectionProperty.Override.Builder().apply {
                                                                    propertyName = "states"
                                                                    table = "PRODUCT_OWNER_PASSPORT_STATES"
                                                                    ownerIdPropertyOverride =
                                                                        PrimitiveProperty.Override.Builder().apply {
                                                                            column = "PRODUCT_ID"
                                                                        }
                                                                    elementColumn = "STATE"
                                                                },
                                                                EmbeddedProperty.Override.Builder().apply {
                                                                    propertyName = "signature"
                                                                    propertyOverrides =
                                                                        arrayListOf(
                                                                            PrimitiveProperty.Override.Builder().apply {
                                                                                propertyName = "private"
                                                                                column = "OWNER_PASSPORT_SIGNATURE_PRIVATE"
                                                                            },
                                                                            PrimitiveProperty.Override.Builder().apply {
                                                                                propertyName = "public"
                                                                                column = "OWNER_PASSPORT_SIGNATURE_PUBLIC"
                                                                            },
                                                                        )
                                                                },
                                                            )
                                                    },
                                                    EmbeddedProperty.Override.Builder().apply {
                                                        propertyName = "birthCertificate"
                                                        propertyOverrides =
                                                            arrayListOf(
                                                                PrimitiveProperty.Override.Builder().apply {
                                                                    propertyName = "serial"
                                                                    column = "OWNER_BIRTH_CERTIFICATE_SERIAL"
                                                                },
                                                                PrimitiveProperty.Override.Builder().apply {
                                                                    propertyName = "number"
                                                                    column = "OWNER_BIRTH_CERTIFICATE_NUMBER"
                                                                },
                                                                PrimitiveCollectionProperty.Override.Builder().apply {
                                                                    propertyName = "states"
                                                                    table = "PRODUCT_OWNER_BIRTH_CERTIFICATE_STATES"
                                                                    ownerIdPropertyOverride =
                                                                        PrimitiveProperty.Override.Builder().apply {
                                                                            column = "PRODUCT_ID"
                                                                        }
                                                                    elementColumn = "STATE"
                                                                },
                                                                EmbeddedProperty.Override.Builder().apply {
                                                                    propertyName = "signature"
                                                                    propertyOverrides =
                                                                        arrayListOf(
                                                                            PrimitiveProperty.Override.Builder().apply {
                                                                                propertyName = "private"
                                                                                column = "OWNER_BIRTH_CERTIFICATE_SIGNATURE_PRIVATE"
                                                                            },
                                                                            PrimitiveProperty.Override.Builder().apply {
                                                                                propertyName = "public"
                                                                                column = "OWNER_BIRTH_CERTIFICATE_SIGNATURE_PUBLIC"
                                                                            },
                                                                        )
                                                                },
                                                            )
                                                    },
                                                )
                                        },
                                    )
                                idPropertyName = "id"
                                idStrategy = ManualIdStrategy
                                table = "PRODUCT"
                            },
                        )
                }.also {
                    assertThat(it.validate()).isEmpty()
                }.build()
    }

    @Test
    fun test2() {
        val model =
            Model
                .Builder()
                .apply {
                    name = "test"
                    types =
                        arrayListOf(
                            EmbeddableType.Builder().apply {
                                name = "Sign"
                                properties =
                                    arrayListOf(
                                        PrimitiveProperty.Builder().apply {
                                            name = "pr"
                                            column = "PR"
                                        },
                                        PrimitiveProperty.Builder().apply {
                                            name = "pub"
                                            column = "PUB"
                                        },
                                    )
                            },
                            EmbeddableType.Builder().apply {
                                name = "Doc"
                                properties =
                                    arrayListOf(
                                        PrimitiveProperty.Builder().apply {
                                            name = "ser"
                                            column = "DSER"
                                        },
                                        PrimitiveProperty.Builder().apply {
                                            name = "num"
                                            column = "DNUM"
                                        },
                                        EmbeddedProperty.Builder().apply {
                                            name = "sign"
                                            typeName = "Sign"
                                            propertyOverrides =
                                                arrayListOf(
                                                    PrimitiveProperty.Override.Builder().apply {
                                                        propertyName = "pub"
                                                        column = "DPUB"
                                                    },
                                                )
                                        },
                                    )
                            },
                            EmbeddableType.Builder().apply {
                                name = "Person"
                                properties =
                                    arrayListOf(
                                        PrimitiveProperty.Builder().apply {
                                            name = "sur"
                                            column = "PSUR"
                                        },
                                        PrimitiveProperty.Builder().apply {
                                            name = "name"
                                            column = "PNAME"
                                        },
                                        EmbeddedProperty.Builder().apply {
                                            name = "pass"
                                            typeName = "Doc"
                                            propertyOverrides =
                                                arrayListOf(
                                                    PrimitiveProperty.Override.Builder().apply {
                                                        propertyName = "ser"
                                                        column = "PSER"
                                                    },
                                                    EmbeddedProperty.Override.Builder().apply {
                                                        propertyName = "sign"
                                                        propertyOverrides =
                                                            arrayListOf(
                                                                PrimitiveProperty.Override.Builder().apply {
                                                                    propertyName = "pr"
                                                                    column = "PPR"
                                                                },
                                                            )
                                                    },
                                                )
                                        },
                                    )
                            },
                            EntityType.Builder().apply {
                                name = "Product"
                                properties =
                                    arrayListOf(
                                        EmbeddedProperty.Builder().apply {
                                            name = "owner"
                                            typeName = "Person"
                                            propertyOverrides =
                                                arrayListOf(
                                                    PrimitiveProperty.Override.Builder().apply {
                                                        propertyName = "name"
                                                        column = "ONAME"
                                                    },
                                                    EmbeddedProperty.Override.Builder().apply {
                                                        propertyName = "pass"
                                                        propertyOverrides =
                                                            arrayListOf(
                                                                PrimitiveProperty.Override.Builder().apply {
                                                                    propertyName = "num"
                                                                    column = "ONUM"
                                                                },
                                                                EmbeddedProperty.Override.Builder().apply {
                                                                    propertyName = "sign"
                                                                    propertyOverrides =
                                                                        arrayListOf(
                                                                            PrimitiveProperty.Override.Builder().apply {
                                                                                propertyName = "pub"
                                                                                column = "OPUB"
                                                                            },
                                                                        )
                                                                },
                                                            )
                                                    },
                                                )
                                        },
                                        PrimitiveCollectionProperty.Builder().apply {
                                            name = "states"
                                            table = "PRODUCT_STATES"
                                            ownerIdPropertyOverride =
                                                EmbeddedProperty.Override.Builder().apply {
                                                    propertyOverrides =
                                                        arrayListOf(
                                                            PrimitiveProperty.Override.Builder().apply {
                                                                propertyName = "sur"
                                                                column = "CSUR"
                                                            },
                                                            EmbeddedProperty.Override.Builder().apply {
                                                                propertyName = "pass"
                                                                propertyOverrides =
                                                                    arrayListOf(
                                                                        PrimitiveProperty.Override.Builder().apply {
                                                                            propertyName = "ser"
                                                                            column = "CSER"
                                                                        },
                                                                        EmbeddedProperty.Override.Builder().apply {
                                                                            propertyName = "sign"
                                                                            propertyOverrides =
                                                                                arrayListOf(
                                                                                    PrimitiveProperty.Override.Builder().apply {
                                                                                        propertyName = "pr"
                                                                                        column = "CPR"
                                                                                    },
                                                                                )
                                                                        },
                                                                    )
                                                            },
                                                        )
                                                }
                                            elementColumn = "STATE"
                                        },
                                        EmbeddedProperty.Builder().apply {
                                            name = "contract"
                                            typeName = "Contract"
                                        },
                                    )
                                idPropertyName = "owner"
                                idStrategy = ManualIdStrategy
                                table = "PRODUCT"
                            },
                            EmbeddableType.Builder().apply {
                                name = "Contract"
                                properties =
                                    arrayListOf(
                                        PrimitiveCollectionProperty.Builder().apply {
                                            name = "codes"
                                            table = "CODES"
//                                            TODO??? Ошибку выдает
//                                            ownerIdPropertyOverride =
//                                                EmbeddedProperty.Override.Builder().apply {
//                                                    propertyOverrides =
//                                                        arrayListOf(
//                                                            PrimitiveProperty.Override.Builder().apply {
//                                                                name = "name"
//                                                                column =
//                                                                    Column.Builder().apply {
//                                                                        name = "C2NAME"
//                                                                    }
//                                                            },
//                                                        )
//                                                }
                                            elementColumn = "CODE"
                                        },
                                    )
                            },
                        )
                }.also {
                    assertThat(it.validate()).isEmpty()
                }.build()
    }
}
