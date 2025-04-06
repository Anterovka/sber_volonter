package com.sbt.mg;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.sbt.mg.data.addons.XmlModelClassPropertyEmbeddable;
import com.sbt.mg.data.model.ClassStrategy;
import com.sbt.mg.data.model.CompatibleTypePair;
import com.sbt.mg.data.model.Property;
import com.sbt.mg.data.model.PropertyType;
import com.sbt.mg.data.model.TypeInfo;
import com.sbt.mg.data.model.XmlCompatibleTypePairs;
import com.sbt.mg.data.model.XmlIndex;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.XmlModelClassReference;
import com.sbt.mg.data.model.usermodel.UserXmlModelClassProperty;
import com.sbt.mg.data.type.XmlTypesInfo;
import com.sbt.mg.exception.GeneralSdkException;
import com.sbt.mg.exception.checkmodel.IncorrectRootTagException;
import com.sbt.mg.exception.checkmodel.NoMatchesFoundInLineByRegexException;
import com.sbt.mg.exception.checkmodel.PropertyNotInBoundsException;
import com.sbt.mg.exception.checkmodel.UnsupportedTimestampLengthException;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.mg.settings.XmlRenamedWords;
import com.sbt.mg.settings.XmlReservedWords;
import com.sbt.parameters.enums.Changeable;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import sbp.com.sbt.dataspace.applocks.model.interfaces.LockInfo;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sbt.mg.jpa.JpaConstants.HISTORY_BASE_POSTFIX;
import static com.sbt.mg.jpa.JpaConstants.MAX_STRING_LENGTH;
import static com.sbt.mg.jpa.JpaConstants.MAX_VALUE_FOR_MAX_CLASS_NAME_LENGTH;
import static com.sbt.mg.jpa.JpaConstants.MAX_VALUE_FOR_MAX_DB_OBJECT_NAME_LENGTH;
import static com.sbt.mg.jpa.JpaConstants.MAX_VALUE_FOR_MAX_PROPERTY_NAME_LENGTH;
import static com.sbt.mg.jpa.JpaConstants.MIN_VALUE_FOR_MAX_DB_OBJECT_NAME_LENGTH;
import static com.sbt.mg.jpa.JpaConstants.MIN_VALUE_FOR_MIN_CROPPED_CLASS_NAME_LENGTH;
import static com.sbt.mg.jpa.JpaConstants.MIN_VALUE_FOR_MIN_CROPPED_PROPERTY_NAME_LENGTH;
import static com.sbt.mg.utils.ClassUtils.isBaseClass;

/**
 * Assistant
 */
public final class ModelHelper {

    public static final int MAX_ENUM_VALUE_NAME = 40;
    public static final int DEFAULT_MAX_CLASS_NAME_LENGTH = 40;
    public static final int MAX_MODEL_NAME_LENGTH = 40;
    public static final int MAX_CLASS_VERSION_NAME_LENGTH = 40;
    public static final int MAX_PROPERTY_NAME = 40;
    public static final int MAX_TABLE_PREFIX_NAME = 20;
    public static final int MAX_STATUS_NAME_LENGTH = 80;

    public static final String STRING_TYPE = "String";
    public static final String UNICODE_STRING_TYPE = "UnicodeString";
    public static final String BOOLEAN_TYPE = "Boolean";
    public static final String ELEMENT_REFERENCE = "ElementReference";
    public static final String REFERENCE = "Reference";

    private static final Logger LOGGER = Logger.getLogger(ModelHelper.class.getName());

    public static final Set<String> SYSTEM_FIELDS;
    public static final Set<String> BASE_ENTITY_FIELDS;

    public static final XmlMapper XML_MAPPER;

    public static final Map<String, String> RESERVED_WORDS;
    public static final Map<String, String> RENAME_WORDS;
    public static final Map<String, TypeInfo> TYPES_INFO;

    public static final Map<String, List<String>> COMPATIBLE_TYPES = new HashMap<>();

    static {
        XML_MAPPER = new XmlMapper();

        Map<String, String> reserved = new HashMap<>();
        Map<String, String> renamed = new HashMap<>();
        Map<String, TypeInfo> typesInfos = new HashMap<>();
        Helper.wrap(() -> reserved.putAll(XML_MAPPER.readValue(Helper.class.getResourceAsStream("/reservedwords.xml"), XmlReservedWords.class).getReserved()));
        Helper.wrap(() -> renamed.putAll(XML_MAPPER.readValue(Helper.class.getResourceAsStream("/renamewords.xml"), XmlRenamedWords.class).getReserved()));
        Helper.wrap(() -> typesInfos.putAll(XML_MAPPER.readValue(Helper.class.getResourceAsStream("/typesInfo.xml"), XmlTypesInfo.class).getTypeInfoMap()));

        List<CompatibleTypePair> compatibleTypePairs = Helper.wrap(() -> XML_MAPPER.readValue(Helper.class.getResourceAsStream("/compatibleTypes.xml"), XmlCompatibleTypePairs.class).getCompatibleTypePairs());

        compatibleTypePairs.forEach(pair -> {
            COMPATIBLE_TYPES.putIfAbsent(pair.getSourceType(), new ArrayList<>());
            COMPATIBLE_TYPES.get(pair.getSourceType()).add(pair.getTargetType());
        });

        RESERVED_WORDS = Collections.unmodifiableMap(reserved);
        RENAME_WORDS = Collections.unmodifiableMap(renamed);
        TYPES_INFO = Collections.unmodifiableMap(typesInfos);

        Set<String> systemFields = new HashSet<>();
        systemFields.add("isDeleted");
        systemFields.add("partitionId");
        systemFields.add("ownerId");
        systemFields.add("recModelVersion");
        systemFields.add("offFlag");
        systemFields.add("chgCnt");
        systemFields.add("lastChangeDate");

        BASE_ENTITY_FIELDS = Collections.unmodifiableSet(new HashSet(systemFields));

        systemFields.add(JpaConstants.OBJECT_ID);
        systemFields.add(JpaConstants.JPA_DISCRIMINATOR_NAME);
        systemFields.add(JpaConstants.LAST_CHANGE_DATE_PROPERTY);
        systemFields.add(JpaConstants.HISTORY_OWNER_PROPERTY);
        systemFields.add(JpaConstants.HISTORY_TIME_PROPERTY);
        systemFields.add(JpaConstants.HISTORY_STATE_PROPERTY);
        systemFields.add(JpaConstants.HISTORY_NUMBER_PROPERTY);
        systemFields.add(JpaConstants.LAST_HIST_VERSION_PROPERTY);
        systemFields.add(JpaConstants.HIST_AGREGATE_ROOT);

        systemFields.add(JpaConstants.OBJECT_ID_PREFIX);
        systemFields.add(JpaConstants.SYS_CLONE_ORIGIN);
        systemFields.addAll(getLockInfoFieldNames());

        SYSTEM_FIELDS = Collections.unmodifiableSet(systemFields);
    }

    private static List<String> getLockInfoFieldNames() {
        List<Method> methods = Arrays.asList(LockInfo.class.getDeclaredMethods());
        return methods.stream()
                .filter(method -> method.getName().startsWith("get"))
                .map(method -> {
                    String methodName = method.getName();
                    return StringUtils.uncapitalize(methodName.substring(3));
                })
                .collect(Collectors.toList());
    }

    public static void validateModelName(String name, GeneralSdkException exception) {
        validateName(name, "[a-zA-z]([0-9a-zA-Z_]{0," + (MAX_MODEL_NAME_LENGTH - 1) + "})", exception);
    }

    public static void validatePropertyName(String name, int maxLength, Supplier<? extends GeneralSdkException> exception) {
        validateName(name, "[a-z]([0-9a-zA-Z]{0," + (maxLength - 1) + "})", exception);
    }

    public static void validateClassName(String name, int maxLength, GeneralSdkException exception) {
        validateName(name, "[A-Z]([0-9a-zA-Z]{0," + (maxLength - 1) + "})", exception);
    }

    public static void validateModelVersionName(String name, GeneralSdkException exception) {
        validateName(name, "([0-9a-zA-Z\\-_+.]{1," + MAX_CLASS_VERSION_NAME_LENGTH + "})", exception);
    }

    public static void validateEnumValueName(String name, int maxLength, GeneralSdkException exception) {
        validateName(name, "[A-Z]([0-9A-Z_]{0," + (maxLength - 1) + "})", exception);
    }

    public static void validateTablePrefix(String name, GeneralSdkException exception) {
        validateName(name, "[A-Z]([A-Z_]{0," + (MAX_TABLE_PREFIX_NAME - 1) + "})", exception);
    }

    public static void validateTypeName(String name, Supplier<? extends GeneralSdkException> exception) {
        validateName(name, "([0-9a-zA-Z\\[\\]])+", exception);
    }

    public static void validateReferenceType(String name, Supplier<? extends GeneralSdkException> exception) {
        if (name.isEmpty()) {
            return;
        }
        validateName(name, "([0-9a-zA-Z\\[\\]])+", exception);
    }

    private static void validateName(String name, String regex, GeneralSdkException exception) {
        if (name == null || !name.matches(regex)) {
            throw exception;
        }
    }

    private static void validateName(String name, String regex, Supplier<? extends GeneralSdkException> exception) {
        if (name == null || !name.matches(regex)) {
            throw exception.get();
        }
    }

    public static boolean validateStatusCode(String code) {
        return code.matches("[a-zA-z]([0-9a-zA-Z_]{0," + (MAX_STATUS_NAME_LENGTH - 1) + "})");
    }

    public static <T extends Comparable> void validateNumericPropertyValue(String propertyName,
                                                                           T propertyValue,
                                                                           T minValue,
                                                                           T maxValue) {
        if (propertyValue.compareTo(minValue) < 0 || propertyValue.compareTo(maxValue) > 0) {
            throw new PropertyNotInBoundsException(
                    propertyName,
                    propertyValue,
                    minValue,
                    maxValue
            );
        }
    }

    public static <T extends Comparable> void validateNumericPropertyMinValue(String propertyName,
                                                                              T propertyValue,
                                                                              T minValue) {
        if (propertyValue.compareTo(minValue) < 0) {
            throw PropertyNotInBoundsException.ofMinValue(
                    propertyName,
                    propertyValue,
                    minValue
            );
        }
    }

    public static <T extends Comparable> void validateNumericPropertyMaxValue(String propertyName,
                                                                              T propertyValue,
                                                                              T maxValue) {
        if (propertyValue.compareTo(maxValue) > 0) {
            throw PropertyNotInBoundsException.ofMaxValue(
                    propertyName,
                    propertyValue,
                    maxValue
            );
        }
    }

    public static void validateMaxDBObjectNameLength(int maxDBObjectNameLength) {
        validateNumericPropertyValue(
                "maxDBObjectNameLength",
                maxDBObjectNameLength,
                MIN_VALUE_FOR_MAX_DB_OBJECT_NAME_LENGTH,
                MAX_VALUE_FOR_MAX_DB_OBJECT_NAME_LENGTH
        );
    }

    public static void validateMaxClassNameLength(int maxClassNameLength) {
        validateNumericPropertyMaxValue(
                "maxClassNameLength",
                maxClassNameLength,
                MAX_VALUE_FOR_MAX_CLASS_NAME_LENGTH
        );
    }

    public static void validateMaxPropertyNameLength(int maxPropertyNameLength) {
        validateNumericPropertyMaxValue(
                "maxPropertyNameLength",
                maxPropertyNameLength,
                MAX_VALUE_FOR_MAX_PROPERTY_NAME_LENGTH
        );
    }

    public static void validateMinCroppedClassNameLength(int minCroppedClassNameLength) {
        validateNumericPropertyMinValue(
                "minCroppedClassNameLength",
                minCroppedClassNameLength,
                MIN_VALUE_FOR_MIN_CROPPED_CLASS_NAME_LENGTH
        );
    }

    public static void validateMinCroppedPropertyNameLength(int minCroppedPropertyNameLength) {
        validateNumericPropertyMinValue(
                "minCroppedPropertyNameLength",
                minCroppedPropertyNameLength,
                MIN_VALUE_FOR_MIN_CROPPED_PROPERTY_NAME_LENGTH
        );
    }

    public static boolean simpleIndexAlreadyDefined(XmlModelClassProperty modelClassProperty) {
        return modelClassProperty.getModelClass().getIndices().stream().anyMatch(
                index -> {
                    Property property = index.getProperties().get(0);
                    return property.getProperty().equals(modelClassProperty) && index.getProperties().size() == 1;
                });
    }

    public static boolean isCompatibleType(XmlModelClassProperty newProperty, XmlModelClassProperty prevProperty) {
        if (isPrimitiveType(newProperty.getType()) && isPrimitiveType(prevProperty.getType())
                && COMPATIBLE_TYPES.containsKey(prevProperty.getType())) {
            List<String> targetCompatibleTypes = COMPATIBLE_TYPES.get(prevProperty.getType());
            return targetCompatibleTypes.contains(newProperty.getType());
        }
        return false;
    }

    public static boolean isNotEqualsType(XmlModelClassProperty firstProperty, XmlModelClassProperty secondProperty) {
        return firstProperty.isEnum() != secondProperty.isEnum()
                || !getJavaType(firstProperty).equals(getJavaType(secondProperty))
                || !getClearDbType(firstProperty).equals(getClearDbType(secondProperty));
    }

    private static String getJavaType(XmlModelClassProperty property) {
        TypeInfo typeInfo = property.getTypeInfo();
        if (property.getCategory() == PropertyType.PRIMITIVE) {
            return typeInfo.getJavaName();
        } else {
            return property.getType();
        }
    }

    private static String getClearDbType(XmlModelClassProperty property) {
        String propertyDbType = getPropertyDbType(property);

        if (propertyDbType.contains("type_offsetDateTime")) {
            return "offsetDateTime";
        }

        int firstBrace = propertyDbType.indexOf('(');

        if (firstBrace == -1) {
            return propertyDbType;
        } else {
            return propertyDbType.substring(0, firstBrace);
        }
    }

    public static boolean isFirstExtentsOrEqualsSecondType(XmlModelClassProperty newModelProperty, XmlModelClassProperty baseModelProperty, boolean withEquals) {
        if (newModelProperty.getLength() == baseModelProperty.getLength()
                && newModelProperty.getScale() == baseModelProperty.getScale()) {
            return withEquals;
        }

        boolean lengthFlag = true;
        if (newModelProperty.getLength() != null) {
            lengthFlag = withEquals
                    ? newModelProperty.getLength() >= baseModelProperty.getLength()
                    : newModelProperty.getLength() > baseModelProperty.getLength();
        } else {
            // The length can be null in newProperty and be NOT null in baseProperty, for example, for long
            // Невероятно, но факт
            // It is assumed that null can only be for elements that do not provide a length value.
            // TODO figure this out
            lengthFlag = withEquals;
        }

        boolean scaleFlag = true;
        if (newModelProperty.getScale() != null) {
            scaleFlag = withEquals
                    ? newModelProperty.getScale() >= baseModelProperty.getScale()
                    : newModelProperty.getScale() > baseModelProperty.getScale();
        } else {
            // Watch comment for length
            scaleFlag = withEquals;
        }

        return lengthFlag && scaleFlag;
    }

    public static String getHumanReadableType(XmlModelClassProperty property) {
        StringBuilder result = new StringBuilder();

        result.append(property.getType());
        if (property.getLength() != null) {
            result.append(String.format("(length=%s", property.getLength()));
        }
        if (property.getScale() != null) {
            result.append(String.format(", scale=%s)", property.getScale()));
        } else {
            result.append(")");
        }

        return result.toString();
    }

    public static String getPropertyDbType(XmlModelClassProperty property) {
        TypeInfo typeInfo = property.getTypeInfo();
        if (property.getCategory() == PropertyType.PRIMITIVE) {
            Integer length = property.getLength();
            if ("string".equals(typeInfo.getHbmName()) && length != null) {
                if ("unicodestring".equals(typeInfo.getNames().get(0)) || property.isUnicode()) {
                    return "VARCHAR(" + length + " ${postfix_type})";
                }
                return "VARCHAR(" + length + ")";
            }
            Integer scale = property.getScale();
            if ("big_decimal".equals(typeInfo.getHbmName()) && length != null && scale != null) {
                return "NUMBER(" + length + "," + scale + ")";
            }

            if ("date".equalsIgnoreCase(typeInfo.getJavaName()) && length != null) {
                return "TIMESTAMP(" + length + ")";
            }

            if ("offsetdatetime".equalsIgnoreCase(typeInfo.getJavaName()) && length != null) {
                switch (length) {
                    case 0:
                        return "${type_offsetDateTime0}";
                    case 1:
                        return "${type_offsetDateTime1}";
                    case 2:
                        return "${type_offsetDateTime2}";
                    case 3:
                        return "${type_offsetDateTime3}";
                    case 4:
                        return "${type_offsetDateTime4}";
                    case 5:
                        return "${type_offsetDateTime5}";
                    case 6:
                        return "${type_offsetDateTime6}";
                    default:
                        throw new UnsupportedTimestampLengthException(property);
                }
            }

            if ("localdatetime".equalsIgnoreCase(typeInfo.getJavaName()) && length != null) {
                if (length >= 0 && length <= 6) {
                    return "TIMESTAMP(" + length + ")";
                } else {
                    throw new UnsupportedTimestampLengthException(property);
                }
            }
        }

        if (typeInfo.getDbType() == null) {
            return typeInfo.getOracleName();
        } else {
            return typeInfo.getDbType();
        }
    }

    public static String getOldPropertyDbType(XmlModelClassProperty property) {

        TypeInfo typeInfo = property.getTypeInfo();
        if (property.getCategory() == PropertyType.PRIMITIVE) {

            Integer length = property.getLength();
            Integer scale = property.getScale();
            if (property.propertyChanged(XmlModelClassProperty.PRECISION_TAG)) {
                scale = property.getOldValueChangedProperty(XmlModelClassProperty.PRECISION_TAG);
            }
            if (property.propertyChanged(XmlModelClassProperty.LENGTH_TAG)
                    || property.propertyChanged(XmlModelClassProperty.UNICODE_TAG)) {
                length = property.getOldValueChangedProperty(XmlModelClassProperty.LENGTH_TAG);
                if ("clob".equals(typeInfo.getHbmName())) {
                    // magic number 4000 - length after which varchar transforms into clob
                    if (length <= MAX_STRING_LENGTH) {
                        return "VARCHAR(" + length + ")";
                    }
                }
            }


            if ("string".equals(typeInfo.getHbmName()) && length != null) {
                if ("unicodestring".equals(typeInfo.getNames().get(0)) || property.isUnicode()) {
                    return "VARCHAR(" + length + " ${postfix_type})";
                }
                return "VARCHAR(" + length + ")";
            }
            if ("big_decimal".equals(typeInfo.getHbmName()) && length != null && scale != null) {
                return "NUMBER(" + length + "," + scale + ")";
            }

            if ("date".equalsIgnoreCase(typeInfo.getJavaName()) && length != null) {
                return "TIMESTAMP(" + length + ")";
            }

            if ("offsetdatetime".equalsIgnoreCase(typeInfo.getJavaName()) && length != null) {
                switch (length) {
                    case 0:
                        return "${type_offsetDateTime0}";
                    case 1:
                        return "${type_offsetDateTime1}";
                    case 2:
                        return "${type_offsetDateTime2}";
                    case 3:
                        return "${type_offsetDateTime3}";
                    case 4:
                        return "${type_offsetDateTime4}";
                    case 5:
                        return "${type_offsetDateTime5}";
                    case 6:
                        return "${type_offsetDateTime6}";
                    default:
                        throw new UnsupportedTimestampLengthException(property);
                }
            }

            if ("localdatetime".equalsIgnoreCase(typeInfo.getJavaName()) && length != null) {
                if (length >= 0 && length <= 6) {
                    return "TIMESTAMP(" + length + ")";
                } else {
                    throw new UnsupportedTimestampLengthException(property);
                }
            }
        }

        if (typeInfo.getDbType() == null) {
            return typeInfo.getOracleName();
        } else {
            return typeInfo.getDbType();
        }
    }

    public static String getApproximateUserType(XmlModelClassProperty property) {
        if (property.isEnum()) {
            return property.getType();
        }
        TypeInfo typeInfo = property.getTypeInfo();
        if (typeInfo != null) {
            List<String> names = new LinkedList<>(typeInfo.getNames());
            if (names.size() == 1) {
                return names.get(0);
            } else if (names.size() > 1) {
                String name0 = names.get(0);
                names.remove(name0);
                return String.format("%s (%s)", name0, String.join(",", names));
            }
        }
        return property.getType();
    }

    public static String getOldPropertyDbType4IntermediaryBuild(XmlModelClassProperty prevProperty) {

        String oldNameType = prevProperty.getType();
        Integer length = prevProperty.getLength();

        TypeInfo typeInfo = prevProperty.getTypeInfo();

        if (isPrimitiveType(oldNameType)) {

            if ("string".equalsIgnoreCase(oldNameType)) {
                if (prevProperty.isUnicode()) {
                    return String.format("VARCHAR(%s ${postfix_type})", length);
                }
                return String.format("VARCHAR(%s)", length);
            }
            if ("big_decimal".equalsIgnoreCase(oldNameType)) {
                return String.format("NUMBER(%s,%s)", length, prevProperty.getScale());
            }

            if ("date".equalsIgnoreCase(oldNameType)) {
                return String.format("TIMESTAMP(%s)", length);
            }

            if ("offsetdatetime".equalsIgnoreCase(oldNameType)) {
                if (length != null) {
                    switch (length) {
                        case 0:
                            return "${type_offsetDateTime0}";
                        case 1:
                            return "${type_offsetDateTime1}";
                        case 2:
                            return "${type_offsetDateTime2}";
                        case 4:
                            return "${type_offsetDateTime4}";
                        case 5:
                            return "${type_offsetDateTime5}";
                        case 6:
                            return "${type_offsetDateTime6}";
                        case 3:
                        default:
                            return "${type_offsetDateTime3}";
                    }
                } else {
                    return "${type_offsetDateTime3}";
                }
            }

            if ("localdatetime".equalsIgnoreCase(oldNameType)) {
                if (length >= 0 && length <= 6) {
                    return String.format("TIMESTAMP(%s)", length);
                } else {
                    return "TIMESTAMP(3)";
                }
            }

        }

        if (typeInfo.getDbType() == null) {
            return typeInfo.getOracleName();
        } else {
            return typeInfo.getDbType();
        }
    }

    public static void initDefaults(XmlModel model) {
        initBaseMarkClass(model);
        initStrategy(model);
        initHistoryClassReference(model);
    }

    /**
     * Initializes historic classes by linking them to the corresponding history class
     */
    private static void initHistoryClassReference(XmlModel model) {
        model.getClassesAsList().stream()
                .filter(XmlModelClass::isHistoryClass)
                .forEach(historyClass -> {
                    XmlModelClass historicalClass = historyClass.getHistoryForClass(); // For throwing an exception
                    historicalClass.setHistoryClass(historyClass.getName());
                });
    }

    /**
     * Sets the base class flag on appropriate classes
     */
    public static void initBaseMarkClass(XmlModel model) {
        model.getClassesAsList().stream()
                .filter(ModelHelper::isModelClassBaseMark)
                .forEach(modelClass -> modelClass.setBaseClassMark(Boolean.TRUE));
    }

    /**
     * Is the model class base (computable, not by property)
     */
    public static boolean isModelClassBaseMark(XmlModelClass modelClass) {
        return !modelClass.isAbstract()
                && !modelClass.isEmbeddable()
                && getAllSuperClasses(modelClass).stream().allMatch(XmlModelClass::isAbstract);
    }

    public static boolean isFirstUserClass(XmlModelClass modelClass) {
        return Objects.isNull(modelClass.getExtendedClassName());
    }

    public static boolean isTypeIsClassAndNotInCurrentImportModel(String type, XmlModel model, Set<String> importModelNames) {
        XmlModelClass aClass = model.getClassNullable(type);
        return aClass != null &&
                importModelNames.stream().noneMatch(it -> it.equalsIgnoreCase(aClass.getImportModelName()));
    }

    /**
     * Copies the class strategy from base to descendants
     */
    public static void initStrategy(XmlModel model) {
        model.getClassesAsList().forEach(modelClass -> {

            if (modelClass.isBaseClassMark()) {

                if (modelClass.getStrategy() == null) {
                    modelClass.setStrategy(ClassStrategy.JOINED);
                }

                getAllChildClasses(modelClass)
                        .forEach(inheritedClass -> inheritedClass.setStrategy(modelClass.getStrategy()));
            }

        });
    }

    public static XmlModelClass getBaseClass(XmlModelClass modelClass) {
        if (modelClass == null) {
            return null;
        }
        if (!modelClass.isBaseClassMark()) {
            return getBaseClass(modelClass.getModel().getClassNullable(modelClass.getExtendedClassName()));
        }
        return modelClass;
    }

    public static XmlModelClass getBaseClassNotNullable(XmlModelClass modelClass) {
        if (!modelClass.isBaseClassMark()) {
            if (modelClass.getExtendedClassName() == null) {
                return modelClass;
            }
            return getBaseClassNotNullable(modelClass.getModel().getClass(modelClass.getExtendedClassName()));
        }
        return modelClass;
    }

    /**
     * First class after BASE_CLASS_NAME. Can be abstract (not to be base).
     */
    public static XmlModelClass getFirstClass(XmlModelClass modelClass) {
        if (modelClass == null) {
            return null;
        }
        if (modelClass.getExtendedClassName() != null && !isBaseClass(modelClass.getExtendedClassName())) {
            return getFirstClass(modelClass.getModel().getClassNullable(modelClass.getExtendedClassName()));
        }
        return modelClass;
    }

    /**
     * Getting all ancestors of a given class
     */
    public static List<XmlModelClass> getAllSuperClasses(XmlModelClass modelClass) {
        return getAllSuperClasses(modelClass, false);
    }

    /**
     * Getting all ancestors of a given class
     *
     * @param withHeadClass add the original class to the list
     */
    public static List<XmlModelClass> getAllSuperClasses(XmlModelClass modelClass, boolean withHeadClass) {
        if (modelClass == null) {
            return Collections.emptyList();
        }
        XmlModel model = modelClass.getModel();

        List<XmlModelClass> modelClasses = new ArrayList<>();

        if (withHeadClass) {
            modelClasses.add(modelClass);
        }

        while (modelClass.getExtendedClassName() != null) {
            modelClass = model.getClass(modelClass.getExtendedClassName());
            modelClasses.add(modelClass);
        }

        return modelClasses;
    }

    /**
     * Getting all classes that are descendants of the passed
     */
    public static Set<XmlModelClass> getAllChildClasses(XmlModelClass modelClass) {
        return getAllChildClasses(modelClass, false);
    }

    /**
     * Getting all classes that are descendants of the passed
     */
    public static Set<XmlModelClass> getAllChildClasses(XmlModelClass modelClass, boolean withHeadClass) {
        Set<XmlModelClass> extendedClasses = modelClass.getModel().getClassesAsList().stream()
                .filter(aClass -> aClass.getExtendedClassName() != null &&
                        aClass.getExtendedClassName().equals(modelClass.getName()))
                .collect(Collectors.toSet());
        if (extendedClasses.isEmpty()) {
            if (withHeadClass) {
                return Collections.singleton(modelClass);
            }
            return Collections.emptySet();
        }

        Set<XmlModelClass> result = new HashSet<>();
        extendedClasses.forEach(aClass -> {
            result.add(aClass);
            result.addAll(getAllChildClasses(aClass));
        });

        if (withHeadClass) {
            result.add(modelClass);
        }

        return result;
    }

    /**
     * Obtaining nearest classes that are descendants of the transmitted
     */
    public static Set<XmlModelClass> getChildClasses(XmlModelClass modelClass) {
        return getChildClasses(modelClass, false);
    }

    /**
     * Obtaining nearest classes that are descendants of the transmitted
     */
    public static Set<XmlModelClass> getChildClasses(XmlModelClass modelClass, boolean withHeadClass) {
        Set<XmlModelClass> result = modelClass.getModel().getClassesAsList().stream()
                .filter(aClass -> aClass.getExtendedClassName() != null &&
                        aClass.getExtendedClassName().equals(modelClass.getName()))
                .collect(Collectors.toSet());
        if (result.isEmpty()) {
            if (withHeadClass) {
                return Collections.singleton(modelClass);
            }
            return Collections.emptySet();
        }

        if (withHeadClass) {
            result.add(modelClass);
        }

        return result;
    }

    /**
     * Obtaining all classes of the inheritance branch in which the passed class participates
     */
    public static Set<XmlModelClass> getAllClassesInInheritance(XmlModelClass modelClass, boolean withThisClass) {
        Set<XmlModelClass> result = new HashSet<>();
        result.addAll(getAllSuperClasses(modelClass));
        result.addAll(getAllChildClasses(modelClass));
        if (withThisClass) {
            result.add(modelClass);
        }
        return result;
    }

    public static String transformClassToReferenceClass(String className) {
        return className + REFERENCE;
    }

    public static String transformReferenceTypeToClassType(String className, String referenceType) {
        return referenceType.substring(className.length(), referenceType.indexOf(ELEMENT_REFERENCE));
    }

    /**
     * Removes the suffix "Reference" from the string
     */
    public static String transformReferenceClassToOriginClass(String className) {
        return className.substring(0, className.length() - REFERENCE.length());
    }

    public static String transformReferenceTypeToOriginClassName(XmlModelClassProperty property) {
        if (property.getCollectionType() != null) {
            XmlModelClass typeClass = property.getModelClass().getModel().getClass(property.getType());
            property = typeClass.getProperty(REFERENCE.toLowerCase());
        }
        return transformReferenceClassToOriginClass(property.getType());
    }

    public static Collection<XmlModelClassProperty> getAllPropertiesWithInherited(XmlModelClass modelClass) {
        return getAllSuperClasses(modelClass, true).stream()
                .flatMap(it -> it.getPropertiesAsList().stream())
                .collect(Collectors.toList());
    }

    public static boolean useIdGenerator(XmlModelClass modelClass) {

        switch (modelClass.getId().getIdCategory()) {
            case SNOWFLAKE:
            case AUTO_ON_EMPTY:
            case UUIDV4:
            case UUIDV4_ON_EMPTY:
                return true;
            case MANUAL:
                return !modelClass.getPropertyWithHierarchyNullable(JpaConstants.OBJECT_ID).isEmbedded()
                        && modelClass.getClassAccess() == Changeable.UPDATE
                        && !modelClass.isDictionary();
            default:
                return false;
        }
    }

    public static final Predicate<String> isHistoryUpdatedFlag = propName -> {
        // sys + Updated = 10 characters, there should be another field name, so more
        //The name of the property starts with sys, ends with Updated, and after sys there is an uppercase letter.
        return propName.length() > 10
                && propName.startsWith("sys")
                && propName.endsWith("Updated")
                && Character.isUpperCase(propName.charAt(3));
    };

    public static boolean isIndexingHistoryUpdatedFlags(XmlModel xmlModel) {
        return xmlModel.getClassesAsList().stream()
                .filter(XmlModelClass::isHistoryClass)
                .flatMap(modelClass -> modelClass.getIndices().stream())
                .flatMap(index -> index.getProperties().stream().map(Property::getName))
                .anyMatch(isHistoryUpdatedFlag);
    }

    public static boolean isPrimitiveType(String type) {
        type = type.toLowerCase(Locale.ENGLISH);
        return TYPES_INFO.containsKey(type);
    }

    public static boolean isBooleanType(String type) {
        TypeInfo typeInfo = TYPES_INFO.get(type.toLowerCase(Locale.ENGLISH));
        if (typeInfo == null) {
            return false;
        }
        return "boolean".equalsIgnoreCase(typeInfo.getJavaName());
    }

    public static boolean isReferenceType(XmlModel model, String type) {
        return !isPrimitiveType(type) && !isEnum(model, type) && !isModelClassType(model, type);
    }

    public static boolean isEnum(XmlModel model, String type) {
        return model.containsEnum(type);
    }

    public static boolean isBinaryType(XmlModelClassProperty property) {
        TypeInfo typeInfo = TYPES_INFO.get(property.getType().toLowerCase(Locale.ENGLISH));
        if (typeInfo == null) {
            return false;
        }
        return "byte[]".equalsIgnoreCase(typeInfo.getJavaName());
    }

    /**
     * Returns the nearest non-abstract descendants
     */
    public static List<XmlModelClass> findClosestNotAbstractChilds(XmlModelClass abstractClass) {
        XmlModel model = abstractClass.getModel();
        List<XmlModelClass> goalClasses = new ArrayList<>();

        model.getClassesAsList().stream()
                .filter(modelClass -> Objects.equals(modelClass.getExtendedClassName(), abstractClass.getName()))
                .forEach(modelClass -> {
                    if (modelClass.isAbstract()) {
                        goalClasses.addAll(findClosestNotAbstractChilds(modelClass));
                    } else {
                        goalClasses.add(modelClass);
                    }
                });

        return goalClasses;
    }

    public static boolean isClob(XmlModelClassProperty property) {
        TypeInfo typeInfo = TYPES_INFO.get(property.getType().toLowerCase(Locale.ENGLISH));
        if (typeInfo == null) {
            return false;
        }
        return typeInfo.getFullName().equals(String.class.getCanonicalName())
                && property.getLength() > 4000;
    }

    // It looks strange that for the primary index uniqueness is false, but the logic was ported from Abramov
    public static void addComplexIndexToClass(XmlModelClass modelClass,
                                              boolean unique,
                                              boolean primaryKey,
                                              List<XmlModelClassProperty> properties,
                                              boolean fromFieldIndex) {

        XmlIndex xmlIndex = XmlIndex.create()
                .setFromField(fromFieldIndex)
                .setUnique(unique)
                .setPrimaryKey(primaryKey);

        properties.forEach(xmlProperty -> {
            Property property = new Property(xmlProperty.getName());
            property.setProperty(xmlProperty);
            xmlIndex.addProperty(property);
        });
        modelClass.addIndex(xmlIndex);
    }

    public static void addComplexIndexToClass(XmlModelClass modelClass,
                                              List<XmlModelClassReference> references,
                                              boolean fromFieldIndex) {

        XmlIndex xmlIndex = XmlIndex.create()
                .setFromField(fromFieldIndex);

        references.forEach(xmlReference -> {
            Property property = new Property(xmlReference.getName());
            property.setReference(xmlReference);
            xmlIndex.addProperty(property);
        });
        modelClass.addIndex(xmlIndex);
    }

    /**
     * Index creation and addition to the passed class
     */
    public static void addComplexIndexToClassByStringProps(XmlModelClass modelClass,
                                                           boolean unique,
                                                           boolean primaryKey,
                                                           List<String> propNames) {

        XmlIndex xmlIndex = XmlIndex.create();

        propNames.forEach(propName -> {
            Property property = new Property(propName);
            xmlIndex.addProperty(property);
        });

        if (unique) {
            xmlIndex.setUnique();
        }
        if (primaryKey) {
            xmlIndex.setPrimaryKey();
        }

        modelClass.addIndex(xmlIndex);
    }

    public static void checkRootTagName(File modelFile, String correctTagName) {
        Helper.wrap(() -> {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(modelFile);
            Node root = document.getDocumentElement();
            String rootTagName = root.getNodeName();
            if (!correctTagName.equals(rootTagName)) {
                throw new IncorrectRootTagException(rootTagName, correctTagName);
            }
        });
    }

    public static String findLineByRegex(String text, String regEx) {
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return text.substring(matcher.start(), matcher.end());
        } else {
            throw new NoMatchesFoundInLineByRegexException(String.format("In the line [%s] no matches were found for the RegEx expression [%s]!", text, regEx));
        }
    }

    /**
     * Get all historical properties for the passed classes
     */
    public static List<XmlModelClassProperty> getAllProperties(Stream<XmlModelClass> classes) {
        return classes
                .flatMap(xmlModelClass -> xmlModelClass.getPropertiesAsList().stream())
                .collect(Collectors.toList());
    }

    /**
     * Collection of properties where embedded properties are revealed in XmlModelClassPropertyEmbeddable
     */
    public static List<XmlModelClassProperty> propertyToPropertyEmbeddable(List<XmlModelClassProperty> properties) {
        if (properties.isEmpty()) {
            return Collections.emptyList();
        }
        // We select non-embedded properties
        List<XmlModelClassProperty> result = properties.stream()
                .filter(prop -> !prop.isEmbedded()).collect(Collectors.toList());
        XmlModel model = properties.get(0).getModelClass().getModel();

        // Expand embedded properties in XmlModelClassPropertyEmbeddable sets
        List<XmlModelClassProperty> embeddedClassProperties = properties.stream()
                .filter(XmlModelClassProperty::isEmbedded)
                .flatMap(prop -> {
                    XmlModelClass embeddableClass = model.getClass(ModelHelper.getRealTypeIfReference(prop));
                    return embeddableClass.getPropertiesAsList().stream()
                            .map(embProp -> new XmlModelClassPropertyEmbeddable(prop, embProp));
                })
                .collect(Collectors.toList());
        result.addAll(embeddedClassProperties);
        return result;
    }

    /**
     * Obtain all properties that MIGHT BE in the SINGLE_TABLE table, into which the passed class would be written.
     * Properties include XmlModelClassPropertyEmbeddable
     */
    public static List<XmlModelClassProperty> getAllPropertiesEmbeddableAsSingleTable(XmlModelClass clazz) {
        //All properties, including embedded but not embeddable
        List<XmlModelClassProperty> properties = getAllProperties(getAllChildClasses(getFirstClass(clazz), true).stream());
        return propertyToPropertyEmbeddable(properties);
    }

    /**
     * If the property is embedded, then a collection of XmlModelClassPropertyEmbeddable is returned
     */
    public static List<XmlModelClassPropertyEmbeddable> propertyToPropertyEmbeddable(XmlModelClassProperty property) {
        XmlModelClass embeddableClass = getTypeClassByProperty(property);
        return embeddableClass.getPropertiesAsList().stream()
                .map(embProp -> new XmlModelClassPropertyEmbeddable(property, embProp))
                .collect(Collectors.toList());
    }

    public static XmlModelClass getTypeClassByProperty(XmlModelClassProperty property) {
        return property.getModelClass().getModel().getClass(ModelHelper.getRealTypeIfReference(property));
    }

    public static String getRealTypeIfReference(XmlModelClassProperty property) {
        if (property.getCategory() != PropertyType.REFERENCE || property.getReferenceType() == null) {
            return property.getType();
        }

        return property.getReferenceType();
    }

    public static boolean isRootDictionary(String className) {
        return JpaConstants.ROOT_DICTIONARY_CLASS_NAME.equals(className);
    }

    public static boolean isModelClassType(XmlModel model, String type) {
        return model.containsClass(type);
    }

    public static Field[] getXmlModelPropertyDeclaredFields() {
        return ArrayUtils.addAll(
                XmlModelClassProperty.class.getDeclaredFields(),
                UserXmlModelClassProperty.class.getDeclaredFields());
    }

    public static Method[] getXmlModelPropertyDeclaredMethods() {
        return ArrayUtils.addAll(
                XmlModelClassProperty.class.getDeclaredMethods(),
                UserXmlModelClassProperty.class.getDeclaredMethods());
    }

    public static boolean fkGeneratedChangedFromTrue(XmlModelClassProperty xmlModelClassProperty) {
        return xmlModelClassProperty.propertyChanged(XmlModelClassProperty.FK_GENERATED_TAG)
                && Boolean.TRUE.equals(xmlModelClassProperty.getOldValueChangedProperty(XmlModelClassProperty.FK_GENERATED_TAG));
    }

    public static boolean fkDeleteCascadeChanged(XmlModelClassProperty xmlModelClassProperty) {
        return xmlModelClassProperty.propertyChanged(XmlModelClassProperty.FK_DELETE_CASCADE_TAG);
    }

    public static boolean fkGeneratedChangedFromFalse(XmlModelClassProperty xmlModelClassProperty) {
        return xmlModelClassProperty.propertyChanged(XmlModelClassProperty.FK_GENERATED_TAG)
                && (Objects.isNull(xmlModelClassProperty.getOldValueChangedProperty(XmlModelClassProperty.FK_GENERATED_TAG))
                || Boolean.FALSE.equals(xmlModelClassProperty.getOldValueChangedProperty(XmlModelClassProperty.FK_GENERATED_TAG)));
    }

    /**
     * Get the name of the historicized class from the class name history.
     * Removes everything starting with the last postfix History:
     * SomeClassHistory -> SomeClass
     */
    public static String parseHistoricalClassName(String historyClassName) {
        return StringUtils.substringBeforeLast(historyClassName, HISTORY_BASE_POSTFIX);
    }

    private ModelHelper() {

    }
}
