package com.sbt.dictionary.check;

import com.sbt.dictionary.DictionaryCheckParams;
import com.sbt.dictionary.exceptions.DictionaryParentPropertyException;
import com.sbt.dictionary.exceptions.DictionaryReferenceException;
import com.sbt.dictionary.exceptions.EmbeddedDictionaryClassException;
import com.sbt.dictionary.exceptions.ExplicitFalseDictionaryOnHeirException;
import com.sbt.dictionary.exceptions.NotSupportedCollectionPropertiesException;
import com.sbt.dictionary.exceptions.NotSupportedOffsetPropertiesException;
import com.sbt.dictionary.exceptions.ReferenceException;
import com.sbt.dictionary.exceptions.RootClassIsNotDictionaryException;
import com.sbt.dictionary.exceptions.WrongIdCategoryException;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.parameters.enums.Changeable;
import com.sbt.parameters.enums.IdCategory;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sbt.mg.ModelHelper.isPrimitiveType;

public class DictionaryCheckModelLogic {

    private final XmlModel model;

    public DictionaryCheckModelLogic(XmlModel model) {
        this.model = model;
    }

    public static DictionaryCheckModelLogic of(XmlModel model) {
        return new DictionaryCheckModelLogic(model);
    }

    public void check(DictionaryCheckParams params) {
        //checkIsDictionaryOnRoot d. b. be first. It preloads the model.
        checkIsDictionaryOnRoot();
        checkReferenceOnDictionary(params);
        checkIdIsManual();
        checkPrimitiveCollections();
        checkNoOffsetDateTimeType();
//        checkNoParentInProperties();
        checkNoEmbeddedDictionaryClasses();
        checkNoReferencesInProperties();
    }

    private void checkIsDictionaryOnRoot() {
        Set<XmlModelClass> baseDictionaryClasses = this.model.getClassesAsList().stream()
                .filter(XmlModelClass::isDictionary)
                .map(this::getBaseClass).collect(Collectors.toSet());

        baseDictionaryClasses.forEach(
                baseClass -> {
                    if (!baseClass.isDictionary()) {
                        throw new RootClassIsNotDictionaryException(baseClass.getName());
                    }
                }
        );

        checkHeirsDoesNotHaveExplicitDictionaryFalseFlag(baseDictionaryClasses);

        baseDictionaryClasses.forEach(baseClass ->
                ModelHelper.getAllChildClasses(baseClass).forEach(aClass -> aClass.setDictionary(Boolean.TRUE)));

    }

    private void checkReferenceOnDictionary(DictionaryCheckParams params) {
        List<String> errors = model.getClassesAsList().stream()
                .flatMap(modelClass -> modelClass.getReferencesAsList().stream())
                .filter(reference -> {
                    XmlModelClass typeClass = model.getClassNullable(reference.getType());
                    if (typeClass != null) {
                        return typeClass.isDictionary();
                    }
                    return false;
                })
            .map(property ->
                String.format("A reference to the directory was found as an external aggregate (reference instead of property). " +
                        "The field %s of class %s. You should refer to the reference through the property.",
                    property.getName(),
                    property.getModelClass().getName())
            ).collect(Collectors.toList());

        if (params.isExceptionOnDictionaryReference()) {
            throw new DictionaryReferenceException(errors);
        }
        params.getWarnings().addAll(errors);
    }

    private void checkHeirsDoesNotHaveExplicitDictionaryFalseFlag(Set<XmlModelClass> baseDictionaryClasses) {

        Set<String> falseDictHeirs = baseDictionaryClasses.stream()
                .flatMap(baseClass -> ModelHelper.getAllChildClasses(baseClass).stream())
                .filter(heirClass -> Boolean.FALSE.equals(heirClass.getIsDictionary()))
                .map(XmlModelClass::getName)
                .collect(Collectors.toSet());

        if (!falseDictHeirs.isEmpty()) {
            throw new ExplicitFalseDictionaryOnHeirException(falseDictHeirs);
        }
    }

    private XmlModelClass getBaseClass(XmlModelClass modelClass) {
        if (modelClass.getExtendedClassName() != null) {
            return getBaseClass(this.model.getClass(modelClass.getExtendedClassName()));
        }
        return modelClass;
    }

    private void checkIdIsManual() {
        this.model.getClassesAsList().stream()
                .filter(XmlModelClass::isDictionary)
                .forEach(dictionaryClass -> {
                    if (dictionaryClass.getId() != null &&
                            IdCategory.MANUAL != dictionaryClass.getId().getIdCategory()) {
                        throw new WrongIdCategoryException(dictionaryClass.getName());
                    }
                });
    }

    private void checkPrimitiveCollections() {
        List<XmlModelClassProperty> collectionReferenceProperties = this.model.getClassesAsList().stream()
                .filter(XmlModelClass::isDictionary)
                .flatMap(modelClass -> modelClass.getPropertiesAsList().stream()
                        .filter(property -> Objects.nonNull(property.getCollectionType())))
                .filter(property -> !(isPrimitiveType(property.getType()) ||isPropertyEnum(property)) &&
                        !this.model.getClass(property.getType()).isDictionary())
                .filter(property -> Changeable.SYSTEM != property.getChangeable())
                .collect(Collectors.toList());
        if (!collectionReferenceProperties.isEmpty()) {
            throw new NotSupportedCollectionPropertiesException(collectionReferenceProperties);
        }
    }

    private boolean isPropertyEnum(XmlModelClassProperty property) {
        return model.getEnums().stream().anyMatch(enm -> Objects.equals(
                enm.getName().toLowerCase(Locale.ENGLISH),
                property.getType().toLowerCase(Locale.ENGLISH)
        ));
    }

    private void checkNoOffsetDateTimeType() {
        this.model.getClassesAsList().stream()
                .filter(XmlModelClass::isDictionary)
                .forEach(modelClass -> {
                    List<XmlModelClassProperty> offsetProperties = modelClass.getPropertiesAsList().stream()
                            .filter(property -> OffsetDateTime.class.getSimpleName().equals(property.getType()))
                            .collect(Collectors.toList());
                    if (!offsetProperties.isEmpty()) {
                        throw new NotSupportedOffsetPropertiesException(offsetProperties);
                    }
                });
    }

    private void checkNoParentInProperties() {
        List<XmlModelClassProperty> propertiesWithParent = model.getClassesAsList().stream()
                .filter(XmlModelClass::isDictionary)
                .flatMap(xmlModelClass -> xmlModelClass.getPropertiesAsList().stream())
                .filter(XmlModelClassProperty::isParent)
                .collect(Collectors.toList());

        if (!propertiesWithParent.isEmpty()) {
            throw new DictionaryParentPropertyException(propertiesWithParent);
        }
    }

    private void checkNoReferencesInProperties() {
        Collection<XmlModelClass> dictionaryWithReferences = model.getClassesAsList().stream()
                .filter(XmlModelClass::isDictionary)
                .filter(modelClass -> !modelClass.getReferencesAsList().isEmpty())
                .collect(Collectors.toList());

        if (!dictionaryWithReferences.isEmpty()) {
            throw new ReferenceException(dictionaryWithReferences);
        }
    }

    private void checkNoEmbeddedDictionaryClasses() {
        Set<String> embeddedDictionaryClassNames = model.getClassesAsList().stream()
                .filter(it -> it.isDictionary() && it.isEmbeddable())
                .map(XmlModelClass::getName)
                .collect(Collectors.toSet());

        if (!embeddedDictionaryClassNames.isEmpty()) {
            throw new EmbeddedDictionaryClassException(embeddedDictionaryClassNames);
        }
    }
}
