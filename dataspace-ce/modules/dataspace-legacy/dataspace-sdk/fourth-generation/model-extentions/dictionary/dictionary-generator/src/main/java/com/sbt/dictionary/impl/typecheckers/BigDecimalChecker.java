package com.sbt.dictionary.impl.typecheckers;

import com.sbt.dictionary.impl.checktypeexceptions.DictionaryDataException;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.XmlModelClassProperty;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;

public class BigDecimalChecker extends TypeChecker {

    private final String id;
    private final String fieldName;
    private final String type;
    private final XmlModelClassProperty property;

    public BigDecimalChecker(String id, String fieldName, String type, XmlModelClassProperty property) {
        this.id = id;
        this.fieldName = fieldName;
        this.type = type;
        this.property = property;
    }

    @Override
    public void check(Object value) {
        if (Objects.isNull(value)) {
            return;
        }
        checkParse(value);
        checkLength(value);
        checkScale(value);
    }

    private void checkParse(Object value) {
        try {
            new BigDecimal(value.toString());
        } catch (NumberFormatException ex) {
            throw new DictionaryDataException(id, fieldName, value, type,
                String.format("Error during parsing of bigDecimal value: %s", getMessage(ex)));
        }
    }

    private void checkLength(Object value) {
        if (length(value) > getPropertyLength(property)) {
            throw new DictionaryDataException(id, fieldName, value, type,
                String.format("The length of bigDecimal exceeds the maximum possible (%s > %s)", length(value), property.getLength())
            );
        }
    }

    private int length(Object value) {
        return value.toString().replace(".", "").length();
    }

    private void checkScale(Object value) {
        final int valueScale = scale(value);
        final int valueLength = length(value);
        final int propertyScale = propertyScale(property);
        if (valueScale > propertyScale) {
            throw new DictionaryDataException(id, fieldName, value, type,
                String.format("The number of digits after the decimal point in bigDecimal exceeds the maximum allowable (%s > %s)",
                    valueScale, propertyScale));
        }
        if (valueScale < 0 || valueScale >= valueLength) {
            throw new DictionaryDataException(id, fieldName, value, type,
                String.format("The number of digits after the decimal point in bigDecimal cannot be less than zero or greater than the length" +
                        "(0<scale(%s)<length(%s))",
                    valueScale, valueLength));
        }
    }

    private int scale(Object value) {
        final String[] split = value.toString().split("\\.");
        if (split.length > 1) {
            return split[1].length();
        }
        return 0;
    }

    private static int propertyScale(XmlModelClassProperty property) {
        if (!Objects.isNull(property.getScale())) {
            return property.getScale();
        }
        return ModelHelper.TYPES_INFO.get(property.getType().toLowerCase(Locale.ENGLISH)).getSecondNumber();
    }
}
