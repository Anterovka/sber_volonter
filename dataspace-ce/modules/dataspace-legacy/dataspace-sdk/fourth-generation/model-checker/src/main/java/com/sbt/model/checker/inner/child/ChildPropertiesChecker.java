package com.sbt.model.checker.inner.child;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;

import java.util.Set;

public interface ChildPropertiesChecker {
    void check(XmlModelClass xmlModelClass, Set<XmlModelClassProperty> properties);
}
