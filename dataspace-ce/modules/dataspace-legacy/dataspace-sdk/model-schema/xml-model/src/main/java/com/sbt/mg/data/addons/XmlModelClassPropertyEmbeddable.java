package com.sbt.mg.data.addons;

import com.sbt.mg.data.model.XmlEmbeddedList;
import com.sbt.mg.data.model.XmlEmbeddedProperty;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.XmlModelClassReference;

import java.util.Iterator;
import java.util.Optional;

/** Class representing an embedded property.
* We start from the assumption that nested embeddable entities cannot exist. */
public class XmlModelClassPropertyEmbeddable extends XmlModelClassProperty {
    /** embedded property of a class (which is an embeddable class) */
    XmlModelClassProperty parentProperty;
    /** Specific embedded property */
    XmlModelClassProperty embeddableProperty;
    /** Reference to property inside XmlEmbeddedList */

    public XmlModelClassPropertyEmbeddable(XmlModelClassProperty parentProperty, XmlModelClassProperty embeddableProperty) {
        this.parentProperty = parentProperty;
        this.embeddableProperty = embeddableProperty;
    }

    public XmlModelClassProperty getParentProperty() {
        return parentProperty;
    }

    public XmlModelClassProperty getEmbeddableProperty() {
        return embeddableProperty;
    }

    @Override
    public String getName() {
        return parentProperty.getName() + "_" + embeddableProperty.getName();
    }

    public String getName(String splitter) {
        return parentProperty.getName() + splitter + embeddableProperty.getName();
    }

    /** Returns the type of embeddable property */
    @Override
    public String getType() {
        return embeddableProperty.getType();
    }

    /** Returns the model class that has an embedded property inside */
    @Override
    public XmlModelClass getModelClass() {
        return parentProperty.getModelClass();
    }

    /** Returns the name of the physical column for an embeddable property from the model class, if such information is available in the class */
    @Override
    public String getColumnName() {
        Optional<XmlEmbeddedProperty> embPropeMeta = parentProperty.getModelClass().getEmbeddedPropertyList().stream()
                .filter(it -> it.getName().equals(parentProperty.getName()))
                .flatMap(it -> it.getEmbeddedPropertyList().stream())
                .filter(it -> it.getName().equals(embeddableProperty.getName()))
                .findAny();
        return embPropeMeta.isPresent() ? embPropeMeta.get().getColumnName() : null;
    }

    /** Sets the physical column name to the model class for this embeddable property */
    @Override
    public XmlModelClassPropertyEmbeddable setColumnName(String columnName) {
        Optional<XmlEmbeddedList> embPropListMeta = parentProperty.getModelClass().getEmbeddedPropertyMeta(parentProperty.getName());

        XmlModelClassReference reference = parentProperty.getModelClass().getReferenceNullable(parentProperty.getName());
        XmlEmbeddedList xmlEmbeddedList = embPropListMeta.isPresent()
                ? embPropListMeta.get()
                : new XmlEmbeddedList(
                parentProperty.getName(),
                reference != null,
                reference != null && reference.isMandatory()
        );
        XmlEmbeddedProperty xmlEmbeddedProperty = new XmlEmbeddedProperty(embeddableProperty.getName(), columnName);
        Optional<XmlEmbeddedProperty> embPropMeta = xmlEmbeddedList.getEmbeddedPropertyList().stream()
                .filter(it -> it.getName().equals(embeddableProperty.getName()))
                .findAny();
        if (embPropMeta.isPresent()) {
            Iterator<XmlEmbeddedProperty> iterator = xmlEmbeddedList.getEmbeddedPropertyList().iterator();
            while (iterator.hasNext()) {
                XmlEmbeddedProperty next = iterator.next();
                if (next.getName().equals(embeddableProperty.getName())) {
                    iterator.remove();
                }
            }
        }

        xmlEmbeddedList.addEmbeddedProperty(xmlEmbeddedProperty);
        if (!embPropListMeta.isPresent()) {
            parentProperty.getModelClass().addEmbeddedProperty(xmlEmbeddedList);
        }
        xmlEmbeddedProperty.setProperty(embeddableProperty);
        return this;
    }
}
