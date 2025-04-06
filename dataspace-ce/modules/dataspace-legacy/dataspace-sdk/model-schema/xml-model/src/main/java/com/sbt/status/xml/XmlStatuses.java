package com.sbt.status.xml;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.status.xml.user.UserXmlStatuses;

import java.util.ArrayList;
import java.util.List;

import static com.sbt.status.xml.user.UserXmlGroup.GROUP;

public class XmlStatuses extends UserXmlStatuses<XmlGroup> {

    @JsonCreator
    public XmlStatuses() {}

    @Override
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = GROUP)
    public void setGroups(List<XmlGroup> groups) {
        this.groups = groups;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = GROUP)
    public List<XmlGroup> getGroups() {
        if (this.groups == null) {
            this.groups = new ArrayList<>();
        }
        return groups;
    }
}
