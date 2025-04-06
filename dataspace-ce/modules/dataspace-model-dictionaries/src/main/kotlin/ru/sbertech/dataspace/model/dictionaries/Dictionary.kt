package ru.sbertech.dataspace.model.dictionaries

import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.type.EntityType

class Dictionary {
    lateinit var type: EntityType
        private set

    class Builder {
        var name: String? = null

        fun build(model: Model): Dictionary {
            val dictionary = Dictionary()

            dictionary.type = model.type(name!!) as EntityType

            return dictionary
        }
    }
}
