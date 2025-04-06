package ru.sbertech.dataspace.uow.command

import com.fasterxml.jackson.databind.node.ObjectNode
import ru.sbertech.dataspace.entitymanager.selector.Selector
import ru.sbertech.dataspace.expr.Expr

class Selection(
    private val entityType: String,
    private val selectorByName: Map<String, Selector>,
    val queryNode: ObjectNode,
) {
    fun build(condition: Expr?): Selector = Selector.EntityCollectionBased(entityType, selectorByName, condition)
}
