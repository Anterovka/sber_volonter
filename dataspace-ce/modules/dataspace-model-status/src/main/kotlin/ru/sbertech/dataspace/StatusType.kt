package ru.sbertech.dataspace

import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.property.Property
import ru.sbertech.dataspace.model.type.EmbeddableType

class StatusType {
    lateinit var type: EmbeddableType
        private set
    lateinit var codeProperty: Property
        private set
    lateinit var reasonProperty: Property
        private set

    override fun toString(): String =
        "StatusType(type=${type.name}, codeProperty=${codeProperty.name}, reasonProperty=${reasonProperty.name})"

    class Builder {
        var type: String? = null
        var codeProperty: String? = null

        var reasonProperty: String? = null

        fun build(model: Model): StatusType {
            val statusType = StatusType()

            val embeddableType = model.type(type!!) as EmbeddableType
            statusType.type = embeddableType

            statusType.codeProperty = embeddableType.property(codeProperty!!)
            statusType.reasonProperty = embeddableType.property(reasonProperty!!)

            return statusType
        }
    }
}
