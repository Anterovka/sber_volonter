package ru.sbertech.dataspace.graphql.command

import graphql.language.Field
import graphql.schema.DataFetchingEnvironment
import graphql.schema.SelectedField
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.ID_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.INC_FAIL_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.INC_NEGATIVE_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.INC_OPERATION_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.INC_VALUE_FIELD_NAME
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.uow.command.Command
import ru.sbertech.dataspace.uow.command.Increment
import ru.sbertech.dataspace.uow.command.IncrementCheck
import ru.sbertech.dataspace.uow.packet.CommandRefContext
import ru.sbertech.dataspace.uow.packet.depends.DependsOn

abstract class CommandFactory(
    protected val entityType: EntityType,
) {
    abstract fun addCommand(
        field: Field,
        selectedField: SelectedField,
        commandByQualifier: LinkedHashMap<String, Command>,
        commandRefContext: CommandRefContext,
        environment: DataFetchingEnvironment,
        dependsOn: List<DependsOn>,
    )

    protected fun fillPropertyValuesByArgument(
        argument: Map<String, Any?>,
        propertyValueByName: LinkedHashMap<String, UniversalValue?>,
        propertyValuesFillingVisitor: PropertyValuesFillingVisitor,
    ) {
        argument.forEach {
            val property =
                if (it.key == ID_FIELD_NAME) {
                    entityType.tableIdProperty
                } else {
                    entityType.inheritedPersistableProperty(it.key)
                }

            propertyValueByName[property.name] = property.accept(propertyValuesFillingVisitor, it.value)
        }
    }

    protected fun fillIncrements(
        incArgument: Map<String, Any?>,
        increments: ArrayList<Increment>,
    ) {
        incArgument.forEach {
            val fieldName = it.key
            val incrementDataAsMap = it.value as Map<String, Any?>
            val delta = incrementDataAsMap[INC_VALUE_FIELD_NAME]!! as Number
            val isNegative = incrementDataAsMap[INC_NEGATIVE_FIELD_NAME] as Boolean? ?: false
            val failDataAsMap = incrementDataAsMap[INC_FAIL_FIELD_NAME] as Map<String, Any>?
            val incrementCheck =
                failDataAsMap?.let { failData ->
                    val failValue = failData.getValue(INC_VALUE_FIELD_NAME) as Number
                    val failOperationCode = failData.getValue(INC_OPERATION_FIELD_NAME) as String
                    IncrementCheck(failValue, IncrementCheck.Operator.getByCode(failOperationCode))
                }
            increments.add(Increment(fieldName, delta, isNegative, incrementCheck))
        }
    }
}
