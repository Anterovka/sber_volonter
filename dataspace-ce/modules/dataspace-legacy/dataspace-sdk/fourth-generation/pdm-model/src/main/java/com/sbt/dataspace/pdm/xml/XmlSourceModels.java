package com.sbt.dataspace.pdm.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;

import java.util.ArrayList;
import java.util.List;

@XmlTagName(XmlSourceModels.SOURCE_MODELS_TAG)
public class XmlSourceModels {

    public static final String SOURCE_MODELS_TAG = "source-models";

    public static final String ROOT_MODEL = "root-model";
    public static final String IMPORT_MODEL = "import-model";
    public static final String STATUS_MODEL = "status-model";


    private XmlRootModel previousRootModel;
    private List<XmlImportModel> previousImportModels = new ArrayList<>();
    private XmlSourceStatusModel previousStatusModel;


    public XmlSourceModels() {
    }

    @JacksonXmlProperty(localName = ROOT_MODEL)
    public XmlRootModel getPreviousRootModel() {
        return previousRootModel;
    }

    public void setPreviousRootModel(XmlRootModel previousRootModel) {
        this.previousRootModel = previousRootModel;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = IMPORT_MODEL)
    public List<XmlImportModel> getPreviousImportModels() {
        return previousImportModels;
    }

    public void setPreviousImportModels(List<XmlImportModel> previousImportModels) {
        this.previousImportModels = previousImportModels;
    }

    public XmlSourceModels addImportModel (XmlImportModel xmlImportModel) {
        previousImportModels.add(xmlImportModel);
        return this;
    }

    @JacksonXmlProperty(localName = STATUS_MODEL)
    public XmlSourceStatusModel getPreviousStatusModel() {
        return previousStatusModel;
    }

    public void setPreviousStatusModel(XmlSourceStatusModel previousStatusModel) {
        this.previousStatusModel = previousStatusModel;
    }
}
