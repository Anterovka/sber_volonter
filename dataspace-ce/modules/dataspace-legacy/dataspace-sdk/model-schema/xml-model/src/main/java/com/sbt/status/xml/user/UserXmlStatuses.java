package com.sbt.status.xml.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

import static com.sbt.status.xml.user.UserXmlGroup.GROUP;

public class UserXmlStatuses<T extends UserXmlGroup> {

    protected List<T> groups;

    @JsonCreator
    public UserXmlStatuses() {}

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = GROUP)
    public void setGroups(List<T> groups) {
        this.groups = groups;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = GROUP)
    public List<T> getGroups() {
        return groups;
    }
}
