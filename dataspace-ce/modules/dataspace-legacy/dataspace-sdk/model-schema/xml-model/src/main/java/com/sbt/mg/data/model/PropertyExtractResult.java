package com.sbt.mg.data.model;

public class PropertyExtractResult {

    private boolean isProperty;
    private boolean isReference;
    private boolean isSystem;
    private XmlModelClassProperty property;
    private XmlModelClassReference reference;

    public PropertyExtractResult(XmlModelClassProperty property) {
        this.isProperty = true;
        this.property = property;
    }

    public PropertyExtractResult(XmlModelClassReference reference) {
        this.isReference = true;
        this.reference = reference;
    }

    private PropertyExtractResult(boolean isSystem) {
        this.isSystem = isSystem;
    }

    public static PropertyExtractResult systemProperty() {
        return new PropertyExtractResult(true);
    }

    public boolean isProperty() {
        return isProperty;
    }

    public boolean isReference() {
        return isReference;
    }

    public XmlModelClassProperty getProperty() {
        return property;
    }

    public XmlModelClassReference getReference() {
        return reference;
    }

    public boolean isSystem() {
        return isSystem;
    }
}
