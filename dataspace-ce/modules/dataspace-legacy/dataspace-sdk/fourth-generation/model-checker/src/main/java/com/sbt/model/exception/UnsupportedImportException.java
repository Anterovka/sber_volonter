package com.sbt.model.exception;

import com.sbt.dataspace.pdm.ModelGenerate;
import com.sbt.mg.data.model.XmlImport;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Collection;
import java.util.stream.Collectors;

public class UnsupportedImportException extends CheckXmlModelException {
    public UnsupportedImportException(XmlImport xmlImport, Collection<ModelGenerate> modelGenerates) {
        super(join("Not supported by the generator import. Error in importing with the name", xmlImport.getType()),
            join("Supported:", modelGenerates.stream().map(ModelGenerate::getProjectName).collect(Collectors.joining(", "))));
    }
}
