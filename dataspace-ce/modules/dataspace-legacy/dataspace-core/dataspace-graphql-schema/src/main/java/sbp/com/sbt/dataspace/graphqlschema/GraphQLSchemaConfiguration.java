package sbp.com.sbt.dataspace.graphqlschema;

import com.sbt.dataspace.pdm.PdmModel;
import graphql.schema.GraphQLSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import ru.sbertech.dataspace.security.graphql.SecurityRulesFetcher;
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJson;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescriptionCheck;
import sbp.com.sbt.dataspace.graphqlschema.builder.GraphQLSchemaBaseBuilder;
import sbp.com.sbt.dataspace.graphqlschema.builder.GraphQLSchemaMutationBuilder;
import sbp.com.sbt.dataspace.graphqlschema.builder.GraphQLSchemaQueryBuilder;

import java.util.Collection;

@ComponentScan(basePackageClasses = GraphQLSchemaBaseBuilder.class)
public class GraphQLSchemaConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLSchemaConfiguration.class);

  @Value("${dataspace.security.gql.pre-commit.enabled:false}")
  private boolean securityPreCommitCheckEnabled = false;

    /**
     * Log settings for GraphQL schema
     */
  private void logGraphQLSchemaSettings(GraphQLSchemaSettings graphQLSchemaSettingsOptional) {
    LOGGER.info("{}", graphQLSchemaSettingsOptional);
  }

    /**
     * Bin settings, use prefix "_"
     */
  @Bean
  @ConfigurationProperties(prefix = "dataspace.endpoint.graphql.schema.settings")
  GraphQLSchemaSettings graphQLSchemaSettings() {
    return new GraphQLSchemaSettings();
  }

  @Bean
  @ConfigurationProperties(prefix = "dataspace.graphql.parser")
  GraphQLParserSettings graphQLParserSettings() {
    return new GraphQLParserSettings();
  }

  @Bean(name = "searchGraphQLDataFetcherHelper")
  @ConditionalOnBean(EntitiesReadAccessJson.class)
  public GraphQLDataFetcherHelper searchGraphQLDataFetcherHelper(GraphQLSchemaSettings graphQLSchemaSettings,
                                                                 ModelDescription modelDescription,
                                                                 EntitiesReadAccessJson entitiesReadAccessJson) {
    logGraphQLSchemaSettings(graphQLSchemaSettings);
    return new GraphQLDataFetcherHelper(
      modelDescription,
      entitiesReadAccessJson,
      graphQLSchemaSettings,
      securityPreCommitCheckEnabled);
  }

  @Bean
  public GraphQLSchema graphQLSchema(GraphQLSchemaSettings graphQLSchemaSettings,
                                     ModelDescription modelDescription,
                                     Collection<GraphQLSchemaQueryBuilder> queryBuilders,
                                     Collection<GraphQLSchemaMutationBuilder> mutationBuilders,
                                     PdmModel pdmModel,
                                     GraphQLParserSettings parserSettings,
                                     EntitiesReadAccessJson entitiesReadAccessJson,
                                     GraphQLDataFetcherHelper graphQLDataFetcherHelper,
                                     @Autowired(required = false) SecurityRulesFetcher securityRulesFetcher) {
    return new GraphQLSchemaBaseBuilder(
      modelDescription,
      pdmModel,
      queryBuilders,
      mutationBuilders,
      graphQLSchemaSettings,
      parserSettings,
      entitiesReadAccessJson,
      graphQLDataFetcherHelper,
      securityRulesFetcher
    ).build();
  }

  @Bean
  public ModelDescriptionCheck graphQLSchemaModelDescriptionCheck(GraphQLSchemaSettings graphQLSchemaSettings) {
    logGraphQLSchemaSettings(graphQLSchemaSettings);
    return new GraphQLSchemaModelDescriptionCheck(graphQLSchemaSettings.idWithUnderscore, graphQLSchemaSettings.aggregateVersionWithUnderscore);
  }

}
