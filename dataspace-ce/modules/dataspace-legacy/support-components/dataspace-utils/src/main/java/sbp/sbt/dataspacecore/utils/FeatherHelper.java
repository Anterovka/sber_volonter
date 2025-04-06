package sbp.sbt.dataspacecore.utils;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.StringJoiner;

public class FeatherHelper {
    private FeatherHelper() {
        throw new UnsupportedOperationException();
    }

    public static String jsonNodeToFeatherString(JsonNode node) {
        return jsonNodeToFeatherString(node, null);
    }

    public static String jsonNodeToFeatherString(JsonNode node, DataType dataType) {
        if (node.isObject()) {
throw new IllegalArgumentException("Unable to convert an object type JsonNode to the fisizer's string");
        }
        if (node.isMissingNode()) {
            //TODO clarify the error path to node
            throw new IllegalArgumentException("Passed non-existent node " + node);
        }

        String result;
        if (node.isArray()) {
            result = arrayNodeToFeatherArray(node);
        } else {
            if (dataType != null) {
                switch (dataType) {
                    case STRING:
                    case TEXT:
                    case CHARACTER:
                        result = CommonUtils.screenString(node.asText());
                        break;
                    case LONG:
                    case INTEGER:
                    case SHORT:
                    case BYTE:
                    case DOUBLE:
                    case FLOAT:
                    case BIG_DECIMAL:
                        result = String.valueOf(node.numberValue());
                        break;
                    case DATE:
                        // TODO check what happens if you pass a date with time
                        //Возможно, что присвоение result = node.asText() не очень удачное решение.
                        TemporalAccessor ta = DateTimeFormatter.ISO_DATE.parse(node.asText());
                        result = "D"+ node.asText();
                        break;
                    case DATETIME:
                        ta = DateTimeFormatter.ISO_DATE_TIME.parse(node.asText());
                        result = "D" + node.asText();
                        break;
                    case OFFSET_DATETIME:
                        ta = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(node.asText());
                        result = "D" + node.asText();
                        break;
                    case TIME:
                        ta = DateTimeFormatter.ISO_TIME.parse(node.asText());
                        result = "D" + node.asText();
                        break;
                    case BOOLEAN:
                        result = String.valueOf(node.asBoolean());
                        break;
                    default:
                        throw new IllegalArgumentException("dataType " + dataType + " couldn't be process");
                }
            } else {
                result = node.isNumber() ? node.asText() : CommonUtils.screenString(node.asText());
            }
        }
        return result;
    }

    public static String arrayNodeToFeatherArray(JsonNode arrayNode) {
        return arrayNodeToFeatherArray(arrayNode, null);
    }

    public static String arrayNodeToFeatherArray(JsonNode arrayNode, DataType dataType) {
        if (!arrayNode.isArray()) {
            throw new IllegalArgumentException("A jsonNode that is not an array was passed");
        }
        StringJoiner arrayJoiner = new StringJoiner(",", "[", "]");
        for (JsonNode node : arrayNode) {
            arrayJoiner.add(jsonNodeToFeatherString(node, dataType));
        }
        return arrayJoiner.toString();
    }
}
