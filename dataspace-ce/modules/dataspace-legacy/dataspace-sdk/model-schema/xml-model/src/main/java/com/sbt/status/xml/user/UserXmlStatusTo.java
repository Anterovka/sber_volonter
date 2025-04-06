package com.sbt.status.xml.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import lombok.Setter;

import java.io.Serializable;

import static com.sbt.status.xml.user.UserXmlStatus.STATUS_NAME_TAG;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@XmlTagName(UserXmlStatusTo.TO_NAME_TAG)
public class UserXmlStatusTo implements Serializable {

    public static final String TO_NAME_TAG = "to";

    public static final String LABEL_TAG = "label";

    private final String statusTo;
    @Setter
    private String label;

    @JsonCreator
    public UserXmlStatusTo(@JacksonXmlProperty(isAttribute = true, localName = STATUS_NAME_TAG) String statusTo,
                           @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG) String label) {

        this.statusTo = statusTo;
        this.label = label == null ? "" : label;
    }

    @JacksonXmlProperty(isAttribute = true, localName = STATUS_NAME_TAG)
    public String getStatusTo() {
        return statusTo;
    }

    @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG)
    public String getLabel() {
        return label;
    }
}
