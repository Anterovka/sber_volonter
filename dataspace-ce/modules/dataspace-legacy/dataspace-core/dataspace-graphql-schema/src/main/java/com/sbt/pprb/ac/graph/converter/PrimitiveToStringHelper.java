package com.sbt.pprb.ac.graph.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.util.RawValue;
import com.sbt.mg.Pair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.StringJoiner;

public class PrimitiveToStringHelper {

    /**
     * Converts an object to a string that can be consumed by Feather.
     * Boolean - whether to wrap the string in quotes in the JSONRPC protocol
     */
    public static Pair<String, Boolean> getString(Object object) {
        if (object instanceof Iterable) {
            StringJoiner sj = new StringJoiner(",", "[", "]");
            for (Object elem : (Iterable) object) {
                Pair<String, Boolean> pair = getString(elem);
                if (pair.getSecond()) {
                    sj.add("\"" + pair.getFirst() + "\"");
                } else {
                    sj.add(pair.getFirst());
                }
            }

            return new Pair<>(sj.toString(), false);
        }
        String serialized = serializeSingleValue(object);
        return new Pair<>(serialized, object != null && !(object instanceof Boolean));
    }

    private static String serializeSingleValue(Object object) {
        if (object instanceof Date) {
            return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(
                LocalDateTime.ofInstant(((Date) object).toInstant(), java.time.ZoneId.systemDefault()));
        } else if (object instanceof LocalDate) {
            return DateTimeFormatter.ISO_LOCAL_DATE.format((LocalDate) object);
        } else if (object instanceof LocalDateTime) {
            return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format((LocalDateTime) object);
        } else if (object instanceof OffsetDateTime) {
            return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format((OffsetDateTime) object);
        } else if (object == null) {
            return null;
        } else {
            return object.toString();
        }
    }

    public static JsonNode getValue(Object object) {
        if (object instanceof Iterable) {
            ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
            for (Object elem : (Iterable) object) {
                String serialized = serializeSingleValue(elem);
                if (elem instanceof Boolean) {
                    arrayNode.addRawValue(new RawValue(serialized));
                } else {
                    arrayNode.add(serialized);
                }
            }

            return arrayNode;
        }
        String serialized = serializeSingleValue(object);
        if (object instanceof Boolean) {
            return JsonNodeFactory.instance.rawValueNode(new RawValue(serialized));
        } else {
            return new TextNode(serialized);
        }
    }
}
