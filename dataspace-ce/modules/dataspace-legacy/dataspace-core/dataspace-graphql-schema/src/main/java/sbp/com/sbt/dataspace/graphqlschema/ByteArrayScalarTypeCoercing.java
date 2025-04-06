package sbp.com.sbt.dataspace.graphqlschema;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import graphql.language.StringValue;
import graphql.schema.Coercing;

import java.io.IOException;
import java.util.Base64;

import static sbp.com.sbt.dataspace.graphqlschema.Helper.getCoercingParseLiteralException;
import static sbp.com.sbt.dataspace.graphqlschema.Helper.getCoercingParseValueException;

/**
 * Handler for scalar type ByteArray
 */
public class ByteArrayScalarTypeCoercing implements Coercing<byte[], String> {

    private boolean useFasterXmlBase64Decoder = true;

    public void setUseFasterXmlBase64Decoder(boolean useFasterXmlBase64Decoder) {
        this.useFasterXmlBase64Decoder = useFasterXmlBase64Decoder;
    }

    public boolean isUseFasterXmlBase64Decoder() {
        return useFasterXmlBase64Decoder;
    }

    @Override
    public String serialize(Object dataFetcherResult) {
        if (dataFetcherResult instanceof String) {
            return (String) dataFetcherResult;
        } else {
            return Base64.getEncoder().encodeToString((byte[]) dataFetcherResult);
        }
    }

    private byte[] decode(String source) throws IOException {

        if (useFasterXmlBase64Decoder) {
            return JsonNodeFactory.instance.textNode(source).getBinaryValue(Base64Variants.getDefaultVariant());
        }

        return Base64.getDecoder().decode(source);

    }

    @Override
    public byte[] parseValue(Object input) {
        if (input instanceof byte[]) {
            return (byte[]) input;
        } else if (input instanceof String) {
            try {
                return decode((String) input);
            } catch (Exception e) {
// Никаких действий не требуется
            }
        }
        throw getCoercingParseValueException(GraphQLSchemaHelper.BYTE_ARRAY_SCALAR_TYPE_NAME, input);
    }

    @Override
    public byte[] parseLiteral(Object input) {
        if (input instanceof StringValue) {
            try {
                return decode(((StringValue) input).getValue());
            } catch (Exception e) {
// Никаких действий не требуется
            }
        }
        throw getCoercingParseLiteralException(GraphQLSchemaHelper.BYTE_ARRAY_SCALAR_TYPE_NAME, input);
    }
}
