package com.sbt.mg.data.model.typedef;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.usermodel.UserXmlTypeDefs;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class XmlTypeDefs extends UserXmlTypeDefs<XmlTypeDef> {

    @Override
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = TYPE_DEF_TAG)
    public void setTypeDefs(List<XmlTypeDef> typeDefs) {
        this.typeDefs = typeDefs;
    }

    @Override
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = TYPE_DEF_TAG)
    public List<XmlTypeDef> getTypeDefs() {
        return typeDefs;
    }
}
