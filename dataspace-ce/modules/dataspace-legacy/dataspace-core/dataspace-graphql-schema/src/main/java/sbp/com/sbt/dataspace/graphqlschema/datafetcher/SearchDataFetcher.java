package sbp.com.sbt.dataspace.graphqlschema.datafetcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import graphql.GraphQLError;
import graphql.language.Field;
import graphql.schema.DataFetchingEnvironment;
import org.jetbrains.annotations.NotNull;
import ru.sbertech.dataspace.security.graphql.SecurityRulesFetcher;
import ru.sbertech.dataspace.security.utils.GraphQLSecurityContext;
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJsonHelper;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.graphqlschema.DataFetcherContainer;
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper;
import sbp.com.sbt.dataspace.graphqlschema.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static graphql.execution.DataFetcherResult.newResult;
import static sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper.SEARCH_FIELD_NAME_PREFIX;

/**
 * Data loader for search
 */
public class SearchDataFetcher extends SecureDataFetcher {

    /**
     * @param graphQLDataFetcherHelper Helper for GraphQL data loader
     */
    public SearchDataFetcher(GraphQLDataFetcherHelper graphQLDataFetcherHelper,
                             SecurityRulesFetcher securityRulesFetcher) {
        super(graphQLDataFetcherHelper, securityRulesFetcher);
    }

    /**
     * Get request node
     *
     * @param environment Environment
     */
    JsonNode getRequestNode(DataFetchingEnvironment environment, GraphQLSecurityContext variablesContainer) {
//Request for Feather
        ObjectNode featherRequest = Helper.OBJECT_MAPPER.createObjectNode();
        Field queryField = environment.getField();
// Getting the field name without the search prefix
        String typeName = queryField.getName().substring(SEARCH_FIELD_NAME_PREFIX.length());
        EntityDescription entityDescription = graphQLDataFetcherHelper.getModelDescription().getEntityDescription(typeName);
        // entityDescription.getName() == typeName
        featherRequest.put(EntitiesReadAccessJsonHelper.TYPE_FIELD_NAME, entityDescription.getName());
        DataFetcherContainer dataFetcherContainer = new DataFetcherContainer(environment, variablesContainer, null);

        dataFetcherContainer.addStep(queryField, entityDescription);
        graphQLDataFetcherHelper.processSpecificationArguments(featherRequest, dataFetcherContainer, queryField.getArguments());
        graphQLDataFetcherHelper.processEntitiesCollection(entityDescription, featherRequest, dataFetcherContainer, queryField.getSelectionSet());
        dataFetcherContainer.removeStep();
        graphQLDataFetcherHelper.postProcessNode(featherRequest);
        return featherRequest;
    }

    @Override
    public @NotNull Object get(DataFetchingEnvironment environment, GraphQLSecurityContext securityContext) {
        List<GraphQLError> errors = new ArrayList<>();
        Object data = graphQLDataFetcherHelper.getEntitiesCollection(
            errors,
            Collections.singletonList(environment.getField().getName()),
            graphQLDataFetcherHelper.getEntitiesReadAccessJson().searchEntities(
                getRequestNode(environment, securityContext))
        );
        return newResult()
            .data(data)
            .errors(errors)
            .build();
    }
}
