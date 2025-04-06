package com.sbt.mg.data.model.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.Property;
import com.sbt.mg.data.model.XmlIndex;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.mg.data.model.usermodel.UserXmlEvent;
import com.sbt.mg.jpa.JpaConstants;

import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@XmlTagName(UserXmlEvent.EVENT_TAG)
public class XmlEvent extends UserXmlEvent<XmlModelClassProperty, XmlIndex> {

    public static final String VERSION_DEPRECATED_TAG = "ver-deprecated";

    private String versionDeprecated;

    @JsonCreator
    public XmlEvent(
            @JacksonXmlProperty(isAttribute = true, localName = MERGE_EVENT_TAG) Boolean mergeEvent
    ) {
        this.mergeEvent = mergeEvent != null && mergeEvent;
    }

    @JsonIgnore
    public XmlModelClass convertToClass() {
        return XmlModelClass.Builder.create()
                .setName(this.name)
                .setLabel(this.label)
                .setDescription(this.description)
                .setEvent(true)
                .addProperties(this.properties)
                .setIndices(this.indices)
                .addIndices(baseIndices())
                .setBaseClassMark(true)
                .setDeprecated(this.isDeprecated)
                .setExtendedClassName(Boolean.TRUE == this.mergeEvent ? JpaConstants.BASE_MERGE_EVENT_NAME : JpaConstants.BASE_EVENT_NAME)
                .build();
    }

    private List<XmlIndex> baseIndices() {
        return Collections.singletonList(
                new XmlIndex()
                        .addProperty(new Property("status"))
                        .addProperty(new Property("partitionHash"))
                        .addProperty(new Property("creationTimestamp"))
        );
    }

    @JacksonXmlProperty(isAttribute = true, localName = VERSION_DEPRECATED_TAG)
    public String getVersionDeprecated() {
        return versionDeprecated;
    }

    public void setVersionDeprecated(String versionDeprecated) {
        this.versionDeprecated = versionDeprecated;
    }
}
