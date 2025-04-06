package com.sbt.liquibase.diff.handler;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.mg.data.model.XmlEmbeddedList;
import com.sbt.mg.data.model.XmlEmbeddedProperty;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.NoFoundEmbeddedPropertyException;
import org.apache.commons.lang3.mutable.MutableLong;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public interface PropertyHandler {
    void handle(StringBuilder changesSB, MutableLong index, XmlModelClassProperty modelClassProperty,
                XmlModelClass handledClass, ModelParameters modelParameters, PluginParameters pluginParameters);

    default Collection<String> getColumnNamesForReference(XmlModelClassProperty modelClassProperty) {
        if (!modelClassProperty.isExternalLink()) {
            return Collections.emptyList();
        }

        XmlEmbeddedList referenceProperties = modelClassProperty.getModelClass().getEmbeddedPropertyList().stream()
                .filter(xmlEmbeddedList -> Objects.equals(xmlEmbeddedList.getName(), modelClassProperty.getName()))
                .findFirst()
                .orElseThrow(() -> new NoFoundEmbeddedPropertyException(
                        modelClassProperty.getName(),
                        modelClassProperty.getModelClass()));

        return referenceProperties.getEmbeddedPropertyList().stream()
                .map(XmlEmbeddedProperty::getColumnName)
                .collect(Collectors.toSet());
    }

}
