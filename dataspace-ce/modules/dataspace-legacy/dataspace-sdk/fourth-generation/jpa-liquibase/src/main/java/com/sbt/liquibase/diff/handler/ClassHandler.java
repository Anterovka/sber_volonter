package com.sbt.liquibase.diff.handler;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.mg.data.model.XmlModelClass;
import org.apache.commons.lang3.mutable.MutableLong;

public interface ClassHandler {
    void handle(StringBuilder changesSB, MutableLong index, XmlModelClass modelClass, ModelParameters modelDiff, PluginParameters pluginParameters);
}
