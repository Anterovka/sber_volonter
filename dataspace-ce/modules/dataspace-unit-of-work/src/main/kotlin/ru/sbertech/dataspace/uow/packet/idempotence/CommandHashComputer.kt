package ru.sbertech.dataspace.uow.packet.idempotence

import ru.sbertech.dataspace.common.forEachSeparated
import ru.sbertech.dataspace.primitive.Primitive
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.universalvalue.UniversalValueVisitor
import ru.sbertech.dataspace.universalvalue.accept
import ru.sbertech.dataspace.uow.command.Command
import ru.sbertech.dataspace.uow.command.ExternalReferencesCollection
import java.math.BigInteger
import java.security.MessageDigest

object CommandHashComputer {
    private val md = MessageDigest.getInstance("MD5")

    private fun getHash(paramsString: StringBuilder): String =
        BigInteger(1, md.digest(paramsString.toString().toByteArray())).toString(16).padStart(32, '0')

    fun compute(command: Command): String {
        val paramsString = StringBuilder()
        paramsString.append(command::class.simpleName).append(command.entityType.name)
        val serializingVisitor = SerializingVisitor(paramsString)

        when (command) {
            is Command.Create -> {
                command.propertyValueByName.accept(serializingVisitor)
            }

            is Command.Get -> {
                val identifier = command.identifier ?: command.condition
                val params = linkedMapOf("id" to identifier, "failOnEmpty" to command.failOnEmpty)
                params.accept(serializingVisitor)
            }

            is Command.Update -> {
                command.propertyValueByName.accept(serializingVisitor)
                paramsString.append(" compare: ")
                command.propertyValueByNameForCompare.accept(serializingVisitor)
                paramsString.append(" increments: ")
                command.increments.accept(serializingVisitor)
            }

            is Command.Delete -> {
                paramsString.append(" id: ")
                command.identifier.accept(serializingVisitor)
                paramsString.append(" compare: ")
                command.propertyValueByNameForCompare.accept(serializingVisitor)
            }

            is Command.UpdateOrCreate -> {
                paramsString
                    .append(compute(command.createCommand))
                    .append(" update: ")
                    .append(compute(command.updateCommand))
                command.byKey?.also {
                    paramsString.append(" byKey: ")
                    it.accept(serializingVisitor)
                }
            }

            is Command.Many -> {
                command.commands.forEach {
                    paramsString.append(compute(it))
                }
            }
        }
        paramsString.append(" dependsOn: ")
        command.dependsOn.accept(serializingVisitor)

        return getHash(paramsString)
    }

    internal class SerializingVisitor(
        private val stringBuilder: StringBuilder,
    ) : UniversalValueVisitor<Unit> {
        override fun visit(
            object0: Map<String, UniversalValue?>,
            param: Unit,
        ) {
            object0.toSortedMap(compareBy { it }).let {
                stringBuilder.append("{ ")
                it.entries.forEachSeparated(stringBuilder, ", ") { (key, value) ->
                    stringBuilder.append('\'').append(key).append("' : ")
                    when (value) {
                        is ExternalReferencesCollection -> {
                            val serializingResult = StringBuilder()
                            val serializingVisitor = SerializingVisitor(serializingResult)
                            serializingResult.append(" clear: ").append(value.clear)
                            serializingResult.append(", add: ")
                            value.add.accept(serializingVisitor)
                            serializingResult.append(", remove: ")
                            value.remove.accept(serializingVisitor)
                            stringBuilder.append(serializingResult)
                        }

                        else -> value?.accept(this) ?: stringBuilder.append("null")
                    }
                }
                stringBuilder.append(" }")
            } ?: stringBuilder.append("null")
        }

        override fun visit(
            collection: Collection<UniversalValue?>,
            param: Unit,
        ) {
            collection.let {
                stringBuilder.append('[')
                collection.forEachSeparated(stringBuilder, ", ") { it?.toString()?.accept(this) ?: stringBuilder.append("null") }
                stringBuilder.append(']')
            } ?: stringBuilder.append("null")
        }

        override fun visit(
            type: PrimitiveType,
            value: Primitive,
            param: Unit,
        ) {
            stringBuilder.append('\'').append(value).append('\'')
        }
    }
}
