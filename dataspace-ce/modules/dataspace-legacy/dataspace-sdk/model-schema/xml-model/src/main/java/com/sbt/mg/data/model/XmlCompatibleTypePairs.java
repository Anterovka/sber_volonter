package com.sbt.mg.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "pairs")
public class XmlCompatibleTypePairs {
    @Nonnull
    @JsonIgnore
    private List<CompatibleTypePair> compatibleTypePairs = new ArrayList<>();

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "pair")
    public void setCompatibleTypePairs(List<CompatibleTypePair> compatibleTypePairs) {
        this.compatibleTypePairs = compatibleTypePairs;
    }

    @Nonnull
    public List<CompatibleTypePair> getCompatibleTypePairs() {
        return compatibleTypePairs;
    }
}
