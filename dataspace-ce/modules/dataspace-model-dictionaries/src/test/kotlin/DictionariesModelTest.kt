import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.dictionaries.DictionariesModel
import ru.sbertech.dataspace.model.dictionaries.Dictionary
import ru.sbertech.dataspace.model.dictionaries.isDictionary
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.primitive.type.PrimitiveType

class DictionariesModelTest {
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
                                )
                            idPropertyName = "id"
                            table = "PRODUCT"
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
                                )
                            idPropertyName = "id"
                            table = "CURRENCY"
                        },
                        EntityType.Builder().apply {
                            name = "Unit"
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
                                )
                            idPropertyName = "id"
                            table = "UNIT"
                        },
                    )
            }.build()

    init {
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
                        Dictionary
                            .Builder()
                            .apply {
                                name = "Unit"
                            },
                    )
            }.build(model)
    }

    @Test
    fun test() {
        assertThat((model.type("Currency") as EntityType).isDictionary).isTrue()
        assertThat((model.type("Unit") as EntityType).isDictionary).isTrue()
        assertThat((model.type("Product") as EntityType).isDictionary).isFalse()
    }
}
