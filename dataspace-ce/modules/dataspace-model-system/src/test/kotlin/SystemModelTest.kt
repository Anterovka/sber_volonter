import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.model.system.SystemModel
import ru.sbertech.dataspace.model.system.SystemPropertiesForEntity
import ru.sbertech.dataspace.model.system.extension.EntityTypeExtension
import ru.sbertech.dataspace.model.system.extension.Index
import ru.sbertech.dataspace.model.system.isSystem
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.primitive.type.PrimitiveType

class SystemModelTest {
    private val model =
        Model
            .Builder()
            .apply {
                name = "model"
                types =
                    mutableListOf(
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
                                        name = "systemProperty1"
                                        type = PrimitiveType.String
                                        column = "systemProperty1"
                                    },
                                    PrimitiveProperty.Builder().apply {
                                        name = "systemProperty2"
                                        type = PrimitiveType.String
                                        column = "systemProperty2"
                                    },
                                )
                            idPropertyName = "id"
                            table = "PRODUCT"
                        },
                        EntityType.Builder().apply {
                            name = "SystemEntity"
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
                            table = "SYSTEM_ENTITY"
                        },
                    )
            }.build()

    init {
        SystemModel
            .Builder()
            .apply {
                entityTypeExtensions =
                    mutableListOf(
                        EntityTypeExtension.Builder().apply {
                            typeName = "SystemEntity"
                            isSystem = true
                            indexes =
                                mutableListOf(
                                    Index.Builder().apply {
                                        name = "systemProperty1UniqIndex"
                                        isUnique = true
                                        properties = mutableListOf("systemProperty1")
                                    },
                                )
                        },
                    )
                systemPropertiesForEntities =
                    mutableListOf(
                        SystemPropertiesForEntity.Builder().apply {
                            entity = "Product"
                            systemProperties = mutableListOf("systemProperty1", "systemProperty2")
                        },
                    )
            }.build(model)
    }

    @Test
    fun test() {
        val product = model.type("Product") as EntityType
        val systemEntity = model.type("SystemEntity") as EntityType

        assertThat(product.isSystem).isFalse()
        assertThat(systemEntity.isSystem).isTrue()

        assertThat(product.property("id").isSystem).isFalse()
        assertThat(product.property("systemProperty1").isSystem).isTrue()
        assertThat(product.property("systemProperty2").isSystem).isTrue()
    }
}
