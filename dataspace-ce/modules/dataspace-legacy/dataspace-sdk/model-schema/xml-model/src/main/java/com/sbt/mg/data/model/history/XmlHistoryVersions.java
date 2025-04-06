package com.sbt.mg.data.model.history;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.sbt.mg.data.model.interfaces.XmlTagName;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * The root element of the version history
 */
@JacksonXmlRootElement(localName = XmlHistoryVersions.HISTORY_VERSIONS_TAG)
@XmlTagName(XmlHistoryVersions.HISTORY_VERSIONS_TAG)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class XmlHistoryVersions {

    public static final String HISTORY_VERSIONS_TAG = "history-versions";
    public static final String MAX_ID_TAG = "maxId";
    public static final String MAX_VERSION_TAG = "maxVersion";

    private String maxId; // For convenience, stores the maximum value of id
    private Long maxVersion; // For convenience stores the maximum value of version

    @Nonnull
    @JsonIgnore
    private List<XmlHistoryVersion> historyVersions = new ArrayList<>();

    @JacksonXmlProperty(isAttribute = true, localName = MAX_ID_TAG)
    public String getMaxId() {
        return maxId;
    }

    public void setMaxId(String maxId) {
        this.maxId = maxId;
    }

    @JacksonXmlProperty(isAttribute = true, localName = MAX_VERSION_TAG)
    public Long getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(Long maxVersion) {
        this.maxVersion = maxVersion;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = XmlHistoryVersion.HISTORY_VERSION_TAG)
    public void setHistoryVersions(List<XmlHistoryVersion> historyVersions) {
        this.historyVersions = historyVersions;
    }

    @Nonnull
    public List<XmlHistoryVersion> getHistoryVersions() {
        return historyVersions;
    }
}
