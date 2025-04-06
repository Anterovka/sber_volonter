package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlImport;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.exception.GeneralSdkException;
import com.sbt.model.exception.parent.CheckModelException;

import java.util.Set;

public class LessImportDefinedException extends CheckModelException {
    public LessImportDefinedException(Set<XmlImport> difference) {
        super(GeneralSdkException.join("Noticed reduction of imported resources", '(', XmlModel.IMPORT_TAG, ')'),
            GeneralSdkException.join("It is necessary to return back the use of", difference));
    }
}
