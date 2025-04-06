package com.sbt.model.utils;

import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PdmModel;
import com.sbt.dataspace.pdm.StreamModel;
import com.sbt.model.phase.CheckModel;

public class ModelUtils {

    private ModelUtils() {
    }

    public static PdmModel makePdmModel(StreamModel streamModel) {
        ModelParameters modelParameters = new CheckModel().execute(streamModel);
        modelParameters.getPdmModel().setModel(modelParameters.getModel());
        return modelParameters.getPdmModel();
    }

}
