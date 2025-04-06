package com.sbt.mg.data.type;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.sbt.mg.data.model.TypeInfo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JacksonXmlRootElement(localName = "types")
public class XmlTypesInfo {
    private final Map<String, TypeInfo> typeInfoMap = new LinkedHashMap<>();

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "type")
    public void setTemplatesConnections(List<TypeInfo> typeInfos) {
        typeInfos.forEach(coreModuleInfo -> coreModuleInfo.getNames().forEach(s -> typeInfoMap.put(s, coreModuleInfo)));
    }

    public Map<String, TypeInfo> getTypeInfoMap() {
        return typeInfoMap;
    }
}
