package com.sbt.model.checker.inner.root;

import com.google.common.collect.Sets;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelInterface;
import com.sbt.mg.exception.checkmodel.ClassAndInterfaceNameDuplicationException;
import com.sbt.mg.exception.checkmodel.InterfaceOnEmbeddableClassException;
import com.sbt.model.exception.CircleClassExtendsException;
import com.sbt.model.exception.ExtendFinalClassException;
import com.sbt.model.exception.PartitionKeyRegexAttributeException;
import com.sbt.model.exception.PartitionKeyRegexNotValidException;
import com.sbt.model.exception.TableNameShouldNotSettingException;
import com.sbt.model.exception.TableNameShouldNotSettingOnCollectionException;
import com.sbt.model.exception.UndefinedExtendedClassException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ClassChecker implements RootChecker {

    private final XmlModel model;

    public ClassChecker(XmlModel model) {
        this.model = model;
    }

    @Override
    public void check() {
        // We check that the table names in the model are not filled, as we generate them.
        checkTableNameIsAbsent();
        // Checking that interface and class names do not overlap
        // intersection of names of other entities (enum, TypeDef, etc.) is checked further)
        checkInterfaceAndClassNameClash();
        // Checking that embeddable classes do not have interfaces
        checkNoInterfaceOnEmbeddedClass();
        // Checks that all extensible classes are present in the model
        allExtendedClassExistsInModel();
        // Checking that the inheritance chain does not form a cycle
        checkCircleClassExtends();
        // Check if the partition-key-regex matches the regular expression
        checkPartitionKeyRegex();
        // Checking that we are not inheriting final classes
        checkExtendFinalClasses();
    }


    /**
    * Checks that no table names are set in the model (they are calculated automatically)
     */
    private void checkTableNameIsAbsent() {
        model.getClassesAsList().stream()
                .filter(modelClass -> !modelClass.isServiceClass())
                .forEach(modelClass -> {
                    if (modelClass.getTableName() != null) {
                        throw new TableNameShouldNotSettingException(modelClass);
                    }
                    modelClass.getPropertiesAsList().forEach(property -> {
                        if (property.getCollectionTableName() != null) {
                            throw new TableNameShouldNotSettingOnCollectionException(modelClass, property);
                        }
                    });
                });
    }

    /**
     * Checking that interface and class names do not overlap
     */
    private void checkInterfaceAndClassNameClash() {
        Set<String> classNames = model.getClassesAsList().stream().map(XmlModelClass::getName).collect(Collectors.toSet());
        Set<String> ifaceNames = model.getInterfacesAsList().stream().map(XmlModelInterface::getName).collect(Collectors.toSet());
        Sets.SetView<String> intersection =
                Sets.intersection(ifaceNames, classNames);
        if (!intersection.isEmpty()) {
            throw new ClassAndInterfaceNameDuplicationException(intersection);
        }
    }

    /**
     * Checking that embeddable classes have no interfaces
     */
    private void checkNoInterfaceOnEmbeddedClass() {
        List<XmlModelClass> embeddableWithInterfaces = model.getClassesAsList().stream().filter(clazz -> clazz.isEmbeddable() && !clazz.getImplementedInterfacesAsSet().isEmpty())
                .collect(Collectors.toList());
        if (!embeddableWithInterfaces.isEmpty()) {
            throw new InterfaceOnEmbeddableClassException(embeddableWithInterfaces);
        }
    }

    /**
     * Ensures that all expandable classes are present in the model
     */
    private void allExtendedClassExistsInModel() {
        List<XmlModelClass> undefinedExtends = model.getClassesAsList().stream()
                .filter(modelClass -> modelClass.getExtendedClassName() != null && !modelClass.isDeprecated())
                .filter(modelClass -> {
                    XmlModelClass extClass = model.getClassNullable(modelClass.getExtendedClassName());
                    return extClass == null || extClass.isDeprecated();
                })
                .collect(Collectors.toList());

        if (!undefinedExtends.isEmpty()) {
            throw new UndefinedExtendedClassException(undefinedExtends);
        }
    }

    /**
     * Check that the inheritance chain of classes does not form a cycle
     */
    private void checkCircleClassExtends() {
        model.getClassesAsList().forEach(modelClass ->
                checkCircleExtends(modelClass, new HashSet<>()));
    }

    private void checkCircleExtends(XmlModelClass modelClass, Set<XmlModelClass> modelClasses) {
        if (!modelClasses.add(modelClass)) {
            throw new CircleClassExtendsException(modelClasses);
        }

        if (modelClass.getExtendedClassName() != null) {
            checkCircleExtends(model.getClass(modelClass.getExtendedClassName()), modelClasses);
        }
    }

    private void checkPartitionKeyRegex() {

        model.getClassesAsList().forEach(xmlModelClass -> {

            if (Objects.isNull(xmlModelClass.getPartitionKeyRegex())) {
                return;
            }

            if (Boolean.TRUE.equals(xmlModelClass.isEmbeddable()) || Boolean.TRUE.equals(xmlModelClass.isAbstract())) {
                throw new PartitionKeyRegexAttributeException(xmlModelClass);
            }

            if (xmlModelClass.getPartitionKeyRegexNotNullAsEmpty().isEmpty()) {
                return;
            }

            try {
                Pattern.compile(xmlModelClass.getPartitionKeyRegex());
            } catch (Exception e) {
                throw new PartitionKeyRegexNotValidException(xmlModelClass);
            }

        });

    }

    private void checkExtendFinalClasses() {
        final List<XmlModelClass> wrongClasses = this.model.getClassesAsList().stream()
                .filter(modelClass ->
                        modelClass.getExtendedClass() != null &&
                                modelClass.getExtendedClass().isFinalClass())
                .collect(Collectors.toList());
        if (!wrongClasses.isEmpty()) {
            throw new ExtendFinalClassException(wrongClasses);
        }
    }

}
