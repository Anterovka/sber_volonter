package com.sbt.model.diff;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.ParameterContext;
import com.sbt.mg.ElementState;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.CollectionType;
import com.sbt.mg.data.model.PropertyType;
import com.sbt.mg.data.model.TypeInfo;
import com.sbt.mg.data.model.XmlEmbeddedList;
import com.sbt.mg.data.model.XmlEmbeddedProperty;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.model.exception.ExternalReferenceDefaultValueException;
import com.sbt.model.exception.ReferenceDefaultValueException;
import com.sbt.model.exception.ReferenceStorageException;
import com.sbt.model.exception.SimpleMandatoryFieldMustBeFilledException;
import com.sbt.model.exception.diff.AggregateChangeException;
import com.sbt.model.exception.diff.CollectionTypeChangeException;
import com.sbt.model.exception.diff.ComputedExpressionChangeException;
import com.sbt.model.exception.diff.IncompatibleTypeConversionException;
import com.sbt.model.exception.diff.IncompatibleTypeException;
import com.sbt.model.exception.diff.ParentPropertyCanNotBeDeprecatedException;
import com.sbt.model.exception.diff.TypeChangeException;
import com.sbt.model.exception.diff.TypeReduceException;
import com.sbt.model.exception.diff.UnicodeStringToStringModificationException;
import com.sbt.model.exception.diff.UnsupportedDefaultValueInDiffException;
import com.sbt.model.utils.Models;
import com.sbt.reference.ExternalReferenceGenerator;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static com.sbt.mg.ModelHelper.transformReferenceClassToOriginClass;
import static com.sbt.mg.ModelHelper.transformReferenceTypeToClassType;
import static com.sbt.model.diff.MessageHandler.getMessageChangeCollectionType;
import static com.sbt.model.diff.MessageHandler.getMessageChangeEmbeddedProperty;
import static com.sbt.model.diff.MessageHandler.getMessageChangePropertyToReferenceRoot;
import static com.sbt.model.diff.MessageHandler.getMessageChangeReferenceRootToProperty;
import static com.sbt.model.diff.MessageHandler.getMessageChangeType;
import static com.sbt.model.diff.MessageHandler.getMessageReductionLengthType;
import static com.sbt.model.diff.MessageHandler.getMessageReductionScaleType;
import static com.sbt.model.foreignkey.ForeignKeyHelper.getFkDeleteCascadeCondition;
import static com.sbt.model.foreignkey.ForeignKeyHelper.getForeignKeyCondition;
import static com.sbt.model.foreignkey.ForeignKeyHelper.prepareDropForeignKey;

public class PropertyDiff implements DiffHandler {
    private static final Logger LOGGER = Logger.getLogger(PropertyDiff.class.getName());

    private boolean disableCompatibilityCheck;

    public PropertyDiff(boolean disableCompatibilityCheck) {
        this.disableCompatibilityCheck = disableCompatibilityCheck;
    }

    @Override
    public void handler(XmlModel newModel, XmlModel baseModel, ParameterContext parameterContext) {
        ModelParameters modelDiff = parameterContext.getModelParameters();
        LOGGER.info("\n\n  The verification properties phase (beginning)");
// handles changes to undeleted properties
        checkDiff(newModel, baseModel, modelDiff);
        LOGGER.info("\n\n  Phase of properties checking (completion)");
    }

    /**
     * Handles changes to undeleted properties
     */
    private void checkDiff(XmlModel newModel, XmlModel baseModel, ModelParameters modelDiff) {
        baseModel.getClassesAsList().forEach(baseModelClass -> {
// If the class is deleted, then we skip it
            if (!newModel.containsClass(baseModelClass.getName())) {
                return;
            }

            XmlModelClass newModelClass = newModel.getClass(baseModelClass.getName());

            baseModelClass.getPropertiesAsList().forEach(prevProperty -> {
// If the property is deleted, then we skip
                if (!newModelClass.containsProperty(prevProperty.getName())) {
                    return;
                }

                XmlModelClassProperty newProperty = newModelClass.getProperty(prevProperty.getName());

                AtomicBoolean propertyUpdated = new AtomicBoolean(false);

// There was an o2o relationship; now it's o2m. The property name remained the same.
                if (!Objects.isNull(prevProperty.getMappedBy()) && Objects.isNull(newProperty.getMappedBy())) {
                    String oldMappedBy = prevProperty.getMappedBy();
                    modelDiff.addDiffObject(ElementState.NEW, newProperty);
// if the class we are referring to has become embeddable (with incompatible changes)
                    if (!Objects.equals(prevProperty.isEmbedded(), newProperty.isEmbedded())) {
                        modelDiff.addBackwardCompatibilityViolationMessage(getMessageChangeEmbeddedProperty(prevProperty));
                    }
                    baseModelClass.removeProperty(prevProperty.getName());
                    baseModelClass.addProperty(newProperty);
                    if (Boolean.TRUE.equals(newProperty.isExternalLink())) {
                        if (!newProperty.getOriginalType().equals(prevProperty.getType())) {
                            newProperty.addChangedProperty(XmlModelClassProperty.TYPE_TAG, prevProperty.getType());
                        }
                        newProperty.addChangedProperty(XmlModelClassProperty.MAPPED_BY_TAG, oldMappedBy);
                        propertyUpdated.set(true);
                    }
                    return;
                }

                prevProperty.setCollectionPkIndexName(newProperty.getCollectionPkIndexName());

                checkUnicode(prevProperty, newProperty, propertyUpdated);
                checkType(prevProperty, newProperty, propertyUpdated, modelDiff);
                checkDeprecated(prevProperty, newProperty, propertyUpdated, modelDiff);
                checkMandatoryChange(prevProperty, newProperty, propertyUpdated);
                checkDefaultValue(prevProperty, newProperty, propertyUpdated);
                checkChangeable(prevProperty, newProperty, propertyUpdated);
                checkHistorical(prevProperty, newProperty, propertyUpdated);
                checkComputedExpression(prevProperty, newProperty);
                checkCollectionType(prevProperty, newProperty, propertyUpdated, modelDiff);
                checkLabel(prevProperty, newProperty, propertyUpdated);
                checkOriginalType(prevProperty, newProperty, propertyUpdated);
                checkExternalSoftReference(prevProperty, newProperty, propertyUpdated);
                checkMask(prevProperty, newProperty);
                checkIntegrityCheck(prevProperty, newProperty);
//                То же самое, что и для уникальности
                checkIndexed(prevProperty, newProperty);
//                The idea is that the uniqueness attribute should be reset when translated into a complex index.
                checkUnique(prevProperty, newProperty);
                checkFkGenerated(prevProperty, newProperty, propertyUpdated);
                checkFkDeleteCascade(prevProperty, newProperty, propertyUpdated);
                // since at the very beginning embedded properties are restored in newModel, then for such properties
                // We will always get to this method.
                if (disableCompatibilityCheck) {
                    checkExternalLink(prevProperty, newProperty, modelDiff);
                    checkEmbedded(prevProperty, newProperty, propertyUpdated);
                    checkColumName(prevProperty, newProperty, propertyUpdated);
                }

                //wa in case not all properties have been transferred
                //TODO   (PII) didn't understand why the second condition is needed; it leads to attribute having partial properties.
                // copied, while in another part - not."
                if (disableCompatibilityCheck && (prevProperty.getAllChanges().size() > 0)) {
                    //TODO   not transferred property isEmbeddable
                    // Need to log such properties
                    cloneProperty(prevProperty, newProperty);
                }

                if (propertyUpdated.get()) {
                    modelDiff.addDiffObject(ElementState.UPDATED, prevProperty);
//                    If the EMBEDDED_TAG attribute changes, then we log it.
                    if (prevProperty.getAllChanges().containsKey(XmlModelClassProperty.EMBEDDED_TAG)) {
                        modelDiff.addBackwardCompatibilityViolationMessage(getMessageChangeEmbeddedProperty(prevProperty));
                    }
                }
            });
        });

        fillOriginalType(baseModel);
    }

    private void checkType(XmlModelClassProperty prevProperty,
                           XmlModelClassProperty newProperty,
                           AtomicBoolean propertyUpdated,
                           ModelParameters modelParameters) {

        if (!disableCompatibilityCheck) {
            if (ModelHelper.isCompatibleType(newProperty, prevProperty)) {

                //We allow Date->LocalDateTime, limiting LocalDateTime to an accuracy of 3, according to the statement.
                //So far, we are making a hardcode.
                //If there are similar restrictions for other pairs of transformations,
                // the entire comparison algorithm will need to be reworked
                if("Date".equals(prevProperty.getType())
                        && "LocalDateTime".equals(newProperty.getType())
                        && newProperty.getLength() != 3) {
                    throw new IncompatibleTypeConversionException(prevProperty, newProperty);
                }

                prevProperty.addChangedProperty(XmlModelClassProperty.TYPE_TAG, prevProperty.getType());
                prevProperty.setType(newProperty.getType());
                prevProperty.setLength(newProperty.getLength());
                prevProperty.setScale(newProperty.getScale());

// установка физического типа свойства
                Models.fillCategoryAndTypeInfo(prevProperty);
                propertyUpdated.set(true);
            } else {
                if (!Objects.equals(newProperty.getType(), prevProperty.getType())
                    && (newProperty.getCategory() == PropertyType.REFERENCE || prevProperty.getCategory() == PropertyType.REFERENCE)) {
                    if (JpaConstants.AGGREGATE_ROOT.equals(newProperty.getName())) {
                        throw new AggregateChangeException(newProperty.getModelClass().getName());
                    }

                    // Condition to determine that we are exactly changing the property -> reference (and only to the root of the aggregate)
                    if (newProperty.getType().endsWith(ModelHelper.REFERENCE)
                        && newProperty.isExternalLink()
                        && newProperty.getOriginalType().equals(prevProperty.getType())
                        && !newProperty.isParent()
                        && Objects.isNull(prevProperty.getMappedBy())
                        && Objects.isNull(prevProperty.getCollectionType())
                        && (newProperty.isEmbedded() && !prevProperty.isEmbedded())
                        && prevProperty.getModelClass().getModel().getClass(prevProperty.getType()).getPropertiesAsList().stream().noneMatch(XmlModelClassProperty::isParent)) {

                        prevProperty.addChangedProperty(XmlModelClassProperty.EMBEDDED_TAG, prevProperty.isEmbedded());

                        String columnName = prevProperty.getColumnName();

                        cloneProperty(prevProperty, newProperty);

                        // When converting property -> reference,
                        // this construction changes the preset column name of the embedded EntityId property (NAMEPROPERTY_ENTITYID)
                        // to the original (NAMEPROPERTY_ID)
                        Optional<XmlEmbeddedList> optionalXmlEmbeddedList = newProperty.getModelClass().getEmbeddedPropertyList().stream()
                            .filter(xmlEmbeddedList -> prevProperty.getName().equals(xmlEmbeddedList.getName()))
                            .findFirst();
                        if (optionalXmlEmbeddedList.isPresent()) {
                            XmlEmbeddedList xmlEmbeddedList = optionalXmlEmbeddedList.get();
                            Optional<XmlEmbeddedProperty> optionalXmlEmbeddedProperty = xmlEmbeddedList.getEmbeddedProperty(ExternalReferenceGenerator.ENTITY_ID);
                            if (optionalXmlEmbeddedProperty.isPresent()) {
                                XmlEmbeddedProperty xmlEmbeddedProperty = optionalXmlEmbeddedProperty.get();
                                xmlEmbeddedProperty.setColumnName(columnName);
                            }
                        }
                    } else {
                        throw new TypeChangeException(prevProperty, newProperty);
                    }
                }

                if (ModelHelper.isNotEqualsType(newProperty, prevProperty)) {
                    throw new IncompatibleTypeException(newProperty,
                        ModelHelper.getApproximateUserType(prevProperty), ModelHelper.getApproximateUserType(newProperty));
                }

                boolean isHistoryClass = newProperty.getModelClass().isHistoryClass();

//                If the dimension of the field is reduced, an exception will be thrown, except when historizing (due to an old bug in historizing).
                if (!ModelHelper.isFirstExtentsOrEqualsSecondType(newProperty, prevProperty, true)) {
                    // WA for bugs, when Fields were created in historical data without taking into account attributes such as length and scale
                    if (!isHistoryClass) {
                        throw new TypeReduceException(newProperty,
                            ModelHelper.getHumanReadableType(prevProperty), ModelHelper.getHumanReadableType(newProperty));
                    }
                }

// Check newProperty.getLength() for != null, as, for example, Long does not have a defined length.
// while in Pdm long has a length of 19.
// TODO Need to figure this out, where does the value 19 come from
                if (newProperty.getLength() != null && !Objects.equals(newProperty.getLength(), prevProperty.getLength())) {
// The condition was added due to an old bug in the historization
                    if (!isHistoryClass || newProperty.getLength() > prevProperty.getLength()) {
//                        If the user removes the length from the embedded field.In 1.8, a check was added for that.
                        if (!newProperty.isEmbedded()) {
                            propertyUpdated.set(true);
                            prevProperty.addChangedProperty(XmlModelClassProperty.LENGTH_TAG, prevProperty.getLength());
                        }
                        prevProperty.setLength(newProperty.getLength());
                        prevProperty.getTypeInfo().setFirstNumber(newProperty.getLength());
                    }
                }

                if (newProperty.getScale() != null && !Objects.equals(newProperty.getScale(), prevProperty.getScale())) {
// The condition was added due to an old bug in the historization
                    if (!isHistoryClass || newProperty.getScale() > prevProperty.getScale()) {
                        propertyUpdated.set(true);
                        prevProperty.addChangedProperty(XmlModelClassProperty.PRECISION_TAG, prevProperty.getScale());
                        prevProperty.setScale(newProperty.getScale());
                    }
                }

// TODO not related to the property and method checkType type, should be moved or removed
                if (newProperty.getAccess() != prevProperty.getAccess()) {
// TODO it seems that this property is not related to Liquibase generation, then it's unclear why it should be included in the changedProperty
                    prevProperty.addChangedProperty(XmlModelClassProperty.ACCESS_TAG, prevProperty.getAccess());
                    prevProperty.setAccess(newProperty.getAccess());
                }
            }
        } else {
            checkTypeWhenDisableCompatibilityCheck(prevProperty, newProperty, propertyUpdated, modelParameters);
        }
    }

    private void checkTypeWhenDisableCompatibilityCheck(XmlModelClassProperty prevProperty,
                                                        XmlModelClassProperty newProperty,
                                                        AtomicBoolean propertyUpdated,
                                                        ModelParameters modelParameters) {
        if (!prevProperty.getType().equals(newProperty.getType())
            || newProperty.isUnicode() != prevProperty.isUnicode()) {
            propertyUpdated.set(true);

            String oldType = prevProperty.getType();
            prevProperty.addChangedProperty(XmlModelClassProperty.TYPE_TAG, oldType);

            prevProperty.setType(newProperty.getType());
            prevProperty.setLength(newProperty.getLength());
            prevProperty.setScale(newProperty.getScale());

// We calculate the field type based on newProperty, since the property type may not have been transferred to pdm yet.
// as a consequence, when calling the function on prevProperty, an exception will occur
// TODO possibly the function call is redundant and you can just copy typeInfo
            Models.fillCategoryAndTypeInfo(newProperty);
            prevProperty.setTypeInfo(newProperty.getTypeInfo());

            modelParameters.addBackwardCompatibilityViolationMessage(getMessageChangeType(prevProperty, newProperty, oldType));

            if (
                (prevProperty.getColumnName() == null && newProperty.getColumnName() != null)
                    || (prevProperty.getColumnName() != null
                    && !prevProperty.getColumnName().equals(newProperty.getColumnName()))
            ) {
                prevProperty.addChangedProperty(XmlModelClassProperty.COLUMN_NAME_TAG, prevProperty.getColumnName());
                prevProperty.setColumnName(newProperty.getColumnName());
            }

        } else if (newProperty.getAccess() != prevProperty.getAccess()) {
            prevProperty.addChangedProperty(XmlModelClassProperty.ACCESS_TAG, prevProperty.getAccess());
            prevProperty.setAccess(newProperty.getAccess());
        } else if (!Objects.equals(newProperty.isParent(), prevProperty.isParent())) {
            prevProperty.addChangedProperty(XmlModelClassProperty.PARENT_TAG, prevProperty.isParent());
            prevProperty.setParent(newProperty.isParent());
            propertyUpdated.set(true);
        }

        if (!Objects.equals(newProperty.getLength(), prevProperty.getLength()) ||
            !Objects.equals(newProperty.getScale(), prevProperty.getScale())) {
            propertyUpdated.set(true);
            if (!Objects.equals(newProperty.getLength(), prevProperty.getLength())) {
                Integer prevPropertyLength = prevProperty.getLength();
                Integer newPropertyLength = newProperty.getLength();
                if (Objects.nonNull(prevPropertyLength) && Objects.nonNull(newPropertyLength)
                    && (newPropertyLength.compareTo(prevPropertyLength) < 0)) {
                    modelParameters.addBackwardCompatibilityViolationMessage(getMessageReductionLengthType(prevProperty, prevPropertyLength, newPropertyLength));
                }
                prevProperty.addChangedProperty(XmlModelClassProperty.LENGTH_TAG, prevPropertyLength);
                prevProperty.setLength(newPropertyLength);
                prevProperty.getTypeInfo().setFirstNumber(newPropertyLength);
            }
            if (!Objects.equals(newProperty.getScale(), prevProperty.getScale())) {
                Integer newPropertyScale = newProperty.getScale();
                Integer prevPropertyScale = prevProperty.getScale();
                if (Objects.nonNull(prevPropertyScale) && Objects.nonNull(newPropertyScale)
                    && (newPropertyScale.compareTo(prevPropertyScale) < 0)) {
                    modelParameters.addBackwardCompatibilityViolationMessage(getMessageReductionScaleType(prevProperty, prevPropertyScale, newPropertyScale));
                }
                prevProperty.addChangedProperty(XmlModelClassProperty.PRECISION_TAG, prevProperty.getScale());
                prevProperty.setScale(newProperty.getScale());
            }
        }

    }

    private void checkExternalLink(XmlModelClassProperty prevProperty,
                                   XmlModelClassProperty newProperty,
                                   ModelParameters modelParameters) {
        if (!Objects.equals(prevProperty.isExternalLink(), newProperty.isExternalLink())) {
            if (Boolean.TRUE.equals(newProperty.isExternalLink())) {

                String originalType;
                if (newProperty.getCollectionType() == null) {
                    originalType = transformReferenceClassToOriginClass(newProperty.getType());
                } else {
// скорее всего сюда never попадем, т.к. isExternalLink не проставляется для collection свойств
// it is set for the property inside the class representing a collection of references
                    originalType = transformReferenceTypeToClassType(newProperty.getModelClass().getName(), newProperty.getType());
                }

                if (prevProperty.propertyChanged(XmlModelClassProperty.TYPE_TAG)) {
                    String oldType = prevProperty.getOldValueChangedProperty(XmlModelClassProperty.TYPE_TAG);
                    if (Objects.nonNull(oldType) && !oldType.equals(originalType)) {
                        prevProperty.addChangedProperty(XmlModelClassProperty.ORIGINAL_TYPE_TAG, oldType);
                    }
                }
                prevProperty.addChangedProperty(XmlModelClassProperty.EXTERNAL_LINK_TAG, prevProperty.isExternalLink());
                modelParameters.addBackwardCompatibilityViolationMessage(getMessageChangePropertyToReferenceRoot(prevProperty));

            } else {

                prevProperty.addChangedProperty(XmlModelClassProperty.EXTERNAL_LINK_TAG, prevProperty.isExternalLink());
                modelParameters.addBackwardCompatibilityViolationMessage(getMessageChangeReferenceRootToProperty(prevProperty));
            }
        }
    }

    private void checkMandatoryChange(XmlModelClassProperty prevProperty,
                                      XmlModelClassProperty newProperty,
                                      AtomicBoolean propertyUpdated) {

        if (Boolean.TRUE.equals(newProperty.isExternalLink()) && Objects.isNull(newProperty.getCollectionType())) {
            if (!disableCompatibilityCheck) {
                if (newProperty.getDefaultValue() != null) {
                    throw new ExternalReferenceDefaultValueException(newProperty.getModelClass().getName(), newProperty.getName());
                }
                XmlEmbeddedList newPropertyEmbeddedList = newProperty.getModelClass().getEmbeddedPropertyList().stream()
                        .filter(xmlEmbeddedList -> xmlEmbeddedList.getName().equals(newProperty.getName()))
                        .findFirst()
                        .orElseThrow(() -> new ReferenceStorageException(newProperty));
                if (!prevProperty.propertyChanged(XmlModelClassProperty.EMBEDDED_TAG)) {
                    XmlEmbeddedList prevPropertyEmbeddedList = prevProperty.getModelClass().getEmbeddedPropertyList().stream()
                            .filter(xmlEmbeddedList -> xmlEmbeddedList.getName().equals(prevProperty.getName()))
                            .findFirst()
                            .orElseThrow(() -> new ReferenceStorageException(prevProperty));
                    prevPropertyEmbeddedList.setMandatory(newPropertyEmbeddedList.isMandatory());
                }
            }
        } else if (newProperty.isMandatory() != prevProperty.isMandatory()) {
            propertyUpdated.set(true);
            prevProperty.addChangedProperty(XmlModelClassProperty.MANDATORY_TAG, prevProperty.isMandatory());
            if (newProperty.isMandatory()) {
                if (ModelHelper.isPrimitiveType(newProperty.getType()) || newProperty.isEnum()) {
                    if (newProperty.getDefaultValue() == null) {
                        throw new SimpleMandatoryFieldMustBeFilledException(newProperty);
                    } else {
                        checkAvailableDefaultValues(newProperty);
                        LOGGER.warning("It is necessary to ensure the absence of dependency on the logic of work" +
                        "from control of obligation at the DB level");
                    }
                } else {
                    if (newProperty.getDefaultValue() != null) {
                        throw new ReferenceDefaultValueException(newProperty.getModelClass().getName(), newProperty.getName());
                    }
                }
            }

            prevProperty.setMandatory(newProperty.isMandatory());
            prevProperty.setDefaultValue(newProperty.getDefaultValue());
        }
    }

    private void checkUnicode(XmlModelClassProperty prevProperty,
                              XmlModelClassProperty newProperty,
                              AtomicBoolean propertyUpdated) {

        //conversion to/from UnicodeString for intermediate release implemented in
        //method for converting types - checkTypeWhenDisableCompatibilityCheck
        if (!disableCompatibilityCheck) {
            if (newProperty.isUnicode() != prevProperty.isUnicode()) {
                //here we check that we convert from unicodestring to string
                // and that the length of the new type should already be twice as large
                if (prevProperty.isUnicode()
                    && (newProperty.getLength() < prevProperty.getLength() * 2)) {
                    throw new UnicodeStringToStringModificationException(newProperty);
                }
                propertyUpdated.set(true);
                prevProperty.addChangedProperty(XmlModelClassProperty.UNICODE_TAG, prevProperty.isUnicode());
                prevProperty.setUnicode(newProperty.isUnicode());
            }
        }
    }

    private static void checkDefaultValue(XmlModelClassProperty prevProperty,
                                          XmlModelClassProperty newProperty,
                                          AtomicBoolean propertyUpdated) {

        if (!Objects.equals(newProperty.getDefaultValue(), prevProperty.getDefaultValue())) {
            propertyUpdated.set(true);
            prevProperty.addChangedProperty(XmlModelClassProperty.DEFAULT_VALUE_TAG, prevProperty.getDefaultValue());
            prevProperty.setDefaultValue(newProperty.getDefaultValue());
        }
    }

    private static void checkChangeable(XmlModelClassProperty prevProperty,
                                        XmlModelClassProperty newProperty,
                                        AtomicBoolean propertyUpdated) {

        if (!Objects.equals(newProperty.getChangeable(), prevProperty.getChangeable())) {
            propertyUpdated.set(true);
            prevProperty.addChangedProperty(XmlModelClassProperty.CHANGEABLE_TAG, prevProperty.getChangeable());
            prevProperty.setChangeable(newProperty.getChangeable());
        }
    }

    private static void checkLabel(XmlModelClassProperty prevProperty,
                                   XmlModelClassProperty newProperty,
                                   AtomicBoolean propertyUpdated) {

        if (!Objects.equals(newProperty.getDefaultValue(), prevProperty.getDefaultValue())) {
            prevProperty.addChangedProperty(XmlModelClassProperty.DEFAULT_VALUE_TAG, prevProperty.getDefaultValue());
            prevProperty.setDefaultValue(newProperty.getDefaultValue());
        }

        if (!Objects.equals(newProperty.getLabel(), prevProperty.getLabel())) {
// In the embedded properties on the physics tab, the label in the embedded class properties changes
            if (!newProperty.isEmbedded()) {
                propertyUpdated.set(true);
                prevProperty.addChangedProperty(XmlModelClassProperty.LABEL_TAG, prevProperty.getLabel());
            }
            prevProperty.setLabel(newProperty.getLabel());
        }
    }

    private void checkDeprecated(XmlModelClassProperty prevProperty,
                                 XmlModelClassProperty newProperty,
                                 AtomicBoolean propertyUpdated,
                                 ModelParameters modelDiff) {
// in ClassDiff.deleteClasses for such properties of such classes we put @Deprecated obligatorily
        if (prevProperty.getModelClass().isDeprecated() || newProperty.getModelClass().isDeprecated()) {
            return;
        }
        if (!Objects.equals(prevProperty.isDeprecated(), newProperty.isDeprecated())) {
            if (newProperty.isDeprecated()) {
                if (!disableCompatibilityCheck) {
// can also add conditions for properties with mappedBy != null,
// either that or for other properties where it doesn't make sense to mark them as deprecated
                    if (prevProperty.isParent()) {
                        throw new ParentPropertyCanNotBeDeprecatedException(prevProperty);
                    }
                }
            }
            prevProperty.addChangedProperty(XmlModelClassProperty.DEPRECATED_TAG, prevProperty.isDeprecated());
            prevProperty.setDeprecated(newProperty.isDeprecated());
            if (prevProperty.isDeprecated()) {
// preparing newProperty for deletion of the foreign key. This state will be transferred to prevProperty in checkFKGenerated
                prepareDropForeignKey(newProperty);
                if (prevProperty.getMappedBy() == null) {
                // TODO it is unclear why only if newProperty.isDeprecated(), and if not deprecated, then
                // you do not need to add information to diff already?
                    modelDiff.addDiffObject(ElementState.DEPRECATED, prevProperty);
                }
            } else {
                if (prevProperty.getMappedBy() == null) {
                    //TODO not clear why only if
                    propertyUpdated.set(true);
                }
            }
        }
    }

    private static void checkOriginalType(XmlModelClassProperty prevProperty,
                                   XmlModelClassProperty newProperty,
                                   AtomicBoolean propertyUpdated) {
        if (!prevProperty.propertyChanged(XmlModelClassProperty.ORIGINAL_TYPE_TAG) && !Objects.equals(prevProperty.getOriginalType(), newProperty.getOriginalType())) {
            propertyUpdated.set(true);
            prevProperty.addChangedProperty(XmlModelClassProperty.ORIGINAL_TYPE_TAG, prevProperty.getOriginalType());
            prevProperty.setOriginalType(newProperty.getOriginalType());
        }
    }

    private static void checkExternalSoftReference(XmlModelClassProperty prevProperty,
                                            XmlModelClassProperty newProperty,
                                            AtomicBoolean propertyUpdated) {
        if (!Objects.equals(prevProperty.isExternalSoftReference(), newProperty.isExternalSoftReference())) {
            propertyUpdated.set(true);
            prevProperty.addChangedProperty(XmlModelClassProperty.EXTERNAL_SOFT_REFERENCE_TAG, prevProperty.isExternalSoftReference());
            prevProperty.setExternalSoftReference(newProperty.isExternalSoftReference());
        }
    }

    private static void checkMask(XmlModelClassProperty prevProperty,
                           XmlModelClassProperty newProperty) {
        if (!Objects.equals(prevProperty.getMask(), newProperty.getMask())) {
            prevProperty.addChangedProperty(XmlModelClassProperty.MASK_TAG, prevProperty.getModelClass());
            prevProperty.setMask(newProperty.getMask());
        }
    }

    private static void checkIntegrityCheck(XmlModelClassProperty prevProperty,
                                     XmlModelClassProperty newProperty) {
        if (!Objects.equals(prevProperty.isIntegrityCheck(), newProperty.isIntegrityCheck())) {
            prevProperty.addChangedProperty(XmlModelClassProperty.INTEGRITY_CHECK_TAG, prevProperty.isIntegrityCheck());
            prevProperty.setIntegrityCheck(newProperty.isIntegrityCheck());
        }
    }

    private static void checkIndexed(XmlModelClassProperty prevProperty,
                              XmlModelClassProperty newProperty) {
        if (!Objects.equals(prevProperty.isIndexed(), newProperty.isIndexed())) {
            prevProperty.addChangedProperty(XmlModelClassProperty.INDEX_TAG, prevProperty.getIndexed());
            prevProperty.setIndex(newProperty.getIndexed());
        }
    }

    private static void checkUnique(XmlModelClassProperty prevProperty,
                              XmlModelClassProperty newProperty) {
        if (!Objects.equals(prevProperty.isUnique(), newProperty.isUnique())) {
            prevProperty.addChangedProperty(XmlModelClassProperty.UNIQUE_TAG, prevProperty.getUnique());
            prevProperty.setUnique(newProperty.getUnique());
        }
    }

    private void checkFkGenerated(XmlModelClassProperty prevProperty, XmlModelClassProperty newProperty, AtomicBoolean propertyUpdated) {
        if (getForeignKeyCondition(prevProperty)) {
            if (!Objects.equals(prevProperty.isFkGenerated(), newProperty.isFkGenerated())) {
                propertyUpdated.set(true);
                prevProperty.addChangedProperty(XmlModelClassProperty.FK_GENERATED_TAG, prevProperty.isFkGenerated());
                prevProperty.setFkGenerated(newProperty.isFkGenerated());
                prevProperty.addChangedProperty(XmlModelClassProperty.FK_NAME_TAG, prevProperty.getFkName());
                prevProperty.setFkName(newProperty.getFkName());
            }
        }
    }

    private void checkFkDeleteCascade(XmlModelClassProperty prevProperty, XmlModelClassProperty newProperty, AtomicBoolean propertyUpdated) {
        if (getFkDeleteCascadeCondition(prevProperty)) {
            if (!Objects.equals(prevProperty.isFkDeleteCascade(), newProperty.isFkDeleteCascade())) {
                propertyUpdated.set(true);
                prevProperty.addChangedProperty(XmlModelClassProperty.FK_DELETE_CASCADE_TAG, prevProperty.isFkDeleteCascade());
                prevProperty.setFkDeleteCascade(newProperty.isFkDeleteCascade());
            }
        }
    }

    private static void checkEmbedded(XmlModelClassProperty prevProperty,
                                      XmlModelClassProperty newProperty,
                                      AtomicBoolean propertyUpdated) {
        if (!Objects.equals(prevProperty.isEmbedded(), newProperty.isEmbedded())) {
            prevProperty.addChangedProperty(XmlModelClassProperty.EMBEDDED_TAG, prevProperty.isEmbedded());
            prevProperty.setEmbedded(newProperty.isEmbedded());
            propertyUpdated.set(true);

            if (!prevProperty.isEmbedded()) {
                //So, earlier it was embedded, then we remove the element from the emebeddedList.
                // We set the flag here (rather than deleting elements on physical property deletion) because,
                // what if the property changes type (historicized embedded property), then at the time of deletion
                // we will not have information that the field was once embedded and we will not delete EmbeddedPropertyList
                prevProperty.getModelClass()
                    .getEmbeddedPropertyMeta(prevProperty.getName())
                    .get()
                    // setting the removed flag immediately, as this case is possible only in develop versions of the model
                    // The setting of deprecated, then deprecatedVersion, and only then the sign of removed
                    // will bring many overheads to reviewing all the list of all classes at each of their stages
                    .setRemoved(true);
            }
        }
    }

    private static void checkColumName(XmlModelClassProperty prevProperty,
                                       XmlModelClassProperty newProperty,
                                       AtomicBoolean propertyUpdate) {
        if (!Objects.equals(prevProperty.getColumnName(), newProperty.getColumnName())) {
            prevProperty.addChangedProperty(XmlModelClassProperty.COLUMN_NAME_TAG, prevProperty.getColumnName());
            prevProperty.setColumnName(newProperty.getColumnName());
            propertyUpdate.set(true);
        }
    }

    private void fillOriginalType(XmlModel baseModel) {
        baseModel.getClassesAsList().forEach(modelClass ->
            modelClass.getPropertiesAsList().stream()
                .filter(XmlModelClassProperty::isExternalLink)
                .filter(it -> Objects.isNull(it.getOriginalType()))
                .forEach(it -> {
                    String type = it.getType();
                    if (it.getCollectionType() == null) {
                        it.setOriginalType(transformReferenceClassToOriginClass(type));
                    } else {
                        it.setOriginalType(transformReferenceTypeToClassType(modelClass.getName(), type));
                    }
                }));
    }

    private void checkHistorical(XmlModelClassProperty prevProperty,
                                 XmlModelClassProperty newProperty,
                                 AtomicBoolean propertyUpdated) {

        if (!Objects.equals(newProperty.isHistorical(), prevProperty.isHistorical())) {
            propertyUpdated.set(true);
            prevProperty.addChangedProperty(XmlModelClassProperty.HISTORICAL_TAG, prevProperty.isHistorical());
            prevProperty.setHistorical(newProperty.isHistorical());
        }
    }

    private void checkComputedExpression(XmlModelClassProperty prevProperty, XmlModelClassProperty newProperty) {
        if (!Objects.equals(newProperty.getComputedExpression(), prevProperty.getComputedExpression())) {
            throw new ComputedExpressionChangeException(prevProperty, newProperty);
        }
    }

    private void checkCollectionType(XmlModelClassProperty prevProperty,
                                     XmlModelClassProperty newProperty,
                                     AtomicBoolean propertyUpdated,
                                     ModelParameters modelParameters) {
        CollectionType prevCollectionType = prevProperty.getCollectionType();
        if (!Objects.equals(newProperty.getCollectionType(), prevCollectionType)) {
            if (disableCompatibilityCheck) {
                modelParameters.addBackwardCompatibilityViolationMessage(getMessageChangeCollectionType(prevProperty, !Objects.isNull(newProperty.getCollectionType())));
                prevProperty.setCollectionType(newProperty.getCollectionType());
                prevProperty.setCollectionTableName(newProperty.getCollectionTableName());
                prevProperty.setCollectionPkIndexName(newProperty.getCollectionPkIndexName());
                prevProperty.setKeyColumnName(newProperty.getKeyColumnName());
                prevProperty.addChangedProperty(XmlModelClassProperty.COLLECTION_TAG, prevCollectionType);
                propertyUpdated.set(true);
            } else {
                throw new CollectionTypeChangeException(prevProperty);
            }
        }
    }

    private void checkAvailableDefaultValues(XmlModelClassProperty property) {
        TypeInfo typeInfo = property.getTypeInfo();
        switch (typeInfo.getJavaName()) {
            case "String":
            case "Character":
            case "Boolean":
            case "BigDecimal":
            case "Integer":
            case "Short":
            case "Long":
            case "Byte":
            case "Float":
            case "Double":
            case "Date":
            case "LocalDate":
            case "LocalDateTime":
            case "OffsetDateTime":
            case "byte[]":
                break;
            default:
                throw new UnsupportedDefaultValueInDiffException(property);
        }
    }
}
