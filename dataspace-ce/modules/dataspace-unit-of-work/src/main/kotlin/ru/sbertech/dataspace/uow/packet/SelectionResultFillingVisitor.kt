package ru.sbertech.dataspace.uow.packet

import ru.sbertech.dataspace.entitymanager.selector.Selector
import ru.sbertech.dataspace.entitymanager.selector.SelectorVisitor
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.universalvalue.UniversalValue

class SelectionResultFillingVisitor(
    private val entityType: EntityType,
    private val propertyValueByName: LinkedHashMap<String, UniversalValue?>,
    private val idempotenceContextIsRestored: Boolean,
) : SelectorVisitor<UniversalValue?> {
    private var enoughDataForResult = true

    override fun visit(
        entityCollectionBasedSelector: Selector.EntityCollectionBased,
        param: Unit,
    ): UniversalValue? {
        val selectionResult = linkedMapOf<String, UniversalValue?>()
        entityCollectionBasedSelector.selectorByName.forEach {
            if (it.value is Selector.EntityCollectionBased || !enoughDataForResult) {
                return null
            } else {
                selectionResult[it.key] = it.value.accept(this)
            }
        }
        return if (enoughDataForResult) selectionResult else return null
    }

    override fun visit(
        propertyBasedSelector: Selector.PropertyBased,
        param: Unit,
    ): UniversalValue? {
        // TODO embedded id
        if (idempotenceContextIsRestored && !entityType.inheritedPersistableProperty(propertyBasedSelector.name).isId) {
            enoughDataForResult = false
        }

        if (!propertyValueByName.containsKey(propertyBasedSelector.name) || propertyBasedSelector.cond != null) {
            enoughDataForResult = false
        }
        return propertyValueByName[propertyBasedSelector.name]
    }

    override fun visit(
        approximateType: Selector.ApproximateType,
        param: Unit,
    ): UniversalValue = entityType.name

    override fun visit(
        group: Selector.Group,
        param: Unit,
    ): UniversalValue? {
        enoughDataForResult = false
        return null
    }
}
