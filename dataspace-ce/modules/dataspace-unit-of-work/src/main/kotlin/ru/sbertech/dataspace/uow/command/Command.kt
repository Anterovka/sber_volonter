package ru.sbertech.dataspace.uow.command

import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.uow.packet.depends.DependsOn

data class CommandExecutionResult(
    val identifier: UniversalValue?,
    val selectionResult: UniversalValue?,
    val command: Command,
)

data class ExternalReferencesCollection(
    val entityType: EntityType,
    val clear: Boolean,
    val add: List<LinkedHashMap<String, UniversalValue?>>,
    val remove: List<LinkedHashMap<String, UniversalValue?>>,
)

sealed class Command {
    abstract val qualifier: String
    abstract val entityType: EntityType
    abstract val dependsOn: List<DependsOn>

    abstract fun <P, R> accept(
        visitor: CommandParameterizedVisitor<P, R>,
        param: P,
    ): R

    fun <R> accept(visitor: CommandVisitor<R>): R = accept(visitor, Unit)

    data class Create(
        override val qualifier: String,
        override val entityType: EntityType,
        val selection: Selection?,
        val propertyValueByName: LinkedHashMap<String, UniversalValue?>,
        override val dependsOn: List<DependsOn>,
        private val parentLazy: Lazy<Command?> = lazy(LazyThreadSafetyMode.NONE) { null },
    ) : Command() {
        val parent: Command? by parentLazy

        override fun <P, R> accept(
            visitor: CommandParameterizedVisitor<P, R>,
            param: P,
        ): R = visitor.visit(this, param)
    }

    data class Update(
        override val qualifier: String,
        override val entityType: EntityType,
        val selection: Selection?,
        val propertyValueByName: LinkedHashMap<String, UniversalValue?>,
        val propertyValueByNameForCompare: LinkedHashMap<String, UniversalValue?>,
        val increments: List<Increment>,
        override val dependsOn: List<DependsOn>,
        private val parentLazy: Lazy<Command?> = lazy(LazyThreadSafetyMode.NONE) { null },
    ) : Command() {
        val parent: Command? by parentLazy

        override fun <P, R> accept(
            visitor: CommandParameterizedVisitor<P, R>,
            param: P,
        ): R = visitor.visit(this, param)
    }

    data class Delete(
        override val qualifier: String,
        override val entityType: EntityType,
        val identifier: UniversalValue,
        val propertyValueByNameForCompare: LinkedHashMap<String, UniversalValue?>,
        override val dependsOn: List<DependsOn>,
        val parent: Command? = null,
    ) : Command() {
        override fun <P, R> accept(
            visitor: CommandParameterizedVisitor<P, R>,
            param: P,
        ): R = visitor.visit(this, param)
    }

    data class UpdateOrCreate(
        override val qualifier: String,
        override val entityType: EntityType,
        val createCommand: Create,
        val updateCommand: Update,
        val selection: Selection?,
        override val dependsOn: List<DependsOn>,
        val byKey: String? = null,
        val parent: Command? = null,
    ) : Command() {
        override fun <P, R> accept(
            visitor: CommandParameterizedVisitor<P, R>,
            param: P,
        ): R = visitor.visit(this, param)
    }

    data class Get(
        override val qualifier: String,
        override val entityType: EntityType,
        val identifier: UniversalValue?,
        val condition: String?,
//        val condition: Expr?,
        val selection: Selection,
        val failOnEmpty: Boolean,
        val lockMode: LockMode,
        override val dependsOn: List<DependsOn>,
    ) : Command() {
        override fun <P, R> accept(
            visitor: CommandParameterizedVisitor<P, R>,
            param: P,
        ): R = visitor.visit(this, param)
    }

    data class Many(
        override val qualifier: String,
        override val entityType: EntityType,
        val commands: List<Command>,
        override val dependsOn: List<DependsOn>,
    ) : Command() {
        override fun <P, R> accept(
            visitor: CommandParameterizedVisitor<P, R>,
            param: P,
        ): R = visitor.visit(this, param)

        override fun toString(): String =
            "Many(qualifier='$qualifier', entityType=${entityType.name}, commandsCount=${commands.size}, dependsOn=$dependsOn)"
    }
}
