package ru.sbertech.dataspace.graphql.schema.datafetcher

import graphql.GraphQLError
import graphql.GraphqlErrorException.Builder
import graphql.execution.DataFetcherResult
import graphql.language.EnumValue
import graphql.language.Field
import graphql.language.StringValue
import graphql.schema.DataFetchingEnvironment
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.sbertech.dataspace.entitymanager.EntityManagerFactory
import ru.sbertech.dataspace.graphql.command.CommandFactory
import ru.sbertech.dataspace.graphql.command.GraphQLCommandRefContext
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.AGGREGATE_VERSION_ARGUMENT_NAME
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.COMMAND_ID_ARGUMENT_NAME
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.DEPENDENCY_ARGUMENT_NAME
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.IDEMPOTENCE_PACKET_ID_ARGUMENT_NAME
import ru.sbertech.dataspace.graphql.schema.utils.Directives.DEPENDS_ON_BY_GET_DIRECTIVE_NAME
import ru.sbertech.dataspace.graphql.schema.utils.Directives.DEPENDS_ON_BY_UPDATE_OR_CREATE_DIRECTIVE_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.AGGREGATE_VERSION_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.IS_IDEMPOTENCE_RESPONSE_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.TYPE_NAME_FIELD_NAME
import ru.sbertech.dataspace.graphql.selection.SelectionResultCreatingVisitor
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.security.graphql.SecurityRulesFetcher
import ru.sbertech.dataspace.security.utils.GraphQLSecurityContext
import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.universalvalue.accept
import ru.sbertech.dataspace.uow.command.Command
import ru.sbertech.dataspace.uow.packet.Packet
import ru.sbertech.dataspace.uow.packet.aggregate.AggregateVersion
import ru.sbertech.dataspace.uow.packet.depends.DependsOn
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper
import sbp.com.sbt.dataspace.graphqlschema.datafetcher.SecureDataFetcher
import javax.sql.DataSource

class PacketFieldsDataFetcher(
    private val model: Model,
    private val commandFactoryByFieldName: Map<String, CommandFactory>,
    private val entityManagerFactory: EntityManagerFactory,
    private val dataSource: DataSource,
    private val isManyAggregatesAllowed: Boolean,
    graphQLDataFetcherHelper: GraphQLDataFetcherHelper,
    securityRulesFetcher: SecurityRulesFetcher?,
) : SecureDataFetcher(graphQLDataFetcherHelper, securityRulesFetcher) {
    override fun get(
        environment: DataFetchingEnvironment,
        securityContext: GraphQLSecurityContext?,
    ): Any {
        val selectionResult = hashMapOf<String, UniversalValue?>()
        val errors = arrayListOf<GraphQLError>()

        val packetField = environment.field
        val packetQualifier = packetField.alias ?: packetField.name
        val commandRefContext = GraphQLCommandRefContext()

        try {
            dataSource.connection.use { connection ->
                try {
                    val idempotenceId =
                        environment.arguments[IDEMPOTENCE_PACKET_ID_ARGUMENT_NAME] as String?
                    val aggregateVersion =
                        environment.arguments[AGGREGATE_VERSION_ARGUMENT_NAME] as Long?

                    val isAggregateVersionRequested =
                        packetField.selectionSet.getSelectionsOfType(Field::class.java).any { it.name == AGGREGATE_VERSION_FIELD_NAME }

                    val commandByQualifier = linkedMapOf<String, Command>()
                    packetField.selectionSet
                        .getSelectionsOfType(Field::class.java)
                        .filter {
                            it.name !in
                                listOf(
                                    IS_IDEMPOTENCE_RESPONSE_FIELD_NAME,
                                    AGGREGATE_VERSION_FIELD_NAME,
                                    TYPE_NAME_FIELD_NAME,
                                )
                        }.forEach { commandField ->
                            val selectedField =
                                environment.selectionSet.immediateFields.first { selectedField ->
                                    (selectedField.alias ?: selectedField.qualifiedName) ==
                                        (commandField.alias ?: commandField.name)
                                }
                            val dependsOn = handleDependsOnDirectives(commandField)
                            val commandFactory =
                                commandFactoryByFieldName[commandField.name]
                                    ?: throw IllegalStateException("CommandFactory not defined for field ${commandField.name}")
                            commandFactory.addCommand(
                                commandField,
                                selectedField,
                                commandByQualifier,
                                commandRefContext,
                                environment,
                                dependsOn,
                            )
                        }

                    val packet =
                        Packet(
                            model,
                            idempotenceId,
                            AggregateVersion(aggregateVersion, isAggregateVersionRequested),
                            entityManagerFactory.create(connection),
                            commandByQualifier,
                            commandRefContext,
                            graphQLDataFetcherHelper.entitiesReadAccessJson,
                            isManyAggregatesAllowed,
                        )

                    val packetExecutionResult = packet.execute(connection)

                    selectionResult[IS_IDEMPOTENCE_RESPONSE_FIELD_NAME] = packetExecutionResult.isIdempotenceResponse
                    selectionResult[AGGREGATE_VERSION_FIELD_NAME] = packetExecutionResult.aggregateVersion

                    commandByQualifier.forEach {
                        val commandQualifier = it.key
                        val command = it.value

                        val gqlSelectionResult =
                            packetExecutionResult.commandResultByQualifier[commandQualifier]?.selectionResult?.let { result ->
                                if (command !is Command.Many) {
                                    return@let result.accept(SelectionResultCreatingVisitor)
                                }
                                result
                            }

                        selectionResult[commandQualifier] = gqlSelectionResult
                    }

                    connection.commit()

                    return DataFetcherResult
                        .newResult<Any>()
                        .data(selectionResult)
                        .build()
                } catch (e: Exception) {
                    connection.rollback()
                    throw e
                }
            }
        } catch (e: Exception) {
            LOGGER.error(e.stackTraceToString())
            errors.add(Builder().message("Packet '$packetQualifier' execution error: ${e.message}").build())

            return DataFetcherResult
                .newResult<Any>()
                .errors(errors)
                .build()
        }
    }

    private fun handleDependsOnDirectives(commandField: Field): ArrayList<DependsOn> {
        val dependsOn = arrayListOf<DependsOn>()
        commandField.directives.forEach { directive ->
            when (directive.name) {
                DEPENDS_ON_BY_GET_DIRECTIVE_NAME -> {
                    val commandId = (directive.getArgument(COMMAND_ID_ARGUMENT_NAME).value as StringValue).value
                    val dependency = (directive.getArgument(DEPENDENCY_ARGUMENT_NAME).value as EnumValue).name

                    dependsOn.add(DependsOn.Get(commandId, DependsOn.Dependency.valueOf(dependency)))
                }

                DEPENDS_ON_BY_UPDATE_OR_CREATE_DIRECTIVE_NAME -> {
                    val commandId = (directive.getArgument(COMMAND_ID_ARGUMENT_NAME).value as StringValue).value
                    val dependency = (directive.getArgument(DEPENDENCY_ARGUMENT_NAME).value as EnumValue).name

                    dependsOn.add(DependsOn.UpdateOrCreate(commandId, DependsOn.Dependency.valueOf(dependency)))
                }

                else -> {
                    // do nothing
                }
            }
        }
        return dependsOn
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(PacketFieldsDataFetcher::class.java)
    }
}
