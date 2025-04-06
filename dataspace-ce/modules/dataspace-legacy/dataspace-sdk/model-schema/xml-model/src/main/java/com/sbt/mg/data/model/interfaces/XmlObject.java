package com.sbt.mg.data.model.interfaces;

import com.sbt.mg.data.model.XmlModel;

import java.util.List;

public interface XmlObject {
    XmlModel getModel();

    String getName();

    String getLabel();

    String getDescription();

    <T extends XmlObject> List<XmlProperty<T>> getPropertiesAsList();
}
