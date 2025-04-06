package ru.sbertech.dataspace.graphql.schema.datafetcher

import graphql.GraphQLError
import graphql.GraphqlErrorException.Builder
import graphql.execution.DataFetcherResult
import graphql.language.Field
import graphql.schema.DataFetchingEnvironment
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.sbertech.dataspace.entitymanager.EntityManagerFactory
import ru.sbertech.dataspace.expr.Expr
import ru.sbertech.dataspace.grammar.Grammar
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.ELEMENTS_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.SEARCH_FIELD_NAME_PREFIX
import ru.sbertech.dataspace.graphql.selection.SelectionResultCreatingVisitor
import ru.sbertech.dataspace.graphql.selection.aliasOrName
import ru.sbertech.dataspace.graphql.selection.getCondition
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.security.graphql.SecurityRulesFetcher
import ru.sbertech.dataspace.security.utils.GraphQLSecurityContext
import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.universalvalue.accept
import ru.sbertech.dataspace.uow.command.Selection
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper
import sbp.com.sbt.dataspace.graphqlschema.datafetcher.SecureDataFetcher
import sbp.sbt.dataspacecore.security.utils.SecurityUtils
import javax.sql.DataSource

class SearchFieldsDataFetcher(
    private val model: Model,
    private val entityManagerFactory: EntityManagerFactory,
    private val dataSource: DataSource,
    private val grammar: Grammar<Expr>,
    graphQLDataFetcherHelper: GraphQLDataFetcherHelper,
    securityRulesFetcher: SecurityRulesFetcher?,
) : SecureDataFetcher(graphQLDataFetcherHelper, securityRulesFetcher) {
    override fun get(
        environment: DataFetchingEnvironment,
        securityContext: GraphQLSecurityContext?,
    ): Any {
        val selectionResult: UniversalValue?
        val errors = arrayListOf<GraphQLError>()

        dataSource.connection.use { connection ->
            try {
                val searchField = environment.field

                val typeName = searchField.name.substring(SEARCH_FIELD_NAME_PREFIX.length)
                val entityType = model.type(typeName) as EntityType

                val elemsField = searchField.selectionSet.getSelectionsOfType(Field::class.java).first { it.name == ELEMENTS_FIELD_NAME }

                var securityCondition: String? = null
                if (securityContext?.secureOperation?.pathConditions != null) {
                    val pathConditions = securityContext.secureOperation!!.pathConditions
                    securityCondition = pathConditions!!["/${searchField.aliasOrName}"]?.cond
                }

                // TODO  нужно заменить объединение условий на что-то физеровское
                val conditionStr =
                    SecurityUtils.addSecurityCondition(
                        searchField.getCondition(),
                        securityCondition,
                    )
                val condition =
                    if (conditionStr != null) {
                        grammar.parse(conditionStr)
                    } else {
                        null
                    }

//                val dataFetcherContext = DataFetcherContext(environment, securityContext, DataFetcherStep(searchField))
                // TODO LEGACY
//                val selection = SelectionFactory.createSelection(entityType, elemsField, grammar)
                val selection: Selection = null!!

                val entityManager = entityManagerFactory.create(connection)
                selectionResult = entityManager.select(selection.build(condition))?.accept(SelectionResultCreatingVisitor)

                connection.commit()

                return DataFetcherResult
                    .newResult<Any>()
                    .data(selectionResult)
                    .build()
            } catch (e: Exception) {
                LOGGER.error(e.stackTraceToString())
                errors.add(Builder().message(e.message).build())
                connection.rollback()

                return DataFetcherResult
                    .newResult<Any>()
                    .errors(errors)
                    .build()
            }
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(SearchFieldsDataFetcher::class.java)
    }
}
