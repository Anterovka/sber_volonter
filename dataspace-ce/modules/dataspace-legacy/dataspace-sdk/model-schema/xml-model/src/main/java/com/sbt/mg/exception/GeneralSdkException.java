package com.sbt.mg.exception;

import com.sbt.mg.data.model.XmlEnumValue;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.XmlModelClassReference;
import com.sbt.mg.data.model.XmlModelInterfaceProperty;
import com.sbt.mg.data.model.typedef.XmlTypeDef;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GeneralSdkException extends RuntimeException implements SdkException {

    private final String solution;
    private final String errorText;

    public GeneralSdkException() {
        super();
        solution = null;
        errorText = null;
    }

    public GeneralSdkException(String errorText) {
        super();
        this.errorText = errorText;
        this.solution = "The solution to the problem is not described";
    }

    public GeneralSdkException(String errorText, String solution) {
        super();
        this.errorText = errorText;
        this.solution = solution;
    }

    @Override
    public String getSolution() {
        return solution;
    }

    @Override
    public String getErrorText() {
        return errorText;
    }

    @Override
    public String getPosition() {
        return "Creation of the SDK";
    }

    protected static String join(Object... strings) {
        return StringUtils.join(strings, " ");
    }

    protected List<SdkException> getMultipleExceptionList() {
        return Collections.singletonList(this);
    }

    @Override
    public String getMessage() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n\nError localization:\n\n");
        try {
            Class<?> curClass = getClass();

            int level = 0;
            while (true) {
                curClass = curClass.getSuperclass();

                if (!SdkException.class.isAssignableFrom(curClass)) {
                    break;
                }

                //  Check if the overridden method getPosition is defined in the class otherwise, proceed.
                if (!hasOverriddenPositionMethod(curClass)) {
                    continue;
                }

                SdkException sdkException = (SdkException) curClass.getDeclaredConstructor().newInstance();

                ++level;
                stringBuilder
                    .append("Level: ")
                    .append(level).append(" : ")
                    .append(sdkException.getPosition())
                    .append('\n');
            }
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException exc) {
            exc.printStackTrace();

            stringBuilder
                    .append("An exception occurred while displaying the error ")
                    .append(exc);
        }

        final List<SdkException> multipleExceptionList = getMultipleExceptionList();

        IntStream.range(0, multipleExceptionList.size()).forEach(index -> {

            if (multipleExceptionList.size() > 1) {
                stringBuilder
                    .append("\n--------- Error No ")
                    .append(index + 1);
            }
            stringBuilder
                .append("\n--------- Reason: ")
                .append(multipleExceptionList.get(index).getErrorText());

            stringBuilder
                .append("\n--------- Solution: ")
                .append(multipleExceptionList.get(index).getSolution());

            stringBuilder
                .append("\n\nStack trace:\n")
                .append(StringUtils.join(this.getStackTrace(), "\n\t"));

        });

        return stringBuilder.toString();

    }

    private boolean hasOverriddenPositionMethod(Class<?> clazz) {

        try {
            clazz.getDeclaredMethod("getPosition");
        } catch (NoSuchMethodException e) {
            return false;
        }

        return true;

    }

    protected static String collectClassProperties(Collection<XmlModelClassProperty> properties) {
        return collectProperties(properties, XmlModelClassProperty::getName, ", ");
    }

    protected static String collectPropertiesWithClassName(Collection<XmlModelClassProperty> properties) {
        Map<XmlModelClass, Collection<XmlModelClassProperty>> classWithProperties = new HashMap<>();
        properties.forEach(property ->
            classWithProperties.computeIfAbsent(property.getModelClass(),
                    prop -> new ArrayList<>())
                .add(property)
        );
        StringBuilder sb = new StringBuilder();
        classWithProperties.forEach((xmlModelClass, xmlModelClassProperties) -> {
            sb.append(System.lineSeparator()).append("[");
            Iterator<XmlModelClassProperty> iterator = xmlModelClassProperties.iterator();
            XmlModelClassProperty property = iterator.next();
            sb.append(property.getName());
            while (iterator.hasNext()) {
                sb.append(", ").append(iterator.next().getName());
            }
            sb.append("] in the class ").append(xmlModelClass.getName());
        });
        return sb.toString();
    }

    protected static String collectReferencesWithClassName(Collection<XmlModelClassReference> references) {
        Map<XmlModelClass, Collection<XmlModelClassReference>> classWithReferences = new HashMap<>();
        references.forEach(reference ->
            classWithReferences.computeIfAbsent(reference.getModelClass(),
                    prop -> new ArrayList<>())
                .add(reference)
        );
        StringBuilder sb = new StringBuilder();
        classWithReferences.forEach((xmlModelClass, xmlModelClassProperties) -> {
            sb.append(System.lineSeparator()).append("[");
            Iterator<XmlModelClassReference> iterator = xmlModelClassProperties.iterator();
            XmlModelClassReference reference = iterator.next();
            sb.append(reference.getName());
            while (iterator.hasNext()) {
                sb.append(", ").append(iterator.next().getName());
            }
            sb.append("] in the class ").append(xmlModelClass.getName());
        });
        return sb.toString();
    }

    protected static String collectClassProperties(Map<XmlModelClassProperty, String> propertiesToErrorsMessages) {
        return collectProperties(propertiesToErrorsMessages.entrySet(), entry -> entry.getKey().getName() + " -> error: " + entry.getValue(), ", ");
    }

    protected static String collectInterfaceProperties(Collection<XmlModelInterfaceProperty> properties) {
        return collectProperties(properties, XmlModelInterfaceProperty::getName, ", ");
    }

    protected static String collectClasses(Collection<XmlModelClass> classes) {
        return collectProperties(classes, XmlModelClass::getName, ", ");
    }

    protected static String collectEnums(Collection<XmlEnumValue> enumValues) {
        return collectProperties(enumValues, XmlEnumValue::getName, ", ");
    }

    protected static <T> String collectProperties(Collection<T> properties, Function<T, String> converter, String splitter) {
        return '[' + properties.stream().map(converter).collect(Collectors.joining(splitter)) + ']';
    }

    protected static String propertyInCLass(String prefix, XmlModelClassProperty property) {
        return String.format("properties%s %s of class %s", prefix, property.getName(), property.getModelClass().getName());
    }

    protected static String collectTypeDefNames(Collection<XmlTypeDef> overheadStringProperties) {
        return "[" + overheadStringProperties.stream()
            .map(XmlTypeDef::getName)
            .collect(Collectors.joining(", ")) + "]";
    }
}
