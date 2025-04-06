package com.sbt.status.xml;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.status.exception.EqualStatusLinkException;
import com.sbt.status.xml.user.UserXmlStatus;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.sbt.status.xml.user.UserXmlStatusTo.TO_NAME_TAG;

@XmlTagName(UserXmlStatus.STATUS_NAME_TAG)
public class XmlStatus extends UserXmlStatus<XmlStatusTo> {

    private String id;

    @Setter
    private XmlGroup group;

    @JsonCreator
    public XmlStatus(@JacksonXmlProperty(isAttribute = true, localName = CODE_NAME_TAG) String code,
                     @JacksonXmlProperty(isAttribute = true, localName = INITIAL_TAG) Boolean initial)
    {
        super(code, initial);
        this.id = id;
        this.initial = Optional.ofNullable(initial).orElse(Boolean.FALSE);
    }

    @Override
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = TO_NAME_TAG)
    public void setStatusTos(List<XmlStatusTo> statusTos) {
        this.statusTos = statusTos;

        Map<String, XmlStatusTo> status = new HashMap<>();

        statusTos.forEach(xmlStatusTo -> {
            if (status.put(xmlStatusTo.getStatusTo(), xmlStatusTo) != null) {
                throw new EqualStatusLinkException(code, xmlStatusTo.getStatusTo());
            }
        });
    }

    @Override
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = TO_NAME_TAG)
    public List<XmlStatusTo> getStatusTos() {
        if (statusTos == null) {
            statusTos = new ArrayList<>();
        }

        return statusTos;
    }

    @JsonIgnore
    public XmlGroup getGroup() {
        return group;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        XmlStatus xmlStatus = (XmlStatus) obj;
        return code.equals(xmlStatus.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
