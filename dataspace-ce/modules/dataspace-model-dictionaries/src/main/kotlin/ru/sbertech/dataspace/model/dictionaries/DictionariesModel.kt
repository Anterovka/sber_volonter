package ru.sbertech.dataspace.model.dictionaries

import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.type.EntityType
import java.util.WeakHashMap

private val dictionariesModelExtension = WeakHashMap<Any, Any>()

val EntityType.isDictionary get(): Boolean = dictionariesModelExtension.containsKey(this)
val Model.hasDictionariesExtension get() = dictionariesModelExtension.containsKey(this)

class DictionariesModel {
    class Builder {
        var dictionaries: MutableCollection<Dictionary.Builder>? = null

        fun build(model: Model): DictionariesModel {
            val dictionariesModel = DictionariesModel()

            dictionaries?.forEach {
                val dictionary = it.build(model)

                dictionariesModelExtension[dictionary.type] = dictionary
            }

            dictionariesModelExtension[model] = dictionariesModel
            return dictionariesModel
        }
    }
}
