package com.sbt.mg.exception.checkmodel;

public class PropertyNotInBoundsException extends CheckXmlModelException {
    public PropertyNotInBoundsException(String messega, String solution) {
        super(messega, solution);
    }

    public <T> PropertyNotInBoundsException(String propertyName, T propertyValue, T minPropertyValue, T maxPropertyValue) {
        super(join("The property value", propertyName, "=", propertyValue, ", which is out of bounds MIN = [", minPropertyValue, "], MAX = [", maxPropertyValue, "]"),
            join("Set the property", propertyName, "to be greater than or equal to", minPropertyValue, "and less than or equal to", maxPropertyValue));
    }

    public static <T> PropertyNotInBoundsException ofMinValue(String propertyName, T propertyValue, T minPropertyValue) {
        return new PropertyNotInBoundsException(join("The property value", propertyName, "=", propertyValue, "is out of bounds, MAX = [", minPropertyValue, "]"),
            join("Set the property", propertyName, "to be greater than or equal to", minPropertyValue));
    }

    public static <T> PropertyNotInBoundsException ofMaxValue(String propertyName, T propertyValue, T maxPropertyValue) {
        return new PropertyNotInBoundsException(join("The property value", propertyName, "=", propertyValue, "exceeds the boundary MAX = [", maxPropertyValue, "]"),
            join("Set the property", propertyName, "to be less than or equal to", maxPropertyValue));
    }
}
