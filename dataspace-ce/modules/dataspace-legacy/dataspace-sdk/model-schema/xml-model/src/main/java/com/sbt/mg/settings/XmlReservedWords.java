package com.sbt.mg.settings;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JacksonXmlRootElement(localName = "reservedWords")
public class XmlReservedWords {

    @Nonnull
    private Map<String, String> reserved = new LinkedHashMap<>();

    public static class Rename {
        private String from;
        private String to;

        public Rename(@Nonnull @JacksonXmlProperty(isAttribute = true, localName = "from") String from,
                      @Nonnull @JacksonXmlProperty(isAttribute = true, localName = "to") String to) {
            this.from = from;
            this.to = to;
        }
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "rename")
    public void setTemplatesConnections(List<Rename> coreModuleInfos) {
        coreModuleInfos.forEach(coreModuleInfo -> reserved.put(coreModuleInfo.from, coreModuleInfo.to));
    }

    @Nonnull
    public Map<String, String> getReserved() {
        return reserved;
    }
}
