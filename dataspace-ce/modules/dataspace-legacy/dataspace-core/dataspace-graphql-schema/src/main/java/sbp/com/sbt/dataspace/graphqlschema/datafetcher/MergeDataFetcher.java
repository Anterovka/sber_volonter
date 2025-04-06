package sbp.com.sbt.dataspace.graphqlschema.datafetcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import graphql.GraphQLError;
import graphql.GraphQLException;
import graphql.language.Directive;
import graphql.language.Field;
import graphql.language.InlineFragment;
import graphql.language.SelectionSet;
import graphql.language.TypeName;
import graphql.schema.DataFetchingEnvironment;
import ru.sbertech.dataspace.security.graphql.SecurityRulesFetcher;
import ru.sbertech.dataspace.security.utils.GraphQLSecurityContext;
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJsonHelper;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.graphqlschema.DataFetcherContainer;
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper;
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper;
import sbp.com.sbt.dataspace.graphqlschema.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static graphql.execution.DataFetcherResult.newResult;

/**
 * Data loader for merging requests
 */
public class MergeDataFetcher extends SecureDataFetcher {

    /**
     * @param graphQLDataFetcherHelper Helper for GraphQL data loader
     */
    public MergeDataFetcher(SecurityRulesFetcher securityRulesFetcher, GraphQLDataFetcherHelper graphQLDataFetcherHelper) {
        super(graphQLDataFetcherHelper, securityRulesFetcher);
    }

    /**
     *
     * Process the request for merging
     *
     * @param processedEntityDescriptions Processed entity descriptions
     * @param mergeRequestsNode           The node for merging requests
     * @param dataFetcherContainer        Environment
     * @param inlineFragment               Inline fragment
     */
    void processMergeRequest(Set<EntityDescription> processedEntityDescriptions, ArrayNode mergeRequestsNode, DataFetcherContainer dataFetcherContainer, InlineFragment inlineFragment) {
        TypeName typeName = inlineFragment.getTypeCondition();
        graphQLDataFetcherHelper.checkEntityInterface(typeName);
        EntityDescription entityDescription = graphQLDataFetcherHelper.getModelDescription().getEntityDescription(graphQLDataFetcherHelper.getEntityType(typeName));
        if (processedEntityDescriptions.contains(entityDescription) || entityDescription.getChildEntityDescriptions().stream().anyMatch(processedEntityDescriptions::contains)) {
            throw new GraphQLException("Validation error of type MisplacedType: Type " + typeName.getName() + " not allowed here because it is covered by previous inline fragments " + graphQLDataFetcherHelper.getSourceLocationString(inlineFragment.getSourceLocation()));
        }
        processedEntityDescriptions.add(entityDescription);
        processedEntityDescriptions.addAll(entityDescription.getChildEntityDescriptions());
        ObjectNode mergeRequestNode = Helper.OBJECT_MAPPER.createObjectNode();
        mergeRequestNode.put(EntitiesReadAccessJsonHelper.TYPE_FIELD_NAME, entityDescription.getName());
        List<Directive> directives = inlineFragment.getDirectives(GraphQLSchemaHelper.MERGE_REQUEST_SPECIFICATION_DIRECTIVE_NAME);
        Directive mergeRequestSpecificationDirective = directives.isEmpty() ? null : directives.get(0);

        // Here we present a fragment as a field, with its name equal to the fragment type and arguments from directives.
        // Will come in handy in pathConditions
        Field fragmentAsField = new Field(
                inlineFragment.getTypeCondition().getName(),
                inlineFragment.getDirectives().stream().flatMap(a -> a.getArguments().stream()).collect(Collectors.toList()),
                inlineFragment.getSelectionSet()
        );
        dataFetcherContainer.addStep(fragmentAsField, entityDescription);
        graphQLDataFetcherHelper.processSpecificationArguments(mergeRequestNode, dataFetcherContainer, mergeRequestSpecificationDirective != null
            ? mergeRequestSpecificationDirective.getArguments()
            : Collections.emptyList()
        );
        mergeRequestNode.put(GraphQLSchemaHelper.SPECIAL_FLAG_FIELD_NAME, true);
        graphQLDataFetcherHelper.processProperties(entityDescription, mergeRequestNode, graphQLDataFetcherHelper.getPropertiesNode(mergeRequestNode), dataFetcherContainer, inlineFragment.getSelectionSet());
        dataFetcherContainer.removeStep();
        graphQLDataFetcherHelper.postProcessNode(mergeRequestNode);
        mergeRequestsNode.add(mergeRequestNode);
    }

    /**
     * Process merge requests
     *
     * @param mergeRequestsNode The node for merging requests
     * @param container       Environment
     * @param selectionSet      Selection
     */
    void processMergeRequests(ArrayNode mergeRequestsNode, DataFetcherContainer container, SelectionSet selectionSet) {
        Set<EntityDescription> processedEntityDescriptions = new HashSet<>();
        graphQLDataFetcherHelper.processSelectionSet(container, selectionSet,
                field -> {},
                inlineFragment -> processMergeRequest(processedEntityDescriptions, mergeRequestsNode, container, inlineFragment),
                (fragmentSpread, fragmentDefinition) -> {
                    throw graphQLDataFetcherHelper.getMisplacedFragmentSpreadException(fragmentSpread);
                }
        );
    }

    /**
     * Get request node
     *
     * @param environment Environment
     */
    JsonNode getRequestNode(DataFetchingEnvironment environment, GraphQLSecurityContext variablesContainer) {
        ObjectNode requestNode = Helper.OBJECT_MAPPER.createObjectNode();
        ArrayNode mergeRequestsNode = Helper.OBJECT_MAPPER.createArrayNode();
        Field queryField = environment.getField();
        DataFetcherContainer dataFetcherContainer = new DataFetcherContainer(environment, variablesContainer, null);

        dataFetcherContainer.addStep(queryField, null);
        graphQLDataFetcherHelper.processSpecificationArguments(requestNode, dataFetcherContainer, queryField.getArguments());
        graphQLDataFetcherHelper.processSelectionSet(dataFetcherContainer, queryField.getSelectionSet(),
                field -> {
                    if (GraphQLSchemaHelper.ELEMENTS_FIELD_NAME.equals(field.getName())) {
                        dataFetcherContainer.addStep(field, null);
                        processMergeRequests(mergeRequestsNode, dataFetcherContainer, field.getSelectionSet());
                        dataFetcherContainer.removeStep();
                    } else if (GraphQLSchemaHelper.COUNT_FIELD_NAME.equals(field.getName())) {
                        requestNode.put(EntitiesReadAccessJsonHelper.COUNT_FIELD_NAME, true);
                    }
                },
                inlineFragment -> {
                    throw graphQLDataFetcherHelper.getMisplacedInlineFragmentException(inlineFragment);
                },
                (fragmentSpread, fragmentDefinition) -> {
                    throw graphQLDataFetcherHelper.getMisplacedFragmentSpreadException(fragmentSpread);
                });
        dataFetcherContainer.removeStep();
        requestNode.set(EntitiesReadAccessJsonHelper.REQUESTS_MERGE_FIELD_NAME, mergeRequestsNode);
        return requestNode;
    }

    @Override
    public Object get(DataFetchingEnvironment environment, GraphQLSecurityContext securityContext) {
        List<GraphQLError> errors = new ArrayList<>();
        Object data = graphQLDataFetcherHelper.getEntitiesCollection(errors, Collections.singletonList(environment.getField().getName()), graphQLDataFetcherHelper.getEntitiesReadAccessJson().searchEntities(getRequestNode(environment, securityContext)));
        return newResult()
                .data(data)
                .errors(errors)
                .build();
    }
}
