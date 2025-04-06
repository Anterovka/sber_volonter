package com.sbt.dataspace.pdm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.dataspace.pdm.exception.NoModelInPdmException;
import com.sbt.dataspace.pdm.xml.XmlMetaInformation;
import com.sbt.dataspace.pdm.xml.XmlSourceModels;
import com.sbt.mg.Helper;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.history.XmlHistoryVersions;
import jakarta.xml.bind.annotation.XmlRootElement;
import sbp.com.sbt.dataspace.utils.ClassPathUtils;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;

import static com.sbt.mg.Helper.wrap;
import static com.sbt.mg.ModelHelper.XML_MAPPER;

@XmlRootElement(name = "pdm")
public class PdmModel {

    private static final String MODEL_TAG = "model";
    private static final String STATUS_TAG = "status";
    private static final String SYSTEM_LOCKS_TAG = "system-locks";
    private static final String META_INFORMATION_TAG = "meta-information";
    private static final String SOURCE_MODELS_TAG = "source-models";

    private static final XmlMapper IGNORABLE_XML_MAPPER = new XmlMapper();

    static {
        IGNORABLE_XML_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private XmlModel model;
    private XmlMetaInformation metaInformation;
    private XmlHistoryVersions xmlHistoryVersions;
    private XmlSourceModels sourceModels;

    public PdmModel() {

    }

    @JsonCreator
    public PdmModel(@JacksonXmlProperty(localName = MODEL_TAG) XmlModel model,
                    @JacksonXmlProperty(localName = META_INFORMATION_TAG) XmlMetaInformation metaInformation) {
        this.model = model;
        this.metaInformation = metaInformation;
    }

    @JacksonXmlProperty(localName = MODEL_TAG)
    public XmlModel getModel() {
        return model;
    }

    public void setModel(XmlModel model) {
        this.model = model;
    }

    @JacksonXmlProperty(localName = META_INFORMATION_TAG)
    public XmlMetaInformation getMetaInformation() {
        return metaInformation;
    }

    public void setMetaInformation(XmlMetaInformation metaInformation) {
        this.metaInformation = metaInformation;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = XmlHistoryVersions.HISTORY_VERSIONS_TAG)
    public XmlHistoryVersions getXmlHistoryVersions() {
        return xmlHistoryVersions;
    }

    public void setXmlHistoryVersions(XmlHistoryVersions xmlHistoryVersions) {
        this.xmlHistoryVersions = xmlHistoryVersions;
    }

    @JacksonXmlProperty(localName = SOURCE_MODELS_TAG)
    public XmlSourceModels getSourceModels() {
        return sourceModels;
    }

    public void setSourceModels(XmlSourceModels sourceModels) {
        this.sourceModels = sourceModels;
    }

    public static PdmModel readModelFromClassPath() {
        return readModelFromClassPath("/pdm.xml");
    }

    public static PdmModel readModelFromClassPath(String filePath) {
        return Helper.wrap(() -> {
            PdmModel pdmModel = ClassPathUtils.readResource(filePath, IGNORABLE_XML_MAPPER, PdmModel.class);
            PdmHelper.applyTransformerChain(pdmModel);
            ModelHelper.initDefaults(pdmModel.getModel());

            return pdmModel;
        });
    }

    public static PdmModel readModelByPath(File filePath) {
        return Helper.wrap(() -> {
            PdmModel pdmModel = IGNORABLE_XML_MAPPER.readValue(filePath, PdmModel.class);
            PdmHelper.applyTransformerChain(pdmModel);
            ModelHelper.initDefaults(pdmModel.getModel());

            return pdmModel;
        });
    }

    public static PdmModel readModelByInputStream(InputStream pdmStream) {
        return Helper.wrap(() -> {
            PdmModel pdmModel = IGNORABLE_XML_MAPPER.readValue(pdmStream, PdmModel.class);
            XmlModel model = pdmModel.getModel();
            if (model == null) {
                throw new NoModelInPdmException();
            }
            PdmHelper.applyTransformerChain(pdmModel);
            ModelHelper.initDefaults(model);

            return pdmModel;
        });
    }

    public static void writePdmToFile(PdmModel pdmModel, File resultFile) {
        pdmModel.getModel().setLayout(null);
        wrap(() -> {
            Writer writer = new PrintWriter(resultFile, "UTF-8");
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n");
            writer.append(XML_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(pdmModel));
            writer.flush();
            writer.close();
        });
    }

}
