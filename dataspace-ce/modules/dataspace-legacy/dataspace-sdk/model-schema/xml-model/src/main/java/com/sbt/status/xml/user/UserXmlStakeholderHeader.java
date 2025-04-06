package com.sbt.status.xml.user;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;

import java.io.Serializable;

@XmlTagName(UserXmlStakeholderHeader.STAKEHOLDER_TAG)
public class UserXmlStakeholderHeader implements Serializable {

    public static final String STAKEHOLDER_TAG = "stakeholder";

    public static final String CODE_TAG = "code";
    public static final String NAME_TAG = "name";

    private final String code;
    private final String name;

    public UserXmlStakeholderHeader(@JacksonXmlProperty(isAttribute = true, localName = CODE_TAG) String code,
                                    @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name) {
        this.code = code;
        this.name = name;
    }

    @JacksonXmlProperty(isAttribute = true, localName = CODE_TAG)
    public String getCode() {
        return code;
    }

    @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG)
    public String getName() {
        return name;
    }
}
