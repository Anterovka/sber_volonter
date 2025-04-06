package com.sbt.status.xml;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.status.xml.user.UserXmlStatusTo;

import static com.sbt.status.xml.user.UserXmlStatus.STATUS_NAME_TAG;

@XmlTagName(UserXmlStatusTo.TO_NAME_TAG)
public class XmlStatusTo extends UserXmlStatusTo {

    private String id;
    @JsonIgnore
    private XmlStatus status;

    @JsonCreator
    public XmlStatusTo(@JacksonXmlProperty(isAttribute = true, localName = STATUS_NAME_TAG) String statusTo,
                       @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG) String label) {
        super(statusTo, label);

        this.id = id;
    }

    @JsonIgnore
    public XmlStatus getStatus() {
        return status;
    }

    @JsonIgnore
    public void setStatus(XmlStatus status) {
        this.status = status;
    }
}
