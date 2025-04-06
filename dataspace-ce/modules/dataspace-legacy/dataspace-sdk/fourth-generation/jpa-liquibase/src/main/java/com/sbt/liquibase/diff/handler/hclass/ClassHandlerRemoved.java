package com.sbt.liquibase.diff.handler.hclass;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.liquibase.diff.handler.HandlerCommonMethods;
import com.sbt.mg.ElementState;
import com.sbt.mg.data.model.XmlModelClass;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;

public class ClassHandlerRemoved extends ClassHandlerBase {

    public ClassHandlerRemoved(MutableInt indexCollection, MutableInt indexIndex) {
        super(indexCollection, indexIndex);
    }

    @Override
    public void handle(StringBuilder changesSB, MutableLong index, XmlModelClass modelClass, ModelParameters modelDiff, PluginParameters pluginParameters) {
        if (!modelDiff.containsObjectInDiff(ElementState.REMOVED, modelClass)) {
            return;
        }
        if (modelClass.isEmbeddable() || modelClass.isAbstract()) {
            return;
        }
        new HandlerCommonMethods().dropTable(changesSB, index, modelClass.getTableName(), "", modelDiff);
    }


}