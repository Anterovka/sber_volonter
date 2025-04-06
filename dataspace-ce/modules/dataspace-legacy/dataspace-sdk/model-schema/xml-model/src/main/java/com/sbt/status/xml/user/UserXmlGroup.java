package com.sbt.status.xml.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;

import java.util.ArrayList;
import java.util.List;

import static com.sbt.status.xml.user.UserXmlStatus.STATUS_NAME_TAG;

@XmlTagName(UserXmlGroup.GROUP)
public class UserXmlGroup<T extends UserXmlStatus> {

    public static final String GROUP = "group";

    private final String code;
    private final String reasonLength;

    protected List<T> statuses;

    @JsonCreator
    public UserXmlGroup(
        @JacksonXmlProperty(isAttribute = true, localName = "code") String code,
        @JacksonXmlProperty(isAttribute = true, localName = "reason-length") String reasonLength
    ) {
        this.code = code;
        this.reasonLength = reasonLength;
    }

    @JacksonXmlProperty(isAttribute = true, localName = "code")
    public String getCode() {
        return this.code;
    }

    @JacksonXmlProperty(isAttribute = true, localName = "reason-length")
    public String getReasonLength() {
        return this.reasonLength;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = STATUS_NAME_TAG)
    public void setStatuses(List<T> statuses) {
        this.statuses = statuses;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty
    public List<T> getStatuses() {
        if (statuses == null) {
            statuses = new ArrayList<>();
        }
        return this.statuses;
    }
}
