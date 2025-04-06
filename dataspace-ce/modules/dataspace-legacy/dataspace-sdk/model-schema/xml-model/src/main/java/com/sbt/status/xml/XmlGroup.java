package com.sbt.status.xml;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.status.xml.user.UserXmlGroup;

import java.util.ArrayList;
import java.util.List;

import static com.sbt.status.xml.user.UserXmlStatus.STATUS_NAME_TAG;

public class XmlGroup extends UserXmlGroup<XmlStatus> {

    private String id;

    @JsonIgnore
    private XmlModelClass modelClass;

    @JsonCreator
    public XmlGroup(
        @JacksonXmlProperty(isAttribute = true, localName = "code") String code,
        @JacksonXmlProperty(isAttribute = true, localName = "reason-length") String reasonLength
    ) {
        super(code, reasonLength);
        this.id = id;
    }

    @Override
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = STATUS_NAME_TAG)
    public void setStatuses(List<XmlStatus> statuses) {
        this.statuses = statuses;
    }

    @Override
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = STATUS_NAME_TAG)
    public List<XmlStatus> getStatuses() {
        if (statuses == null) {
            statuses = new ArrayList<>();
        }
        return statuses;
    }

    @JsonIgnore
    public XmlModelClass getModelClass() {
        return this.modelClass;
    }

    @JsonIgnore
    public void setModelClass(XmlModelClass modelClass) {
        this.modelClass = modelClass;
    }
}
