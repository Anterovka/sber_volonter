package com.sbt.mg.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.mg.data.model.usermodel.UserXmlEnum;
import com.sbt.parameters.enums.Changeable;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlTagName(UserXmlEnum.ENUM_TAG)
public class XmlModelClassEnum extends UserXmlEnum<XmlEnumValue> {

    private static final String DEPRECATED_TAG = "deprecated";
    private static final String ACCESS_TAG = "access";

    private boolean deprecated;
    private Changeable changeable = Changeable.UPDATE;

    public XmlModelClassEnum() {

    }

    @JsonCreator
    public XmlModelClassEnum(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                             @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG) String label,
                             @JacksonXmlProperty(isAttribute = true, localName = DEPRECATED_TAG) Boolean deprecated,
                             @JacksonXmlProperty(isAttribute = true, localName = ACCESS_TAG) Changeable changeable
    ) {
        this(name, label, deprecated);
        this.label = StringUtils.isEmpty(label) ? "" : label;
        if (changeable != null) {
            this.changeable = changeable;
        }
    }

    public XmlModelClassEnum(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                             @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG) String label,
                             @JacksonXmlProperty(isAttribute = true, localName = DEPRECATED_TAG) Boolean deprecated
    ) {
        super(name, label);
        this.deprecated = Boolean.TRUE.equals(deprecated);
    }

    @JacksonXmlProperty(isAttribute = true, localName = ACCESS_TAG)
    public Changeable getChangeable() {
        return changeable;
    }

    public void setChangeable(Changeable changeable) {
        this.changeable = changeable;
    }

    @JacksonXmlProperty(isAttribute = true, localName = DEPRECATED_TAG)
    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = VALUE_TAG)
    public List<XmlEnumValue> getEnumValues() {
        return enumValue;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = VALUE_TAG)
    public void setEnumValue(List<XmlEnumValue> enumValue) {
        this.enumValue = enumValue;
    }
}
