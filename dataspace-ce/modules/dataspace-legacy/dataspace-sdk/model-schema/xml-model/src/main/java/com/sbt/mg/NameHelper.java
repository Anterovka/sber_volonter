package com.sbt.mg;

import com.sbt.mg.data.model.ClassStrategy;
import com.sbt.mg.data.model.Property;
import com.sbt.mg.data.model.PropertyType;
import com.sbt.mg.data.model.XmlIndex;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.unusedschemaItems.XmlUnusedSchemaItems;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;
import com.sbt.mg.exception.checkmodel.NonEmbeddableClassPropertyException;
import com.sbt.mg.exception.checkmodel.StrategyNotDefinedException;
import com.sbt.mg.exception.common.UnsupportedTypeDbObjectException;
import com.sbt.mg.jpa.JpaConstants;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NameHelper {
    /**
     * A separator that is added to the name when assigning a numeric postfix. Used when names intersect
     */
    public static final String POSTFIX_DELIMETER = "_";
    /**
     * The length of the separator
     */
    public static final int POSTFIX_DELIMETER_LENGTH = POSTFIX_DELIMETER.length();
    public static final String LINK_FIELD_POSTFIX = "_ID";
    public static final String ORDER_POSTFIX = "_ORDER";

    public enum TypeDbObject {
        TABLE, INDEX, FOREIGN_KEY
    }

    private NameHelper() {
    }

    /**
     * Assigns the physical table name to classes if it is missing in the class
     *
     * @param modelClass Model class
     */
    @Nonnull
    public static void setTableName(@Nonnull XmlModelClass modelClass, XmlModel oldModel, int maxDBObjectNameLength) {
        if (modelClass.getTableName() != null) {
            return;
        }
        setNewTableName(modelClass, maxDBObjectNameLength);
    }

    /**
     * Sets a new table name to the class, regardless of its presence in pdm or on the class
     */
    private static void setNewTableName(@Nonnull XmlModelClass modelClass, int maxDBObjectNameLength) {
        String tableName;
        switch (modelClass.getStrategy() == null ? ClassStrategy.JOINED : modelClass.getStrategy()) {
            case JOINED:
                tableName = generateTableName(modelClass, maxDBObjectNameLength);
                modelClass.setTableName(tableName);
                break;
            case SINGLE_TABLE:
                // All classes have the same physical name, generated for the base class.
                //Therefore, we check if there is a physical name of the base class if not, we generate it.
                // Retrieve physical name from the base class to all descendants.
                XmlModelClass baseModelClass = ModelHelper.getBaseClass(modelClass);
                if (baseModelClass.getTableName() == null) {
                    tableName = generateTableName(baseModelClass, maxDBObjectNameLength);
                    baseModelClass.setTableName(tableName);
                }
// If the class itself is not basic, then we set the physical name; otherwise, it was already set above
                if (modelClass != baseModelClass) {
                    modelClass.setTableName(baseModelClass.getTableName());
                }
                break;
            default:
                throw new StrategyNotDefinedException(modelClass);
        }
    }

    /**
     * Generates a name for the class.
     * If the name does not exceed the specified length, the name is returned without checking for overlap with existing names.
     * If the name exceeds the allowed length, the name is truncated and checked for overlap with existing table names.
     * At the intersection, a postfix is added to the name, which increases from 1 until the intersection of names disappears.
     * We don't check if the class is SingleTable, generates the name head-on
     */
    private static String generateTableName(@Nonnull XmlModelClass modelClass, int maxDBObjectNameLength) {
        XmlModel model = modelClass.getModel();
        String tablePrefix = model.getTablePrefix();

        if (!StringUtils.isEmpty(tablePrefix)) {
            tablePrefix += '_';
        }

        String tableNamePrefix = "T_" + tablePrefix;
        String resultTableName = getName(tableNamePrefix + modelClass.getName(), maxDBObjectNameLength);
        // If the name is taken, then we generate with a suffix
        if (containsName(resultTableName, model, TypeDbObject.TABLE)) {

            resultTableName = getNameWithPostfix(resultTableName,
                maxDBObjectNameLength,
                (name) -> containsName(name, model, TypeDbObject.TABLE));
        }
        return resultTableName;
    }

    /**
     * Adds and increments suffix in name while there is an intersection with existing names.
     * The name is always returned with the postfix
     */
// TODO want to combine all WithPostfix functions into one, by passing lambda, but is it worth it?
    private static String getNameWithPostfix(String name,
                                             int maxLength,
                                             Function<String, Boolean> containsName) {
        name = name.toUpperCase(Locale.ENGLISH);
        String newObjectName;
        int basePostfix = 1;
        do {
            int postfixLength = Helper.countDigits(basePostfix) + POSTFIX_DELIMETER_LENGTH;
            if (name.length() + postfixLength >= maxLength) {
                name = name.substring(0, maxLength - postfixLength);
            }
            newObjectName = name + POSTFIX_DELIMETER + basePostfix;
            basePostfix++;
        } while (containsName.apply(newObjectName));

        return newObjectName;
    }

    /**
     * Checks for the presence of a table or index name in the model. If the name has already been added to the model previously, an exception is thrown.
     * The name itself is not added to the model
     */
    private static boolean containsName(String name, XmlModel xmlModel, TypeDbObject typeDbObject) {
        if (typeDbObject == TypeDbObject.TABLE) {
            return xmlModel.containsTableName(name);
        } else if (typeDbObject == TypeDbObject.INDEX) {
            return xmlModel.getIndexNames().contains(name);
        } else if (typeDbObject == TypeDbObject.FOREIGN_KEY) {
            return xmlModel.getFkNames().contains(name);
        } else {
            throw new UnsupportedTypeDbObjectException(typeDbObject);
        }
    }

    /**
     * Checks for the presence of a column name in a class or hierarchy of classes. If the name has already been added previously, an exception is thrown.
     * The name itself is not added to the class
     */
    private static boolean containsColumnName(String name, XmlModelClass xmlModelClass) {
        Set<String> columnNamesReal = xmlModelClass.getColumnNamesReal();
        XmlUnusedSchemaItems unusedSchemaItems = xmlModelClass.getModel().getUnusedSchemaItems();
        return columnNamesReal.contains(name)
            || unusedSchemaItems != null
            && xmlModelClass.getTableName() != null
            && unusedSchemaItems.containsColumn(xmlModelClass.getTableName(), name);
    }

    /**
     * Get table name for collection
     *
     * @param modelClassProperty Class property of the model
     */
    @Nonnull
    public static String getTableNameForCollectionProperty(
        @Nonnull XmlModelClassProperty modelClassProperty,
        int maxDBObjectNameLength,
        int minCroppedClassNameLength,
        int minCroppedPropertyNameLength) {

        XmlModel model = modelClassProperty.getModelClass().getModel();
        String tablePrefix = model.getTablePrefix();

        if (!StringUtils.isEmpty(tablePrefix)) {
            tablePrefix += '_';
        }

        String categoryPrefix = modelClassProperty.getCategory() == PropertyType.REFERENCE ? "L_" : "LC_";
        String modelClassName = modelClassProperty.getModelClass().getName();
        String propertyName = modelClassProperty.getName();

        return getUniqueName(
            TypeDbObject.TABLE,
            model,
            tablePrefix,
            categoryPrefix,
            modelClassName,
            propertyName,
            maxDBObjectNameLength,
            minCroppedClassNameLength,
            minCroppedPropertyNameLength);
    }

    private static String getUniqueName(TypeDbObject typeDbObject,
                                        XmlModel model,
                                        String tablePrefix,
                                        String categoryPrefix,
                                        String modelClassName,
                                        String propertyName,
                                        int maxDBObjectNameLength,
                                        int minCroppedClassNameLength,
                                        int minCroppedPropertyNameLength) {
        String resultObjectName = getName(
            categoryPrefix,
            tablePrefix,
            modelClassName,
            propertyName,
            maxDBObjectNameLength,
            minCroppedClassNameLength,
            minCroppedPropertyNameLength
        );

        if (containsName(resultObjectName, model, typeDbObject)) {
            resultObjectName = getNameWithPostfix(
                categoryPrefix,
                tablePrefix,
                modelClassName,
                propertyName,
                model,
                maxDBObjectNameLength,
                minCroppedClassNameLength,
                minCroppedPropertyNameLength,
                typeDbObject
            );
        }

        return resultObjectName;
    }

    /**
     * Returns the field name, consisting of the passed parts and truncated to the given length without adding suffixes.
     */
    private static String getName(String categoryPrefix,
                                  String tablePrefix,
                                  String modelClassName,
                                  String propertyName,
                                  int maxNameLength,
                                  int minCroppedClassNameLength,
                                  int minCroppedPropertyNameLength) {
// If the name does not exceed the length, then we return it as is
        String resultObjectName = getResultObjectName(categoryPrefix, tablePrefix, modelClassName, propertyName);
        if (resultObjectName.length() <= maxNameLength) {
            return resultObjectName;
        }

        int modelClassNameLength = modelClassName.length();
        int propertyNameLength = propertyName.length();

//exceeding the object length over the maximum length
        int exceedingLength = resultObjectName.length() - maxNameLength;

//how much can we crop the class name at most
        int maxCropClassNameLength = Math.max(modelClassNameLength - minCroppedClassNameLength, 0);
// how much we can cut the field name for maximum optimization
        int maxCropPropertyNameLength = Math.max(propertyNameLength - minCroppedPropertyNameLength, 0);

// If necessary to cut more than allow fields, then an error occurs.
        if (exceedingLength > maxCropClassNameLength + maxCropPropertyNameLength) {
            throw new CheckXmlModelException("Невозможно сформировать физическое имя для поля" + propertyName +
                " in the class " + modelClassName + " with parameters {minCroppedClassNameLength = " + minCroppedClassNameLength + ", " +
                "minCroppedPropertyNameLength = " + minCroppedPropertyNameLength + ", maxDBObjectNameLength = " + maxNameLength +
                "}, original name: " + resultObjectName, "It is necessary to either change the value of the given parameters or the field and/or class name");
        }

//check if we can crop class name at all (i.e. it is longer than MIN_NAME_LENGTH_AFTER_CROP characters)
        if (maxCropClassNameLength > 0) {
// determine how much we need to trim the class name
            int cropClassNameLength = modelClassNameLength - Math.min(exceedingLength, maxCropClassNameLength);
// Trim the class name
            modelClassName = modelClassName.substring(0, cropClassNameLength);
            exceedingLength -= maxCropClassNameLength;
        }

// If it is necessary to cut more than the class name allows, then we cut the field name
        if (exceedingLength > 0) {
            propertyName = propertyName.substring(0, propertyName.length() - exceedingLength);
        }
// формируем имя после урезания
        return getResultObjectName(categoryPrefix, tablePrefix, modelClassName, propertyName);
    }

    /**
     * Concatenates the passed elements into a string
     */
    private static String getResultObjectName(String categoryPrefix, String tablePrefix, String modelClassName, String propertyName) {
        StringBuilder sb = new StringBuilder();
        sb.append(categoryPrefix)
            .append(tablePrefix)
            .append(modelClassName)
            .append('_')
            .append(propertyName);
        return sb.toString().toUpperCase(Locale.ENGLISH);
    }

    /**
     * Returns the name always with postfix.
     * Method for composite name.
     */
    private static String getNameWithPostfix(
        String categoryPrefix,
        String tablePrefix,
        String modelClassName,
        String propertyName,
        XmlModel model,
        int maxDBObjectNameLength,
        int minCroppedClassNameLength,
        int minCroppedPropertyNameLength,
        TypeDbObject typeDbObject) {

        String resultObjectName;
        int i = 1;
        int digitsCount = 0;
        String baseName = null;

        do {
            int newDigitsCount = Helper.countDigits(i);
            if (newDigitsCount != digitsCount) {
                baseName = getName(
                    categoryPrefix,
                    tablePrefix,
                    modelClassName,
                    propertyName,
                    maxDBObjectNameLength - POSTFIX_DELIMETER_LENGTH - Helper.countDigits(i),
                    minCroppedClassNameLength,
                    minCroppedPropertyNameLength
                );
                digitsCount = newDigitsCount;
            }

            resultObjectName = baseName + POSTFIX_DELIMETER + i;

            ++i;
        } while (containsName(resultObjectName, model, typeDbObject));
        return resultObjectName;
    }

    /**
     * Trims to the specified length, converts to uppercase, and checks for reservedness of the name.
     *
     * @param name Name
     */
    @Nonnull
    public static String getName(@Nonnull String name, int maxDBObjectNameLength) {
        if (name.length() > maxDBObjectNameLength) {
            name = name.substring(0, maxDBObjectNameLength);
        }
        return checkReservedWords(name);
    }

    public static String getName(@Nonnull String name, int maxDBObjectNameLength, boolean useRename) {
        String newName = getName(name, maxDBObjectNameLength);
        if (useRename) {
            newName = checkRenameWords(newName);
        }

        return newName;
    }

    /**
     * Checks whether the passed word is not overloaded; if it is reserved, then replaces it
     */
    public static String checkReservedWords(String name) {
        name = name.toUpperCase(Locale.ENGLISH);
// if the word is reserved, then converts it to unreserved according to the RESERVED_WORDS reference book
        return ModelHelper.RESERVED_WORDS.getOrDefault(name, name);
    }

    public static String checkRenameWords(String name) {
        name = name.toUpperCase(Locale.ENGLISH);
// if the word is reserved, then converts it to unreserved according to the RESERVED_WORDS reference book
        return ModelHelper.RENAME_WORDS.getOrDefault(name, name);
    }

    public static String getFkIndexName(XmlModelClassProperty xmlModelClassProperty, int maxDBObjectNameLength) {
        String indexName = getName(String.format("fk_%s_%s",
            xmlModelClassProperty.getModelClass().getName(),
            xmlModelClassProperty.getName()).toUpperCase(Locale.ENGLISH), maxDBObjectNameLength);
        if (containsName(indexName, xmlModelClassProperty.getModelClass().getModel(), TypeDbObject.FOREIGN_KEY)) {
            indexName = getNameWithPostfix(indexName,
                maxDBObjectNameLength,
                (name) -> containsName(name, xmlModelClassProperty.getModelClass().getModel(), TypeDbObject.INDEX));
        }
        return indexName;
    }

    /**
     * Get the name of the primary key index
     *
     * @param modelClass Model class
     */
    @Nonnull
    public static String getPkIndexName(@Nonnull XmlModelClass modelClass, int maxDBObjectNameLength) {
        if (modelClass.getStrategy() == ClassStrategy.SINGLE_TABLE && !modelClass.isBaseClassMark()) {
            XmlModelClass baseClass = ModelHelper.getBaseClass(modelClass);
            return baseClass.getPkIndexName() != null ? baseClass.getPkIndexName() : getPkIndexName(baseClass, maxDBObjectNameLength);
        }
        String indexName = getName("PK_" + modelClass.getTableName(), maxDBObjectNameLength);
// If the name is taken, then we generate with a suffix
        if (containsName(indexName, modelClass.getModel(), TypeDbObject.INDEX)) {
            indexName = getNameWithPostfix(indexName,
                maxDBObjectNameLength,
                (name) -> containsName(name, modelClass.getModel(), TypeDbObject.INDEX));
        }
        return indexName;
    }

    /**
     * Get the name of the primary key index of the table collection
     *
     * @param modelClassProperty The model class
     */
    @Nonnull
    public static String getPkIndexNameForCollectionProperty(@Nonnull XmlModelClassProperty modelClassProperty, int maxDBObjectNameLength) {
        String indexName = "PK_" + modelClassProperty.getCollectionTableName();
        if (indexName.length() <= maxDBObjectNameLength) {
            return indexName;
        }
// TODO to rework into normal index name formation through class name truncation, not property name.
        return getNameWithPostfix(
            indexName,
            maxDBObjectNameLength,
            (name) -> containsName(name, modelClassProperty.getModelClass().getModel(), TypeDbObject.INDEX)
        );
    }

    public static String makeIndexName(XmlIndex index,
                                       int maxDBObjectNameLength,
                                       int minCroppedClassNameLength,
                                       int minCroppedPropertyNameLength) {
        XmlModelClass modelClass = index.getModelClass();
        XmlModel model = modelClass.getModel();

        String categoryPrefix = "I_";

        String tablePrefix = model.getTablePrefix();

        if (!StringUtils.isEmpty(tablePrefix)) {
            tablePrefix += '_';
        }

// TODO for now we keep backward compatibility, but we need to bring it to uniformity.
        String modelClassName = index.isFromField() ||
            modelClass.getStrategy() != ClassStrategy.SINGLE_TABLE ?
            modelClass.getName().toUpperCase(Locale.ENGLISH) :
            ModelHelper.getBaseClass(modelClass).getName().toUpperCase(Locale.ENGLISH);

        List<Property> indexProperties = index.getProperties();

        String indexNameWithoutProperties = categoryPrefix + tablePrefix + modelClassName;

        if (indexProperties.isEmpty()) {
            throw new IllegalStateException(
                String.format(
                    "The index in class %s is described with an error, since it does not contain any properties." +
                    "Error in the index with the name %s", modelClass.getName(), indexNameWithoutProperties
                )
            );
        }

        Property property = indexProperties.get(0);

        String propertyName;
// If the property is embeddable
        if (property.getPropertyOwner() != null) {
            propertyName = index.isFromField()
                ? property.getPropertyOwner().getName()
                : XmlModelClass.getEmbeddedProperty(property.getPropertyOwner(),
                NameHelper.getEmbeddedIndexPropertyName(index.getProperties().get(0))).getColumnName();
        } else {
            propertyName = property.getProperty().getColumnName();
        }

        boolean amountPropertiesOver1 = index.isFromField() ? false : indexProperties.size() > 1;
        return getCroppedIndexNameWithPostfix(
            categoryPrefix,
            tablePrefix,
            modelClassName,
            propertyName,
            model,
            maxDBObjectNameLength,
            minCroppedClassNameLength,
            minCroppedPropertyNameLength,
            amountPropertiesOver1);
    }

    private static boolean isIndexByFullEmbeddedProp(XmlIndex index) {
        List<Property> indexProperties = index.getProperties();
        XmlModelClassProperty propertyOwner = indexProperties.get(0).getPropertyOwner();
        String embeddableType = propertyOwner.getType();
        XmlModelClass embeddableClass = propertyOwner.getModelClass().getModel().getClass(embeddableType);
        List<XmlModelClassProperty> embeddableProps = embeddableClass.getPropertiesAsList().stream()
            .filter(prop -> !prop.isSystemField())
            .collect(Collectors.toList());
        if (indexProperties.size() == embeddableProps.size()) {
// We check that all fields from the embeddable object are in the index.
            for (Property indexProp : indexProperties) {
                embeddableProps.remove(indexProp.getProperty());
            }
        }

        return embeddableProps.isEmpty();
    }

    private static String getCroppedIndexNameWithPostfix(String categoryPrefix,
                                                         String tablePrefix,
                                                         String modelClassName,
                                                         String propertyName,
                                                         XmlModel model,
                                                         int maxDBObjectNameLength,
                                                         int minCroppedClassNameLength,
                                                         int minCroppedPropertyNameLength,
                                                         boolean amountPropertiesOver1) {

        String indexName = getName(
            categoryPrefix,
            tablePrefix,
            modelClassName,
            propertyName,
            maxDBObjectNameLength,
            minCroppedClassNameLength,
            minCroppedPropertyNameLength);

// TODO eliminate amountPropertiesOver1 and translate to getUniqueName
        if (amountPropertiesOver1 || containsName(indexName, model, TypeDbObject.INDEX)) {
            indexName = getNameWithPostfix(
                categoryPrefix,
                tablePrefix,
                modelClassName,
                propertyName,
                model,
                maxDBObjectNameLength,
                minCroppedClassNameLength,
                minCroppedPropertyNameLength,
                TypeDbObject.INDEX
            );
        }

        return indexName;
    }

    // TODO needs to be reworked so that at least something remains of both composite names
    public static String getEmbeddedColumnName(XmlModelClassProperty rootProperty,
                                               XmlModelClassProperty embeddedProperty,
                                               int maxColumnNameLength) {

        String columnName = embeddedProperty.getColumnName();

        String resultName;
// Checking the name to avoid breaking the logic of already existing system classes.
        if (rootProperty.isId() && Objects.equals(rootProperty.getName(), JpaConstants.COMPOSITE_ID)) {
            resultName = columnName;
        } else {
            resultName = rootProperty.getName() + '_' + columnName;
        }

        columnName = getName(resultName, maxColumnNameLength);

        if (containsColumnName(columnName, rootProperty.getModelClass())) {
            columnName = getNameWithPostfix(
                columnName,
                maxColumnNameLength,
                (name) -> containsColumnName(name, rootProperty.getModelClass())
            );
        }
        return columnName;
    }

    public static String generateColumnName(
        XmlModelClassProperty modelClassProperty,
        int maxDBObjectNameLength,
        boolean useRename) {
// We get the correct collection of field names
        XmlModelClass modelClass = modelClassProperty.getModelClass();
        String columnName = modelClassProperty.getCategory() == PropertyType.PRIMITIVE
            ? modelClassProperty.getName()
            : modelClassProperty.getName() + "_ID";

        columnName = getName(columnName, maxDBObjectNameLength, useRename);
        if (containsColumnName(columnName, modelClass)) {
            columnName = getNameWithPostfix(columnName, maxDBObjectNameLength, (name) -> containsColumnName(name, modelClass));
        }
        return columnName;
    }

    public static String getEmbeddedIndexPropertyName(Property property) {
        if (!property.getProperty().getModelClass().isEmbeddable()) {
            throw new NonEmbeddableClassPropertyException(property);
        }

        String propertyName = property.getName();

        return propertyName.substring(propertyName.indexOf('.') + 1);
    }

    /**
     * Get the column name for collection order
     *
     * @param modelClassProperty Class property of the model
     */
    public static String getPrimitiveCollectionOrderColumnName(XmlModelClassProperty modelClassProperty, int maxDbObjectNameLength) {
        return getName(modelClassProperty.getName(), maxDbObjectNameLength - ORDER_POSTFIX.length()) + ORDER_POSTFIX;
    }

    /**
     * Get the column name for the collection key, which in fact is a reference to the parent class
     *
     * @param modelClass Model class
     */
    public static String getPrimitiveCollectionPkColumnName(XmlModelClass modelClass, int maxDbObjectNameLength) {
        return getName(modelClass.getName(), maxDbObjectNameLength - LINK_FIELD_POSTFIX.length()) + LINK_FIELD_POSTFIX;
    }

    /**
     * Check if property attributes have changed so that copying the physical column name is pointless
     */
    public static boolean notCopyColumnName(XmlModelClassProperty newProp, XmlModelClassProperty oldProp) {
        return !newProp.getType().equals(oldProp.getType())
            || newProp.getCollectionType() != null
            || newProp.isEmbedded() != oldProp.isEmbedded()
            || newProp.isExternalLink() != oldProp.isExternalLink()
            || newProp.getMappedBy() != null;
    }

    /**
     * Check if property attributes have changed so that copying the physical column name is pointless
     */
    public static boolean notCopyTableName(XmlModelClass newClass, XmlModelClass oldClass) {
        return newClass.getStrategy() != oldClass.getStrategy()
            || newClass.isDictionary() != oldClass.isDictionary()
            || newClass.isExternalReference() != oldClass.isExternalReference();
    }
}
