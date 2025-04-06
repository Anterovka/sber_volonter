package ru.sbertech.dataspace.graphql.schema.utils

import graphql.introspection.Introspection
import graphql.schema.GraphQLDirective
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.commandIdArgument
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.dependencyOnByGetArgument
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.dependencyOnByUpdateOrCreateArgument

object Directives {
    const val DEPENDS_ON_BY_GET_DIRECTIVE_NAME = "dependsOnByGet"
    const val DEPENDS_ON_BY_UPDATE_OR_CREATE_DIRECTIVE_NAME = "dependsOnByUpdateOrCreate"

    val dependsOnByGetDirective: GraphQLDirective =
        GraphQLDirective
            .newDirective()
            .name(DEPENDS_ON_BY_GET_DIRECTIVE_NAME)
            .argument(commandIdArgument)
            .argument(dependencyOnByGetArgument)
            .repeatable(true)
            .validLocation(Introspection.DirectiveLocation.FIELD)
            .build()

    val dependsOnByUpdateOrCreateDirective: GraphQLDirective =
        GraphQLDirective
            .newDirective()
            .name(DEPENDS_ON_BY_UPDATE_OR_CREATE_DIRECTIVE_NAME)
            .argument(commandIdArgument)
            .argument(dependencyOnByUpdateOrCreateArgument)
            .repeatable(true)
            .validLocation(Introspection.DirectiveLocation.FIELD)
            .build()
}
