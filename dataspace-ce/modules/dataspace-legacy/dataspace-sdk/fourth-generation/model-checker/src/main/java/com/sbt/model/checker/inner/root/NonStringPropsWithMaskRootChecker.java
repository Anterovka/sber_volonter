package com.sbt.model.checker.inner.root;

import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.model.checker.inner.child.ChildPropertiesChecker;
import com.sbt.model.checker.inner.child.InvalidRegexpInMaskTagChecker;
import com.sbt.model.exception.NonStringPropertiesWithMaskTagException;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class NonStringPropsWithMaskRootChecker implements RootChecker {
    private static Set<ChildPropertiesChecker> childPropertiesCheckers = new HashSet<>();

    static {
        childPropertiesCheckers.add(new InvalidRegexpInMaskTagChecker());
    }

    private final XmlModel model;

    public NonStringPropsWithMaskRootChecker(XmlModel model) {
        this.model = model;
    }

    @Override
    public void check() {
        model.getClassesAsList().forEach(this::checkMaskOnlyOnStringProperties);
    }

    private void checkMaskOnlyOnStringProperties(XmlModelClass xmlModelClass) {
        Set<XmlModelClassProperty> propsWithMask = xmlModelClass
                .getPropertiesAsList()
                .stream()
                .filter(property -> property.getMask() != null)
                .collect(Collectors.toSet());

        if (propsWithMask.isEmpty()) {
            return;
        }

        Set<XmlModelClassProperty> nonStringPropsWithMask = new HashSet<>();

        propsWithMask.forEach(property -> {
            if (!property.getType().equalsIgnoreCase("string")) {
                nonStringPropsWithMask.add(property);
            }
        });

        if (!nonStringPropsWithMask.isEmpty()) {
            throw new NonStringPropertiesWithMaskTagException(xmlModelClass, nonStringPropsWithMask);
        }

        childPropertiesCheckers.forEach(checker -> checker.check(xmlModelClass, propsWithMask));
    }
}
