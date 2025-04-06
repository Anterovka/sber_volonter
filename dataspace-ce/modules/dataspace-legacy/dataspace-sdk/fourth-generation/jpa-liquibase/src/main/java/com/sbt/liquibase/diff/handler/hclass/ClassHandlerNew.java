package com.sbt.liquibase.diff.handler.hclass;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.mg.data.model.XmlModelClass;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;

public class ClassHandlerNew extends ClassHandlerBase {

    public ClassHandlerNew(MutableLong classIndex, MutableInt indexIndex, MutableInt indexCollection) {
        super(classIndex, indexCollection, indexIndex);
    }

    @Override
    public void handle(StringBuilder changesSB, MutableLong index, XmlModelClass modelClass, ModelParameters modelDiff, PluginParameters pluginParameters) {
        if (modelClass.isAbstract() || modelClass.isEmbeddable()) {
            return;
        }
        createClass(changesSB, index, modelClass, modelDiff, pluginParameters);
    }
}