package sbp.com.sbt.dataspace.graphqlschema;

import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;

import java.math.BigDecimal;

import static sbp.com.sbt.dataspace.graphqlschema.Helper.getCoercingParseLiteralException;
import static sbp.com.sbt.dataspace.graphqlschema.Helper.getCoercingParseValueException;

/**
 * Handler for scalar type Float4
 */
class Float4ScalarTypeCoercing implements Coercing<Float, Float> {

  @Override
  public Float serialize(Object dataFetcherResult) {
    if (dataFetcherResult instanceof String) {
      return Float.valueOf((String) dataFetcherResult);
    } else {
      return (Float) dataFetcherResult;
    }
  }

  @Override
  public Float parseValue(Object input) {
    if (input instanceof Number || input instanceof String) {
      try {
        return new BigDecimal(input.toString()).floatValue();
      } catch (Exception e) {
          // None action
      }
    }
    throw getCoercingParseValueException(GraphQLSchemaHelper.FLOAT4_SCALAR_TYPE_NAME, input);
  }

  @Override
  public Float parseLiteral(Object input) {
    if (input instanceof FloatValue) {
      return ((FloatValue) input).getValue().floatValue();
    } else if (input instanceof IntValue) {
      return ((IntValue) input).getValue().floatValue();
    } else if (input instanceof StringValue) {
      try {
        return Float.valueOf(((StringValue) input).getValue());
      } catch (Exception e) {
          // None action
      }
    }
    throw getCoercingParseLiteralException(GraphQLSchemaHelper.FLOAT4_SCALAR_TYPE_NAME, input);
  }
}
