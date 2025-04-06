package com.sbt.mg.data.model.interfaces;

public interface XmlProperty<T extends XmlObject> {
    T getParent();

    String getName();

    String getType();


}
