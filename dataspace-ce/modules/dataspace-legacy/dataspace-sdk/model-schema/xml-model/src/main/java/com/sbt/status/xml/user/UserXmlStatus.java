package com.sbt.status.xml.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.sbt.status.xml.user.UserXmlStatusTo.TO_NAME_TAG;

@XmlTagName(UserXmlStatus.STATUS_NAME_TAG)
public class UserXmlStatus<T extends UserXmlStatusTo> implements Serializable {

    public static final String STATUS_NAME_TAG = "status";

    public static final String CODE_NAME_TAG = "code";
    public static final String INITIAL_TAG = "initial";

    protected final String code;
    @Setter
    protected Boolean initial;

    protected List<T> statusTos;

    @JsonCreator
    public UserXmlStatus(@JacksonXmlProperty(isAttribute = true, localName = CODE_NAME_TAG) String code,
                         @JacksonXmlProperty(isAttribute = true, localName = INITIAL_TAG) Boolean initial) {
        this.code = code;

        this.initial = initial;
    }

    @JacksonXmlProperty(isAttribute = true, localName = CODE_NAME_TAG)
    public String getCode() {
        return code;
    }

    @JacksonXmlProperty(isAttribute = true, localName = INITIAL_TAG)
    public Boolean isInitial() {
        return initial;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = TO_NAME_TAG)
    public List<T> getStatusTos() {
        if (statusTos == null) {
            statusTos = new ArrayList<>();
        }

        return statusTos;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = TO_NAME_TAG)
    public void setStatusTos(List<T> statusTos) {
        this.statusTos = statusTos;
    }
}
