package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

public class UserXmlTypeDefs<T extends UserXmlTypeDef> {

    public static final String TYPE_DEF_TAG = "type-def";

    protected List<T> typeDefs = new ArrayList<>();

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = TYPE_DEF_TAG)
    public void setTypeDefs(List<T> typeDefs) {
        this.typeDefs = typeDefs;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = TYPE_DEF_TAG)
    public List<T> getTypeDefs() {
        return typeDefs;
    }

}
