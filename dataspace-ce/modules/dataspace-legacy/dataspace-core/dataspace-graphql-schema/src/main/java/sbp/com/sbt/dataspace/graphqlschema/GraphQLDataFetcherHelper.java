package sbp.com.sbt.dataspace.graphqlschema;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sbt.pprb.ac.graph.converter.PrimitiveToStringHelper;
import graphql.GraphQLError;
import graphql.GraphQLException;
import graphql.language.Argument;
import graphql.language.ArrayValue;
import graphql.language.BooleanValue;
import graphql.language.Directive;
import graphql.language.EnumValue;
import graphql.language.Field;
import graphql.language.FragmentDefinition;
import graphql.language.FragmentSpread;
import graphql.language.InlineFragment;
import graphql.language.IntValue;
import graphql.language.ObjectField;
import graphql.language.ObjectValue;
import graphql.language.SelectionSet;
import graphql.language.SourceLocation;
import graphql.language.StringValue;
import graphql.language.TypeName;
import graphql.language.Value;
import graphql.language.VariableReference;
import org.jetbrains.annotations.NotNull;
import ru.sbertech.dataspace.security.utils.GraphQLSecurityContext;
import ru.sbertech.dataspace.security.utils.GraphQLStringReplacer;
import sbp.com.sbt.dataspace.feather.common.Pointer;
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJson;
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJsonHelper;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.GroupDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ParamDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ReferenceDescription;
import ru.sbertech.dataspace.security.model.dto.PathCondition;
import sbp.com.sbt.dataspace.utils.MsgUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper.CALC_FIELD_NAME;
import static sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper.ELEMENTS_FIELD_NAME;
import static sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper.GET_PREFIX;
import static sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper.MERGE_FIELD_NAME;
import static sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper.PACKET_FIELD_NAME;
import static sbp.com.sbt.dataspace.graphqlschema.Helper.injectVariablesIntoStringExpression;

/**
 * Assistant for loading data GraphQL
 */
public final class GraphQLDataFetcherHelper {

  static final String ROOT_DICTIONARY_CLASS_NAME = "RootDictionary";

  ModelDescription modelDescription;
  EntitiesReadAccessJson entitiesReadAccessJson;

  private final GraphQLSchemaSettings graphQLSchemaSettings;
  public final boolean securityPreCommitCheckEnabled;

    /**
     * @param modelDescription              Description of the model
     * @param entitiesReadAccessJson        Access to entities for reading through JSON
     * @param graphQLSchemaSettings         Settings for using the underscore
     * @param securityPreCommitCheckEnabled Is security check before commit enabled?
     */
  public GraphQLDataFetcherHelper(ModelDescription modelDescription, EntitiesReadAccessJson entitiesReadAccessJson, GraphQLSchemaSettings graphQLSchemaSettings, boolean securityPreCommitCheckEnabled) {
    this.modelDescription = modelDescription;
    this.entitiesReadAccessJson = entitiesReadAccessJson;
    this.graphQLSchemaSettings = graphQLSchemaSettings;
    this.securityPreCommitCheckEnabled = securityPreCommitCheckEnabled;

    Helper.BYTE_ARRAY_SCALAR_TYPE_COERCING.setUseFasterXmlBase64Decoder(graphQLSchemaSettings.isUseFasterXmlBase64Decoder());
    Helper.DATE_SCALAR_TYPE_COERCING.setSuppressCoercingErrors(graphQLSchemaSettings.isSuppressCalcDateTimeCoercingErrors());
    Helper.TIME_SCALAR_TYPE_COERCING.setSuppressCoercingErrors(graphQLSchemaSettings.isSuppressCalcDateTimeCoercingErrors());
    Helper.DATE_TIME_SCALAR_TYPE_COERCING.setSuppressCoercingErrors(graphQLSchemaSettings.isSuppressCalcDateTimeCoercingErrors());
    Helper.OFFSET_DATE_TIME_SCALAR_TYPE_COERCING.setSuppressCoercingErrors(graphQLSchemaSettings.isSuppressCalcDateTimeCoercingErrors());
  }

    /**
     * Get model description
     */
  public ModelDescription getModelDescription() {
    return modelDescription;
  }

    /**
     * Access to read entities through JSON
     */
  public EntitiesReadAccessJson getEntitiesReadAccessJson() {
    return entitiesReadAccessJson;
  }

    /**
     * Get the field name with id
     */
  public String getIdFieldName() {
    return graphQLSchemaSettings.getIdFieldName();
  }

    /**
     * Get the name of the field with the version of the unit
     */
  public String getAggregateVersionFieldName() {
    return graphQLSchemaSettings.getAggregateVersionFieldName();
  }

  public boolean isUseJsonRpcBindMode() {
    return graphQLSchemaSettings.isUseJsonRpcBindMode();
  }

    /**
     * Name of the root aggregate class of reference books
     */
  public static String getRootDictionaryClassName() {
    return ROOT_DICTIONARY_CLASS_NAME;
  }

    /**
     * If value is a variable, then it returns the value of the GQL variable; otherwise, it returns the value itself.
     *
     * @param dataFetcherContainer is used to retrieve GQL query variables
     * @param value                either a GQL variable or a value
     */
  public String getString(DataFetcherContainer dataFetcherContainer, Value<?> value) {
    if (value instanceof VariableReference) {
      return (String) dataFetcherContainer.getParsedVariables().get(((VariableReference) value).getName());
    } else if (value instanceof StringValue) {
      return ((StringValue) value).getValue();
    }
    return null;
  }

    /**
     * If value is a variable, then it returns the value of the GQL variable; otherwise, it returns the value itself.
     *
     * @param value The value
     */
  public String getIdAsString(DataFetcherContainer dataFetcherContainer, Value<?> value) {
    if (value instanceof VariableReference) {
      Object res = dataFetcherContainer.getParsedVariables().get(((VariableReference) value).getName());
      if (res instanceof String) {
        return (String) res;
      }
      if (res instanceof Integer) {
        return ((Integer) res).toString();
      }
    } else if (value instanceof StringValue) {
      return ((StringValue) value).getValue();
    } else if (value instanceof IntValue) {
      return ((IntValue) value).getValue().toString();
    }

      throw new GraphQLException("Unable to convert the identifier " + value + " to a string");
  }

    /**
     * If value is a variable, then it returns the value of the GQL variable; otherwise, it returns the value itself (a 4-byte integer).
     *
     * @param dataFetcherContainer is used to retrieve GQL query variables
     * @param value                either a GQL variable or a value
     */
  public Integer getInteger(DataFetcherContainer dataFetcherContainer, Value<?> value) {
    if (value instanceof VariableReference) {
      return (Integer) dataFetcherContainer.getParsedVariables().get(((VariableReference) value).getName());
    } else if (value instanceof IntValue) {
        return ((IntValue) value).getValue().intValue();
    }
    return null;
  }

    /**
     * Get offset date time
     *
     * @param value The value
     */
  public OffsetDateTime getOffsetDateTime(DataFetcherContainer dataFetcherContainer, Value<?> value) {
    if (value instanceof VariableReference) {
      return (OffsetDateTime) dataFetcherContainer.getParsedVariables().get(((VariableReference) value).getName());
    } else {
      return Helper.OFFSET_DATE_TIME_SCALAR_TYPE_COERCING.parseLiteral(value);
    }
  }

    /**
     * Get a logical value
     *
     * @param value The value
     */
  public Boolean getBoolean(DataFetcherContainer dataFetcherContainer, Value<?> value) {
    if (value instanceof VariableReference) {
      return (Boolean) dataFetcherContainer.getParsedVariables().get(((VariableReference) value).getName());
    } else if (value instanceof BooleanValue) {
      return ((BooleanValue) value).isValue();
    }
    return null;
  }

    /**
     * Get enumeration
     *
     * @param value The value
     */
  public String getEnum(DataFetcherContainer dataFetcherContainer, Value<?> value) {
    if (value instanceof VariableReference) {
      return (String) dataFetcherContainer.getParsedVariables().get(((VariableReference) value).getName());
    } else {
      return ((EnumValue) value).getName();
    }
  }

    /**
     * Is the object enabled
     *
     * @param getDirectiveFunction The function for obtaining a directive
     */
  public boolean isIncluded(DataFetcherContainer dataFetcherContainer, Function<String, List<Directive>> getDirectiveFunction) {
    List<Directive> skipDirectives = getDirectiveFunction.apply(Helper.SKIP_DIRECTIVE_NAME);
    List<Directive> includeDirectives = getDirectiveFunction.apply(Helper.INCLUDE_DIRECTIVE_NAME);
    Directive skipDirective = skipDirectives.isEmpty() ? null : skipDirectives.get(0);
    Directive includeDirective = includeDirectives.isEmpty() ? null : includeDirectives.get(0);
    return (skipDirective == null || !getBoolean(dataFetcherContainer, skipDirective.getArgument(Helper.IF_ARGUMENT_NAME).getValue()))
      && (includeDirective == null || getBoolean(dataFetcherContainer, includeDirective.getArgument(Helper.IF_ARGUMENT_NAME).getValue()));
  }

    /**
     * Get string for placement in the source code
     *
     * @param sourceLocation Location in the source code
     */
  public String getSourceLocationString(SourceLocation sourceLocation) {
    return "(" + sourceLocation.getLine() + ":" + sourceLocation.getColumn() + ")";
  }

    /**
     * Get an exception for the built-in fragment in the wrong place
     *
     * @param inlineFragment Inline fragment
     */
  public GraphQLException getMisplacedInlineFragmentException(InlineFragment inlineFragment) {
    return new GraphQLException("Validation error of type MisplacedInlineFragment: Inline fragment not allowed here " + getSourceLocationString(inlineFragment.getSourceLocation()));
  }

    /**
     * Get an exception for the fragment in the wrong place
     *
     * @param fragmentSpread The fragment
     */
  public GraphQLException getMisplacedFragmentSpreadException(FragmentSpread fragmentSpread) {
    return new GraphQLException("Validation error of type MisplacedFragmentSpread: Fragment spread not allowed here " + getSourceLocationString(fragmentSpread.getSourceLocation()));
  }

    /**
     * Check the merge request specification directive
     *
     * @param getDirectiveFunction The function for obtaining a directive
     */
  public void checkMergeRequestSpecificationDirective(Function<String, List<Directive>> getDirectiveFunction) {
    List<Directive> directives = getDirectiveFunction.apply(GraphQLSchemaHelper.MERGE_REQUEST_SPECIFICATION_DIRECTIVE_NAME);
    Directive directive = directives.isEmpty() ? null : directives.get(0);
    if (directive != null) {
      throw new GraphQLException("Validation error of type MisplacedDirective: Directive " + GraphQLSchemaHelper.MERGE_REQUEST_SPECIFICATION_DIRECTIVE_NAME + " not allowed here " + getSourceLocationString(directive.getSourceLocation()));
    }
  }

    /**
     * Check the entity's interface
     *
     * @param typeName Type name
     */
  public void checkEntityInterface(TypeName typeName) {
    if (GraphQLSchemaHelper.ENTITY_INTERFACE_TYPE_NAME.equals(typeName.getName())) {
      throw new GraphQLException("Validation error of type MisplacedInterface: Interface type " + GraphQLSchemaHelper.ENTITY_INTERFACE_TYPE_NAME + " not allowed here " + getSourceLocationString(typeName.getSourceLocation()));
    }
  }

    /**
     * Process selection
     *
     * @param selectionSet            Selection
     * @param fieldProcessor          Field handler
     * @param inlineFragmentProcessor The built-in fragment handler
     * @param fragmentProcessor       The fragment processor handler
     */
  public void processSelectionSet(DataFetcherContainer dataFetcherContainer, SelectionSet selectionSet, Consumer<Field> fieldProcessor, Consumer<InlineFragment> inlineFragmentProcessor, BiConsumer<FragmentSpread, FragmentDefinition> fragmentProcessor) {
    selectionSet.getSelectionsOfType(Field.class).stream()
      .filter(field -> isIncluded(dataFetcherContainer, field::getDirectives))
      .forEach(fieldProcessor);
    selectionSet.getSelectionsOfType(InlineFragment.class).stream()
      .filter(inlineFragment -> isIncluded(dataFetcherContainer, inlineFragment::getDirectives))
      .forEach(inlineFragmentProcessor);
    selectionSet.getSelectionsOfType(FragmentSpread.class).stream()
      .filter(fragmentSpread -> isIncluded(dataFetcherContainer, fragmentSpread::getDirectives))
      .forEach(fragmentSpread -> fragmentProcessor.accept(fragmentSpread, dataFetcherContainer.getFragments().get(fragmentSpread.getName())));
  }

    /**
     * Get sorting criterion specification
     *
     * @param dataFetcherContainer                  is used to access GQL variables
     * @param sortCriterionSpecificationObjectValue Object value of sorting criteria specification
     */
  private Map<String, Object> getSortCriterionSpecification(DataFetcherContainer dataFetcherContainer, ObjectValue sortCriterionSpecificationObjectValue) {
    List<ObjectField> objectFields = sortCriterionSpecificationObjectValue.getObjectFields();
    Map<String, Object> result = new LinkedHashMap<>(objectFields.size());
    objectFields.forEach(field2 -> {
      if (GraphQLSchemaHelper.CRITERION_INPUT_OBJECT_FIELD_NAME.equals(field2.getName())) {
        result.put(GraphQLSchemaHelper.CRITERION_INPUT_OBJECT_FIELD_NAME, getString(dataFetcherContainer, field2.getValue()));
      } else if (GraphQLSchemaHelper.ORDER_INPUT_OBJECT_FIELD_NAME.equals(field2.getName())) {
        result.put(GraphQLSchemaHelper.ORDER_INPUT_OBJECT_FIELD_NAME, getEnum(dataFetcherContainer, field2.getValue()));
      } else {
        result.put(GraphQLSchemaHelper.NULLS_LAST_OBJECT_FIELD_NAME, getBoolean(dataFetcherContainer, field2.getValue()));
      }
    });
    return result;
  }

  private Map<String, Object> getErrorStrategySpecification(DataFetcherContainer dataFetcherContainer, ObjectValue errorStrategyObjectValue) {
    List<ObjectField> objectFields = errorStrategyObjectValue.getObjectFields();
    Map<String, Object> result = new LinkedHashMap<>(objectFields.size());
    objectFields.forEach(field2 -> {
      if (GraphQLSchemaHelper.UNAVAILABLE_OBJECT_FIELD_NAME.equals(field2.getName())) {
        result.put(GraphQLSchemaHelper.UNAVAILABLE_OBJECT_FIELD_NAME, getEnum(dataFetcherContainer, field2.getValue()));
      } else if (GraphQLSchemaHelper.INCOMPATIBLE_OBJECT_FIELD_NAME.equals(field2.getName())) {
        result.put(GraphQLSchemaHelper.INCOMPATIBLE_OBJECT_FIELD_NAME, getEnum(dataFetcherContainer, field2.getValue()));
      } else if (GraphQLSchemaHelper.RETRY_COUNT_OBJECT_FIELD_NAME.equals(field2.getName())) {
        result.put(GraphQLSchemaHelper.RETRY_COUNT_OBJECT_FIELD_NAME, getInteger(dataFetcherContainer, field2.getValue()));
      } else {
        result.put(GraphQLSchemaHelper.RETRY_INTERVAL_MS_OBJECT_FIELD_NAME, getInteger(dataFetcherContainer, field2.getValue()));
      }
    });
    return result;
  }


    /**
     * Process command line arguments
     *
     * @param featherRequest - The generated request for Feather
     * @param arguments      Arguments
     */
  public void processSpecificationArguments(ObjectNode featherRequest, DataFetcherContainer dataFetcherContainer, List<Argument> arguments) {
    arguments.forEach(argument -> {
      if (GraphQLSchemaHelper.ID_ARGUMENT_NAME.equals(argument.getName())) {
        String entityIdArgument = getString(dataFetcherContainer, argument.getValue());
        String cond = "it.$id=='" + entityIdArgument.replace("'", "''") + "'";
        featherRequest.put(EntitiesReadAccessJsonHelper.CONDITION_FIELD_NAME, cond);
      }
      if (GraphQLSchemaHelper.CONDITION_ARGUMENT_NAME.equals(argument.getName())) {
        String value = getString(dataFetcherContainer, argument.getValue());
        if (value != null) {
          featherRequest.put(
            EntitiesReadAccessJsonHelper.CONDITION_FIELD_NAME,
            injectVariablesIntoStringExpression(dataFetcherContainer.getDataFetchingEnvironment(), value)
          );
        }
      } else if (GraphQLSchemaHelper.GROUP_ARGUMENT_NAME.equals(argument.getName())) {
        List<String> value = null;
        if (argument.getValue() instanceof VariableReference) {
          value = (List<String>) dataFetcherContainer.getParsedVariables().get(((VariableReference) argument.getValue()).getName());
        } else if (argument.getValue() instanceof ArrayValue) {
          List<Value> values = ((ArrayValue) argument.getValue()).getValues();
          List<String> value2 = new ArrayList<>(values.size());
          values.forEach(element -> {
            if (element instanceof VariableReference) {
              value2.add((String) dataFetcherContainer.getParsedVariables().get(((VariableReference) element).getName()));
            } else {
              value2.add(((StringValue) element).getValue());
            }
          });
          value = value2;
        }
        if (value != null) {
          ArrayNode groupNode = Helper.OBJECT_MAPPER.createArrayNode();
          value.stream().map(expr -> injectVariablesIntoStringExpression(dataFetcherContainer.getDataFetchingEnvironment(), expr)).forEach(groupNode::add);

          featherRequest.set(EntitiesReadAccessJsonHelper.GROUP_FIELD_NAME, groupNode);
        }
      } else if (GraphQLSchemaHelper.GROUP_COND_ARGUMENT_NAME.equals(argument.getName())) {
        String value = getString(dataFetcherContainer, argument.getValue());
        if (value != null) {
          featherRequest.put(EntitiesReadAccessJsonHelper.GROUP_CONDITION_FIELD_NAME, injectVariablesIntoStringExpression(dataFetcherContainer.getDataFetchingEnvironment(), value));
        }
      } else if (GraphQLSchemaHelper.LIMIT_ARGUMENT_NAME.equals(argument.getName())) {
        Integer value = getInteger(dataFetcherContainer, argument.getValue());
        if (value != null) {
          featherRequest.put(EntitiesReadAccessJsonHelper.LIMIT_FIELD_NAME, value);
        }
      } else if (GraphQLSchemaHelper.OFFSET_ARGUMENT_NAME.equals(argument.getName())) {
        Integer value = getInteger(dataFetcherContainer, argument.getValue());
        if (value != null) {
          featherRequest.put(EntitiesReadAccessJsonHelper.OFFSET_FIELD_NAME, value);
        }
      } else if (GraphQLSchemaHelper.SORT_ARGUMENT_NAME.equals(argument.getName())) {
        List<Map<String, Object>> value = null;
        if (argument.getValue() instanceof VariableReference) {
          value = (List<Map<String, Object>>) dataFetcherContainer.getParsedVariables().get(((VariableReference) argument.getValue()).getName());
        } else if (argument.getValue() instanceof ArrayValue) {
          List<Value> values = ((ArrayValue) argument.getValue()).getValues();
          List<Map<String, Object>> value2 = new ArrayList<>(values.size());
          values.forEach(element -> {
            if (element instanceof VariableReference) {
              value2.add((Map<String, Object>) dataFetcherContainer.getParsedVariables().get(((VariableReference) element).getName()));
            } else {
              value2.add(getSortCriterionSpecification(dataFetcherContainer, (ObjectValue) element));
            }
          });
          value = value2;
        } else if (argument.getValue() instanceof ObjectValue) {
          value = Collections.singletonList(getSortCriterionSpecification(dataFetcherContainer, (ObjectValue) argument.getValue()));
        }
        if (value != null) {
          ArrayNode sortNode = Helper.OBJECT_MAPPER.createArrayNode();
          value.forEach(sortCriterionSpecification -> {
            ObjectNode sortCriterionSpecificationNode = Helper.OBJECT_MAPPER.createObjectNode();
            sortCriterionSpecification.forEach((key, value2) -> {
              if (value2 != null) {
                if (GraphQLSchemaHelper.CRITERION_INPUT_OBJECT_FIELD_NAME.equals(key)) {
                  sortCriterionSpecificationNode.put(
                    EntitiesReadAccessJsonHelper.CRITERION_FIELD_NAME,
                    injectVariablesIntoStringExpression(dataFetcherContainer.getDataFetchingEnvironment(), (String) value2)
                  );
                } else if (GraphQLSchemaHelper.ORDER_INPUT_OBJECT_FIELD_NAME.equals(key)) {
                  sortCriterionSpecificationNode.put(EntitiesReadAccessJsonHelper.ORDER_FIELD_NAME, Helper.SORT_ORDER_MAPPING.get(value2));
                } else {
                  sortCriterionSpecificationNode.put(EntitiesReadAccessJsonHelper.NULLS_LAST_FIELD_NAME, (Boolean) value2);
                }
              }
            });
            sortNode.add(sortCriterionSpecificationNode);
          });
          featherRequest.set(EntitiesReadAccessJsonHelper.SORT_FIELD_NAME, sortNode);
        }
      } else if (GraphQLSchemaHelper.ALIAS_ARGUMENT_NAME.equals(argument.getName())) {
        String value = getString(dataFetcherContainer, argument.getValue());
        if (value != null) {
          featherRequest.put(EntitiesReadAccessJsonHelper.ALIAS_FIELD_NAME, value);
        }
      } else if (GraphQLSchemaHelper.EXPRESSION_ARGUMENT_NAME.equals(argument.getName())) {
        String value = getString(dataFetcherContainer, argument.getValue());
        if (value != null) {
          featherRequest.put(EntitiesReadAccessJsonHelper.CALCULATED_EXPRESSION_FIELD_NAME, injectVariablesIntoStringExpression(dataFetcherContainer.getDataFetchingEnvironment(), value));
        }
      } else if (GraphQLSchemaHelper.EXPR_ARGUMENT_NAME.equals(argument.getName())) {
        String value = getString(dataFetcherContainer, argument.getValue());
        if (value != null) {
          featherRequest.put(EntitiesReadAccessJsonHelper.CALCULATED_EXPRESSION_FIELD_NAME, injectVariablesIntoStringExpression(dataFetcherContainer.getDataFetchingEnvironment(), value));
        }
      } else if (GraphQLSchemaHelper.DISTINCT_ARGUMENT_NAME.equals(argument.getName())) {
        Boolean value = getBoolean(dataFetcherContainer, argument.getValue());
        if (value != null) {
          featherRequest.put(EntitiesReadAccessJsonHelper.DISTINCT_FIELD_NAME, value);
        }
      } else if (GraphQLSchemaHelper.ELEMENT_ALIAS_ARGUMENT_NAME.equals(argument.getName())) {
        String value = getString(dataFetcherContainer, argument.getValue());
        if (value != null) {
          featherRequest.put(EntitiesReadAccessJsonHelper.ELEMENT_ALIAS_FIELD_NAME, value);
        }
      } else if (GraphQLSchemaHelper.MULTISEARCH_CONTEXT_NAME.equals(argument.getName())) {
        String value = getString(dataFetcherContainer, argument.getValue());
        if (value != null) {
          // Pass the GraphQL query parameter to the JSON attribute "context"
          featherRequest.put("context", value);
        }
      } else if (GraphQLSchemaHelper.ERROR_STRATEGY_NAME.equals(argument.getName())) {
        Map<String, Object> value = null;
        if (argument.getValue() instanceof VariableReference) {
          value = (Map<String, Object>) dataFetcherContainer.getParsedVariables().get(((VariableReference) argument.getValue()).getName());
        } else if (argument.getValue() instanceof ObjectValue) {
          value = getErrorStrategySpecification(dataFetcherContainer, (ObjectValue) argument.getValue());
        }
        if (value != null) {
          ObjectNode errorStrategyNode = Helper.OBJECT_MAPPER.createObjectNode();
          value.forEach((key, value2) -> {
            if (GraphQLSchemaHelper.UNAVAILABLE_OBJECT_FIELD_NAME.equals(key)) {
              errorStrategyNode.put(GraphQLSchemaHelper.UNAVAILABLE_OBJECT_FIELD_NAME, ((String) value2).toLowerCase());
            } else if (GraphQLSchemaHelper.INCOMPATIBLE_OBJECT_FIELD_NAME.equals(key)) {
              errorStrategyNode.put(GraphQLSchemaHelper.INCOMPATIBLE_OBJECT_FIELD_NAME, ((String) value2).toLowerCase());
            } else if (GraphQLSchemaHelper.RETRY_COUNT_OBJECT_FIELD_NAME.equals(key)) {
              errorStrategyNode.put(GraphQLSchemaHelper.RETRY_COUNT_OBJECT_FIELD_NAME, (int) value2);
            } else {
              errorStrategyNode.put(GraphQLSchemaHelper.RETRY_INTERVAL_MS_OBJECT_FIELD_NAME, (int) value2);
            }
          });
          featherRequest.set(GraphQLSchemaHelper.ERROR_STRATEGY_NAME, errorStrategyNode);
        }
      } else if (GraphQLSchemaHelper.PARAMS_ARGUMENT_NAME.equals(argument.getName())) {
        // In theory, it should work...
        String type = featherRequest.get("type").asText();
        EntityDescription entityDescription = modelDescription.getEntityDescription(type);

        List<ObjectField> objectFields = ((ObjectValue) argument.getValue()).getObjectFields();
        if (objectFields != null && !objectFields.isEmpty()) {
          ObjectNode paramsNode = Helper.OBJECT_MAPPER.createObjectNode();
          for (ObjectField objectField : objectFields) {
            String paramName = objectField.getName();
            ParamDescription paramDescription = entityDescription.getParamDescriptions().get(paramName);
            Value<?> value = objectField.getValue();
            if (value instanceof ArrayValue) {
              ArrayNode arrayNode = Helper.OBJECT_MAPPER.createArrayNode();
              for (Value<?> innerValue : ((ArrayValue) value).getValues()) {
                Object object = Helper.TYPE_MAPPING.get(paramDescription.getType()).getCoercing().parseLiteral(innerValue);
                JsonNode valueToInsert = PrimitiveToStringHelper.getValue(object);
                arrayNode.add(valueToInsert);
              }
              paramsNode.set(paramName, arrayNode);
            } else {
              Object object = Helper.TYPE_MAPPING.get(paramDescription.getType()).getCoercing().parseLiteral(value);
              JsonNode valueToInsert = PrimitiveToStringHelper.getValue(object);
              paramsNode.set(paramName, valueToInsert);
            }

          }
          if (!paramsNode.isEmpty() && !paramsNode.isNull()) {
            featherRequest.set(EntitiesReadAccessJsonHelper.PARAMS_FIELD_NAME, paramsNode);
          }
        }
      }
    });
    // Determine whether there is an additional condition for the current field path or not
    getPathConditionString(dataFetcherContainer)
      .ifPresent(cond -> {
JsonNode existingSecCond = featherRequest.get(EntitiesReadAccessJsonHelper.SECURITY_CONDITION_FIELD_NAME); // Тут может быть paramAddition
        String adjustedCond = existingSecCond == null
          ? prepareConditionReplacements(dataFetcherContainer, cond)
          : '(' + existingSecCond.asText() + ") && (" + prepareConditionReplacements(dataFetcherContainer, cond) + ')';

        featherRequest.put(
          EntitiesReadAccessJsonHelper.SECURITY_CONDITION_FIELD_NAME,
          adjustedCond
        );
      });
  }

    /**
     * Handles additional security condition by performing variable substitution
     */
  private String prepareConditionReplacements(DataFetcherContainer dataFetcherContainer, String additionalCondition) {
    GraphQLSecurityContext securityContext = dataFetcherContainer.getSecurityContext();

    List<String> replacementResult = GraphQLStringReplacer.Companion.replaceAndReturn(
      additionalCondition,
      securityContext == null ? Collections.emptyMap() : securityContext.getAllVariables()
    );

    if (replacementResult.size() > 1) {
        throw new GraphQLException("Additional security condition '" + additionalCondition + "' contains an indication to an array");
    }

    return replacementResult.get(0);
  }

    /**
     * Get entity type
     *
     * @param typeName Type name
     */
  public String getEntityType(TypeName typeName) {
    String result = typeName.getName();
    if (result.startsWith(GraphQLSchemaHelper.ENTITY_OBJECT_TYPE_PREFIX)) {
      result = result.substring(GraphQLSchemaHelper.ENTITY_OBJECT_TYPE_PREFIX.length());
    }
    return result;
  }

    /**
     * Get object node
     *
     * @param node      Node
     * @param fieldName Field name
     */
  public ObjectNode getObjectNode(ObjectNode node, String fieldName) {
    ObjectNode result = (ObjectNode) node.get(fieldName);
    if (result == null) {
      result = Helper.OBJECT_MAPPER.createObjectNode();
      node.set(fieldName, result);
    }
    return result;
  }

    /**
     * Get array node
     *
     * @param node      Node
     * @param fieldName Field name
     */
  public ArrayNode getArrayNode(ObjectNode node, String fieldName) {
    ArrayNode result = (ArrayNode) node.get(fieldName);
    if (result == null) {
      result = Helper.OBJECT_MAPPER.createArrayNode();
      node.set(fieldName, result);
    }
    return result;
  }

    /**
     * Get property node
     *
     * @param node      Node
     * @param fieldName The name of the field
     */
  public ArrayNode getPropertiesNode(ObjectNode node, String fieldName) {
    ArrayNode result = getArrayNode(node, fieldName);
    if (result.isEmpty()) {
      result.add(Helper.OBJECT_MAPPER.createObjectNode());
    }
    return result;
  }

    /**
     * Get property node
     *
     * @param node Node
     */
  public ArrayNode getPropertiesNode(ObjectNode node) {
    return getPropertiesNode(node, EntitiesReadAccessJsonHelper.PROPERTIES_FIELD_NAME);
  }

    /**
     * Process a collection of primitives
     *
     * @param node         Node
     * @param selectionSet Выборка
     */
  public void processPrimitivesCollection(ObjectNode node, DataFetcherContainer dataFetcherContainer, SelectionSet selectionSet) {
    processSelectionSet(dataFetcherContainer, selectionSet,
      field -> {
        if (GraphQLSchemaHelper.ELEMENTS_FIELD_NAME.equals(field.getName())) {
          node.put(GraphQLSchemaHelper.SPECIAL_FLAG_FIELD_NAME, true);
        } else if (GraphQLSchemaHelper.COUNT_FIELD_NAME.equals(field.getName())) {
          node.put(EntitiesReadAccessJsonHelper.COUNT_FIELD_NAME, true);
        }
      },
      inlineFragment -> {
        checkMergeRequestSpecificationDirective(inlineFragment::getDirectives);
        processPrimitivesCollection(node, dataFetcherContainer, inlineFragment.getSelectionSet());
      },
      (fragmentSpread, fragmentDefinition) -> processPrimitivesCollection(node, dataFetcherContainer, fragmentDefinition.getSelectionSet()));
  }

    /**
     * Process group
     *
     * @param groupDescription Description of the group
     * @param node             Node
     * @param selectionSet     Selection
     */
  public void processGroup(GroupDescription groupDescription, ArrayNode node, DataFetcherContainer dataFetcherContainer, SelectionSet selectionSet) {
    ObjectNode propertiesNode = (ObjectNode) node.get(0);
    processSelectionSet(dataFetcherContainer, selectionSet,
      field -> {
        if (groupDescription.getPrimitiveDescriptions().containsKey(field.getName())) {
          node.add(field.getName());
        } else if (groupDescription.getReferenceDescriptions().containsKey(field.getName())) {
          ObjectNode propertyNode = getObjectNode(propertiesNode, field.getName());
          dataFetcherContainer.addStep(field, null);
          processSpecificationArguments(propertyNode, dataFetcherContainer, field.getArguments());
          propertyNode.put(GraphQLSchemaHelper.SPECIAL_FLAG_FIELD_NAME, true);
          processProperties(groupDescription.getReferenceDescription(field.getName()).getEntityDescription(), propertyNode, getPropertiesNode(propertyNode), dataFetcherContainer, field.getSelectionSet());
          dataFetcherContainer.removeStep();
        }
      },
      inlineFragment -> {
        checkMergeRequestSpecificationDirective(inlineFragment::getDirectives);
        processGroup(groupDescription, node, dataFetcherContainer, inlineFragment.getSelectionSet());
      },
      (fragmentSpread, fragmentDefinition) -> processGroup(groupDescription, node, dataFetcherContainer, fragmentDefinition.getSelectionSet()));
  }

    /**
     * Process computation
     *
     * @param propertiesNode Property node
     * @param selectionSet   Selection
     */
  public void processCalc(String prefix, ObjectNode propertiesNode, DataFetcherContainer dataFetcherContainer, SelectionSet selectionSet) {
    processCalc(prefix, propertiesNode, dataFetcherContainer, selectionSet, false);
  }

    /**
     * Process computation
     *
     * @param propertiesNode Property node
     * @param selectionSet   Selection
     */
  public void processCalc(String prefix, ObjectNode propertiesNode, DataFetcherContainer dataFetcherContainer, SelectionSet selectionSet, boolean calcAsValue) {
    processSelectionSet(dataFetcherContainer, selectionSet,
      field -> {
        if (calcAsValue) {
          String value = getString(dataFetcherContainer, field.getArguments().get(0).getValue());
          propertiesNode.put(prefix + (field.getAlias() != null ? field.getAlias() : field.getName()), injectVariablesIntoStringExpression(dataFetcherContainer.getDataFetchingEnvironment(), value));
        } else {
          ObjectNode propertyNode = getObjectNode(propertiesNode, prefix + (field.getAlias() != null ? field.getAlias() : field.getName()));
          processSpecificationArguments(propertyNode, dataFetcherContainer, field.getArguments());
        }
      },
      inlineFragment -> {
        checkMergeRequestSpecificationDirective(inlineFragment::getDirectives);
        processCalc(prefix, propertiesNode, dataFetcherContainer, inlineFragment.getSelectionSet());
      },
      (fragmentSpread, fragmentDefinition) -> processCalc(prefix, propertiesNode, dataFetcherContainer, fragmentDefinition.getSelectionSet())
    );
  }

    /**
     * Process details
     *
     * @param node         Node
     * @param typeName     Type name
     * @param selectionSet Выборка
     */
  public void processDetails(ObjectNode node, DataFetcherContainer dataFetcherContainer, TypeName typeName, SelectionSet selectionSet) {
    String entityType = getEntityType(typeName);
    processProperties(modelDescription.getEntityDescription(entityType), node, getPropertiesNode(getObjectNode(node, EntitiesReadAccessJsonHelper.DETAILS_FIELD_NAME), entityType), dataFetcherContainer, selectionSet);
  }

    /**
     * Process properties
     *
     * @param entityDescription Entity description
     * @param node              Node
     * @param propertiesNode    Properties node
     * @param selectionSet      Selection
     */
  public void processProperties(EntityDescription entityDescription,
                                ObjectNode node,
                                ArrayNode propertiesNode,
                                DataFetcherContainer dataFetcherContainer,
                                SelectionSet selectionSet) {
    ObjectNode propertiesNode2 = (ObjectNode) propertiesNode.get(0);
    processSelectionSet(dataFetcherContainer, selectionSet,
      field -> {
        if (getAggregateVersionFieldName().equals(field.getName())) {
          DataFetcherStep step = dataFetcherContainer.getDataFetcherStep();
          if (step != null) {
            // For merge, packet, elems requests, the depth differs by +1, so they are excluded
            int nodesToIgnoreCount = step.countNodes(Set.of(MERGE_FIELD_NAME, PACKET_FIELD_NAME, ELEMENTS_FIELD_NAME));
            if (dataFetcherContainer.getLevel() - nodesToIgnoreCount > 1 && !(step.getParent() == null || step.getParent().getFieldDescription() instanceof GroupDescription)) {
              throw new GraphQLException(MsgUtils.WRONG_AGGVERSION_REQUEST_ERROR_MESSAGE);
            }
          }
          node.put(EntitiesReadAccessJsonHelper.AGGREGATE_VERSION_FIELD_NAME, true);
        } else if (entityDescription.getPrimitiveDescriptions().containsKey(field.getName())) {
          if (field.getAlias() == null) {
            propertiesNode.add(field.getName());
          } else {
            processNodeWithAlias(propertiesNode2, field);
          }
        } else if (entityDescription.getPrimitivesCollectionDescriptions().containsKey(field.getName())) {
          ObjectNode propertyNode = getObjectNode(propertiesNode2, field.getName());
          processSpecificationArguments(propertyNode, dataFetcherContainer, field.getArguments());
          processPrimitivesCollection(propertyNode, dataFetcherContainer, field.getSelectionSet());
          processNodeWithAlias(propertiesNode2, field);
        } else if (entityDescription.getReferenceDescriptions().containsKey(field.getName())) {
          ReferenceDescription fieldDescription = entityDescription.getReferenceDescription(field.getName());
          dataFetcherContainer.addStep(field, fieldDescription);
          ObjectNode propertyNode = getObjectNode(propertiesNode2, field.getName());
          processSpecificationArguments(propertyNode, dataFetcherContainer, field.getArguments());
          propertyNode.put(GraphQLSchemaHelper.SPECIAL_FLAG_FIELD_NAME, true);
          processProperties(fieldDescription.getEntityDescription(), propertyNode, getPropertiesNode(propertyNode), dataFetcherContainer, field.getSelectionSet());
          dataFetcherContainer.removeStep();
          processNodeWithAlias(propertiesNode2, field);
        } else if (entityDescription.getReferenceBackReferenceDescriptions().containsKey(field.getName())) {
          ReferenceDescription fieldDescription = entityDescription.getReferenceBackReferenceDescription(field.getName());
          dataFetcherContainer.addStep(field, fieldDescription);
          ObjectNode propertyNode = getObjectNode(propertiesNode2, field.getName());
          processSpecificationArguments(propertyNode, dataFetcherContainer, field.getArguments());
          propertyNode.put(GraphQLSchemaHelper.SPECIAL_FLAG_FIELD_NAME, true);
          processProperties(fieldDescription.getOwnerEntityDescription(), propertyNode, getPropertiesNode(propertyNode), dataFetcherContainer, field.getSelectionSet());
          dataFetcherContainer.removeStep();
          processNodeWithAlias(propertiesNode2, field);
        } else if (entityDescription.getReferencesCollectionDescriptions().containsKey(field.getName())) {
          EntityDescription fieldDescription = entityDescription.getReferencesCollectionDescription(field.getName()).getEntityDescription();
          dataFetcherContainer.addStep(field, fieldDescription);
          ObjectNode propertyNode = getObjectNode(propertiesNode2, field.getName());
          processSpecificationArguments(propertyNode, dataFetcherContainer, field.getArguments());
          processEntitiesCollection(fieldDescription, propertyNode, dataFetcherContainer, field.getSelectionSet());
          dataFetcherContainer.removeStep();
          processNodeWithAlias(propertiesNode2, field);
        } else if (entityDescription.getReferencesCollectionBackReferenceDescriptions().containsKey(field.getName())) {
          EntityDescription fieldDescription = entityDescription.getReferencesCollectionBackReferenceDescription(field.getName()).getOwnerEntityDescription();
          dataFetcherContainer.addStep(field, fieldDescription);
          ObjectNode propertyNode = getObjectNode(propertiesNode2, field.getName());
          processSpecificationArguments(propertyNode, dataFetcherContainer, field.getArguments());
          processEntitiesCollection(fieldDescription, propertyNode, dataFetcherContainer, field.getSelectionSet());
          dataFetcherContainer.removeStep();
          processNodeWithAlias(propertiesNode2, field);
        } else if (entityDescription.getGroupDescriptions().containsKey(field.getName())) {
          GroupDescription fieldDescription = entityDescription.getGroupDescription(field.getName());
          dataFetcherContainer.addStep(field, fieldDescription);
          ArrayNode propertyNode = getArrayNode(propertiesNode2, field.getName());
          propertyNode.add(Helper.OBJECT_MAPPER.createObjectNode());
          processGroup(fieldDescription, propertyNode, dataFetcherContainer, field.getSelectionSet());
          dataFetcherContainer.removeStep();
          processNodeWithAlias(propertiesNode2, field);
        } else if (field.getName().startsWith(GET_PREFIX)) {
          ObjectNode propertyNode = getObjectNode(propertiesNode2, field.getAlias() != null ? field.getAlias() : field.getName());
          processSpecificationArguments(propertyNode, dataFetcherContainer, field.getArguments());
        } else if (field.getName().equals(CALC_FIELD_NAME)) {
          processCalc((field.getAlias() != null ? field.getAlias() : field.getName()) + '#', propertiesNode2, dataFetcherContainer, field.getSelectionSet());
        }
      },
      inlineFragment -> {
        checkMergeRequestSpecificationDirective(inlineFragment::getDirectives);
        TypeName typeName = inlineFragment.getTypeCondition();
        if (GraphQLSchemaHelper.ENTITY_INTERFACE_TYPE_NAME.equals(typeName.getName())
          || entityDescription.isExtensionOf(typeName.getName())) {
          processProperties(entityDescription, node, propertiesNode, dataFetcherContainer, inlineFragment.getSelectionSet());
        } else {
          processDetails(node, dataFetcherContainer, typeName, inlineFragment.getSelectionSet());
        }
      },
      (fragmentSpread, fragmentDefinition) -> {
        TypeName typeName = fragmentDefinition.getTypeCondition();
        if (GraphQLSchemaHelper.ENTITY_INTERFACE_TYPE_NAME.equals(typeName.getName())
          || entityDescription.isExtensionOf(typeName.getName())) {
          processProperties(entityDescription, node, propertiesNode, dataFetcherContainer, fragmentDefinition.getSelectionSet());
        } else {
          processDetails(node, dataFetcherContainer, typeName, fragmentDefinition.getSelectionSet());
        }
      });
  }

  public void processNodeWithAlias(ObjectNode propertyNode, Field field) {
    if (field.getAlias() != null) {
      JsonNode propertySpec = propertyNode.get(field.getName());
      ObjectNode newPropertyNode = Helper.OBJECT_MAPPER.createObjectNode();
      if (propertySpec != null) {
        newPropertyNode.set("spec", propertySpec);
        ObjectNode specialFlagHolderNode;
        if (propertySpec instanceof ObjectNode) {
          specialFlagHolderNode = (ObjectNode) propertySpec;
        } else {
          specialFlagHolderNode = (ObjectNode) propertySpec.get(0);
        }
        BooleanNode specialFlag = (BooleanNode) specialFlagHolderNode.get(GraphQLSchemaHelper.SPECIAL_FLAG_FIELD_NAME);
        if (specialFlag != null) {
          newPropertyNode.put(GraphQLSchemaHelper.SPECIAL_FLAG_FIELD_NAME, true);
        }
      }
      newPropertyNode.put(GraphQLSchemaHelper.SPECIAL_FLAG_FIELD_NAME, true);
      newPropertyNode.put("base", field.getName());
      propertyNode.remove(field.getName());
      propertyNode.set(field.getAlias(), newPropertyNode);
    }
  }

    /**
     * Process a collection of entities
     *
     * @param entityDescription Entity description
     * @param node              Node
     * @param selectionSet      Selection
     */
  public void processEntitiesCollection(EntityDescription entityDescription, ObjectNode node, DataFetcherContainer dataFetcherContainer, SelectionSet selectionSet) {
    processSelectionSet(dataFetcherContainer, selectionSet,
      field -> {
        if (GraphQLSchemaHelper.ELEMENTS_FIELD_NAME.equals(field.getName())
          || GraphQLSchemaHelper.ENTITY_SUBSCRIPTION_TYPE_ELEMENT_FIELD_NAME.equals(field.getName())
        ) {
          node.put(GraphQLSchemaHelper.SPECIAL_FLAG_FIELD_NAME, true);
          dataFetcherContainer.addStep(field, null);
          processProperties(entityDescription, node, getPropertiesNode(node), dataFetcherContainer, field.getSelectionSet());
          dataFetcherContainer.removeStep();
        } else if (GraphQLSchemaHelper.COUNT_FIELD_NAME.equals(field.getName())) {
          node.put(EntitiesReadAccessJsonHelper.COUNT_FIELD_NAME, true);
        }
      },
      inlineFragment -> {
        checkMergeRequestSpecificationDirective(inlineFragment::getDirectives);
        processEntitiesCollection(entityDescription, node, dataFetcherContainer, inlineFragment.getSelectionSet());
      },
      (fragmentSpread, fragmentDefinition) -> processEntitiesCollection(entityDescription, node, dataFetcherContainer, fragmentDefinition.getSelectionSet()));
  }

    /**
     * Postprocess properties
     *
     * @param propertiesNode Property node
     */
  public void postProcessProperties(ArrayNode propertiesNode) {
    if (propertiesNode.get(0).isEmpty()) {
      propertiesNode.remove(0);
    } else {
      propertiesNode.get(0).fields().forEachRemaining(entry -> {
        if (entry.getValue() instanceof ObjectNode) {
          JsonNode specNode = entry.getValue().get(EntitiesReadAccessJsonHelper.SPECIFICATION_FIELD_NAME);
          if (specNode != null) {
            if (specNode instanceof ArrayNode) {
              postProcessProperties((ArrayNode) specNode);
            } else {
              postProcessNode((ObjectNode) specNode);
            }
            ((ObjectNode) entry.getValue()).remove(GraphQLSchemaHelper.SPECIAL_FLAG_FIELD_NAME);
          } else {
            postProcessNode((ObjectNode) entry.getValue());
          }
        } else {
          if (entry.getValue() instanceof ArrayNode) {
            postProcessProperties((ArrayNode) entry.getValue());
          }
        }
      });
    }
  }

    /**
     * Postprocess the node
     *
     * @param node Node
     */
  public void postProcessNode(ObjectNode node) {
    if (node.get(GraphQLSchemaHelper.SPECIAL_FLAG_FIELD_NAME) == null
      && node.get(EntitiesReadAccessJsonHelper.CALCULATED_EXPRESSION_FIELD_NAME) == null) {
      node.put(EntitiesReadAccessJsonHelper.LIMIT_FIELD_NAME, 0);
      node.remove(EntitiesReadAccessJsonHelper.OFFSET_FIELD_NAME);
      node.remove(EntitiesReadAccessJsonHelper.SORT_FIELD_NAME);
    } else {
      ArrayNode propertiesNode = (ArrayNode) node.get(EntitiesReadAccessJsonHelper.PROPERTIES_FIELD_NAME);
      if (propertiesNode != null) {
        postProcessProperties(propertiesNode);
      }
      ObjectNode detailsNode = (ObjectNode) node.get(EntitiesReadAccessJsonHelper.DETAILS_FIELD_NAME);
      if (detailsNode != null) {
        detailsNode.fields().forEachRemaining(entry -> postProcessProperties((ArrayNode) entry.getValue()));
      }
      JsonNode specNode = node.get(EntitiesReadAccessJsonHelper.SPECIFICATION_FIELD_NAME);
      if (specNode != null) {
        if (specNode instanceof ArrayNode) {
          postProcessProperties((ArrayNode) specNode);
        } else {
          postProcessNode((ObjectNode) specNode);
        }
      }
    }
    node.remove(GraphQLSchemaHelper.SPECIAL_FLAG_FIELD_NAME);
  }

    /**
     * Get path
     *
     * @param path     Path
     * @param elements The elements
     */
  public List<Object> getPath(List<Object> path, Object... elements) {
    List<Object> result = new ArrayList<>(path);
    result.addAll(Arrays.asList(elements));
    return result;
  }

    /**
     * Converts the JsonRpc entity to GQL Json
     *
     * @param errors     Errors
     * @param path       Path
     * @param entityNode Entity node
     */
  public Map<String, Object> getEntity(List<GraphQLError> errors, List<Object> path, JsonNode entityNode) {
    if (entityNode.isNull() || entityNode.isEmpty()) {
      return null;
    }
    JsonNode access = entityNode.get("access");
    if (access != null && !access.isMissingNode() && !access.booleanValue()) {
      return null;
    }

    EntityDescription entityDescription = modelDescription.getEntityDescription(entityNode.get(EntitiesReadAccessJsonHelper.TYPE_FIELD_NAME).textValue());
    String id = entityNode.get(EntitiesReadAccessJsonHelper.ID_FIELD_NAME).textValue();
    JsonNode invalidNode = entityNode.get(EntitiesReadAccessJsonHelper.INVALID_FIELD_NAME);
    if (invalidNode != null) {
      errors.add(new InvalidDataError(id, entityDescription.getName(), path));
      return null;
    }
    Map<String, Object> result = new LinkedHashMap<>();
    result.put(getIdFieldName(), id);
    result.put(Helper.TYPE, entityDescription.getName());
    JsonNode propertiesNode = entityNode.get(EntitiesReadAccessJsonHelper.PROPERTIES_FIELD_NAME);
    if (propertiesNode != null) {
      propertiesNode.fields().forEachRemaining(entry -> {
        String propertyName = getPropertyName(entry);
        JsonNode propertyValue = getPropertyValue(entry);
        if (entityDescription.getPrimitiveDescriptions().containsKey(propertyName)) {
          result.put(entry.getKey(), Helper.VALUE_CONVERTER.get(entityDescription.getPrimitiveDescription(propertyName).getType()).apply(propertyValue));
        } else if (entityDescription.getPrimitivesCollectionDescriptions().containsKey(propertyName)) {
          Map<String, Object> primitivesCollection = new LinkedHashMap<>();
          JsonNode elementsNode = propertyValue.get(EntitiesReadAccessJsonHelper.ELEMENTS_FIELD_NAME);
          List<Object> elements2 = new ArrayList<>(elementsNode.size());
          elementsNode.elements().forEachRemaining(elementNode -> elements2.add(Helper.VALUE_CONVERTER.get(entityDescription.getPrimitivesCollectionDescription(propertyName).getType()).apply(elementNode)));
          primitivesCollection.put(GraphQLSchemaHelper.ELEMENTS_FIELD_NAME, elements2);
          JsonNode countNode = propertyValue.get(EntitiesReadAccessJsonHelper.COUNT_FIELD_NAME);
          if (countNode != null) {
            primitivesCollection.put(GraphQLSchemaHelper.COUNT_FIELD_NAME, countNode.intValue());
          }
          result.put(entry.getKey(), primitivesCollection);
        } else if (entityDescription.getReferenceDescriptions().containsKey(propertyName)
          || entityDescription.getReferenceBackReferenceDescriptions().containsKey(propertyName)) {
          if (propertyValue.isNull()) {
            result.put(entry.getKey(), null);
          } else {
            result.put(entry.getKey(), getEntity(errors, getPath(path, propertyName), propertyValue));
          }
        } else if (entityDescription.getReferencesCollectionDescriptions().containsKey(propertyName)
          || entityDescription.getReferencesCollectionBackReferenceDescriptions().containsKey(propertyName)) {
          result.put(entry.getKey(), getEntitiesCollection(errors, getPath(path, propertyName), propertyValue));
        } else if ((entry.getValue().has(EntitiesReadAccessJsonHelper.CALCULATED_EXPRESSION_FIELD_NAME))) {
          processIfContainsHashSymbol(result, entry.getKey(), propertyValue);
        } else {
          GroupDescription groupDescription = entityDescription.getGroupDescription(propertyName);
          Map<String, Object> group = new LinkedHashMap<>();
          propertyValue.fields().forEachRemaining(entry2 -> {
            if (entry2.getValue().isNull()) {
              group.put(entry2.getKey(), null);
            } else if (groupDescription.getPrimitiveDescriptions().containsKey(entry2.getKey())) {
              group.put(entry2.getKey(), Helper.VALUE_CONVERTER.get(groupDescription.getPrimitiveDescription(entry2.getKey()).getType()).apply(entry2.getValue()));
            } else {
              group.put(entry2.getKey(), getEntity(errors, getPath(path, propertyName, entry2.getKey()), entry2.getValue()));
            }
          });
          result.put(entry.getKey(), group);
        }
      });
    }
    JsonNode aggregateVersionNode = entityNode.get(EntitiesReadAccessJsonHelper.AGGREGATE_VERSION_FIELD_NAME);
    if (aggregateVersionNode != null) {
      result.put(getAggregateVersionFieldName(), aggregateVersionNode.textValue());
    }
    return result;
  }

  public Map<String, Object> getEntity(JsonNode entityNode) {
    Map<String, Object> result = new LinkedHashMap<>();
    entityNode.fields().forEachRemaining(entry -> {
      JsonNode propertyValue = getPropertyValue(entry);
      if (entry.getKey().contains("#")) {
        processIfContainsHashSymbol(result, entry.getKey(), propertyValue);
      } else {
        result.put(entry.getKey(), convertValue(entry.getValue()));
      }
    });
    return result;
  }

  private void processIfContainsHashSymbol(Map<String, Object> result, String key, JsonNode propertyValue) {
    if (graphQLSchemaSettings.getCalcExprFieldsPlacement() == CalcExprFieldsPlacement.ON_EACH_TYPE) {
      result.put(key, convertValue(propertyValue));
    } else if (graphQLSchemaSettings.getCalcExprFieldsPlacement() == CalcExprFieldsPlacement.ON_SEPARATE_TYPE) {
      String[] aliases = key.split("#");
      Map<String, Object> calc = (Map<String, Object>) result.computeIfAbsent(aliases[0], (key0) -> new LinkedHashMap<>());
      calc.put(aliases[1], convertValue(propertyValue));
    }
  }

  private Object convertValue(JsonNode jsonNode) {
    if (jsonNode == null) {
      return null;
    } else if (jsonNode.isBoolean()) {
      return jsonNode.booleanValue();
    } else
      return jsonNode.textValue();
  }

  public String getPropertyName(Map.Entry<String, JsonNode> entry) {
    TextNode baseNode = (TextNode) entry.getValue().get(EntitiesReadAccessJsonHelper.BASE_PROPERTY_FIELD_NAME);
    return baseNode != null ? baseNode.textValue() : entry.getKey();
  }

  public JsonNode getPropertyValue(Map.Entry<String, JsonNode> entry) {
    JsonNode valueNode = entry.getValue().get(EntitiesReadAccessJsonHelper.VALUE_FIELD_NAME);
    return valueNode != null ? valueNode : entry.getValue();
  }

    /**
     * Get entity collection
     *
     * @param errors                 Errors
     * @param path                   Path
     * @param entitiesCollectionNode The entity collection node
     */
  public Map<String, Object> getEntitiesCollection(List<GraphQLError> errors, List<Object> path, JsonNode entitiesCollectionNode) {
    Pointer<Integer> indexPointer = new Pointer<>(0);
    return getEntitiesCollection(entitiesCollectionNode, (entityNode) ->
      getEntity(errors, getPath(path, GraphQLSchemaHelper.ELEMENTS_FIELD_NAME, indexPointer.object++), entityNode));
  }

  public Map<String, Object> getNonTypedEntitiesCollection(JsonNode entitiesCollectionNode) {
    return getEntitiesCollection(entitiesCollectionNode, (this::getEntity));
  }

  public Map<String, Object> getEntitiesCollection(JsonNode entitiesCollectionNode,
                                                   Function<JsonNode, Map<String, Object>> entitySupplier) {
    Map<String, Object> result = new LinkedHashMap<>();
    JsonNode elementsNode = entitiesCollectionNode.get(EntitiesReadAccessJsonHelper.ELEMENTS_FIELD_NAME);
    List<Map<String, Object>> elements = new ArrayList<>(elementsNode.size());
    elementsNode.elements().forEachRemaining(entityNode -> elements.add(entitySupplier.apply(entityNode)));
    result.put(GraphQLSchemaHelper.ELEMENTS_FIELD_NAME, elements);
    JsonNode countNode = entitiesCollectionNode.get(EntitiesReadAccessJsonHelper.COUNT_FIELD_NAME);
    if (countNode != null) {
      result.put(GraphQLSchemaHelper.COUNT_FIELD_NAME, countNode.intValue());
    }
    // Context handling (for multi-layered search)
    // We read from the response the attribute "context" and pass it to GraphQL
    JsonNode contextNode = entitiesCollectionNode.get("context");
    if (contextNode != null) {
      result.put(GraphQLSchemaHelper.MULTISEARCH_CONTEXT_NAME, contextNode.toString());
    }
    JsonNode shardErrorStatNode = entitiesCollectionNode.get(GraphQLSchemaHelper.SHARD_ERROR_STAT_NAME);
    if (contextNode != null) {
      result.put(GraphQLSchemaHelper.SHARD_ERROR_STAT_NAME, Helper.OBJECT_MAPPER.convertValue(shardErrorStatNode,
        new TypeReference<Map<String, Object>>() {
        }));
    }
    return result;
  }

    /**
     * Obtain the security path condition of the current path (from dataFetcherContainer)
     */
  private Optional<String> getPathConditionString(DataFetcherContainer dataFetcherContainer) {
    @NotNull Map<String, PathCondition> pathConditions = getPathConditions(dataFetcherContainer);
    if (pathConditions.isEmpty()) {
      return Optional.empty();
    }

      String currentPath = Objects.requireNonNull(dataFetcherContainer.getDataFetcherStep(), "Missing path in request context").getPath();
      // We iterate through all path conditions of the operation, looking for the right one
      return Optional.ofNullable(pathConditions.get(currentPath))
              .map(PathCondition::getCond);
  }

  @NotNull
  public Map<String, PathCondition> getPathConditions(DataFetcherContainer dataFetcherContainer) {
    if (dataFetcherContainer.getSecurityContext() != null
      && dataFetcherContainer.getSecurityContext().getSecureOperation() != null
      && dataFetcherContainer.getSecurityContext().getSecureOperation().getPathConditions() != null) {
      return dataFetcherContainer.getSecurityContext().getSecureOperation().getPathConditions();
    }
    return Collections.emptyMap();
  }
}
