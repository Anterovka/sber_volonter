package com.sbt.mg.data.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.mg.data.model.usermodel.UserXmlImport;

@XmlTagName(XmlModel.IMPORT_TAG)
public class XmlImport extends UserXmlImport {

    public XmlImport(@JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG) String type,
                     @JacksonXmlProperty(isAttribute = true, localName = FILE_TAG) String file) {
        super(type, file);
    }
}
