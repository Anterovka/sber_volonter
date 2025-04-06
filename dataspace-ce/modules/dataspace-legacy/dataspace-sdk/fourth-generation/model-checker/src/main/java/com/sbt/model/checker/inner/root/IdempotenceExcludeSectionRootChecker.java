package com.sbt.model.checker.inner.root;

import com.sbt.mg.data.model.Property;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.XmlModelClassReference;
import com.sbt.model.checker.inner.child.ChildPropertiesChecker;
import com.sbt.model.checker.inner.child.InvalidRegexpInMaskTagChecker;
import com.sbt.model.exception.IdempotenceExcludeSectionWrongPropertiesException;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class IdempotenceExcludeSectionRootChecker implements RootChecker {
    private static Set<ChildPropertiesChecker> childPropertiesCheckers = new HashSet<>();

    static {
        childPropertiesCheckers.add(new InvalidRegexpInMaskTagChecker());
    }

    private final XmlModel model;

    public IdempotenceExcludeSectionRootChecker(XmlModel model) {
        this.model = model;
    }

    @Override
    public void check() {
        model.getClassesAsList().forEach(this::checkWrongProperties);
    }

    private void checkWrongProperties(XmlModelClass xmlModelClass) {
        if (Objects.isNull(xmlModelClass.getIdempotenceExclude())) {
            return;
        }
        Set<String> classPropertyNames = xmlModelClass.getPropertiesAsList().stream()
                .map(XmlModelClassProperty::getName)
                .collect(Collectors.toSet());

        classPropertyNames.addAll(
                xmlModelClass.getReferencesAsList().stream()
                        .map(XmlModelClassReference::getName)
                        .collect(Collectors.toSet())
        );

        Set<Property> wrongProperties = xmlModelClass.getIdempotenceExclude().getProperties().stream()
                .filter(property -> !classPropertyNames.contains(property.getName()))
                .collect(Collectors.toSet());

        if (!wrongProperties.isEmpty()) {
            throw new IdempotenceExcludeSectionWrongPropertiesException(xmlModelClass, wrongProperties);
        }
    }
}
