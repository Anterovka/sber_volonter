package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.EntityDiff;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.interfaces.XmlTagName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@XmlTagName(XmlModelClass.IDEMPOTENCE_EXCLUDE_TAG)
public class UserXmlIdempotenceExclude<T extends UserXmlProperty> extends EntityDiff {

    public static final String PROPERTY_TAG = "property";

    private List<T> properties = new ArrayList<>();

    @JsonCreator
    public UserXmlIdempotenceExclude() {
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = PROPERTY_TAG)
    public List<T> getProperties() {
        return Collections.unmodifiableList(properties);
    }

}
