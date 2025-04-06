package com.sbt.mg.data.model.typedef;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.usermodel.UserXmlTypeDef;

public class XmlTypeDef extends UserXmlTypeDef {


    public XmlTypeDef(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                      @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG) String type,
                      @JacksonXmlProperty(isAttribute = true, localName = LENGTH_TAG) Integer length,
                      @JacksonXmlProperty(isAttribute = true, localName = SCALE_TAG) Integer scale,
                      @JacksonXmlProperty(isAttribute = true, localName = PRECISION_TAG) Integer precision) {
        super(name, type, length, scale, precision);
    }
}
