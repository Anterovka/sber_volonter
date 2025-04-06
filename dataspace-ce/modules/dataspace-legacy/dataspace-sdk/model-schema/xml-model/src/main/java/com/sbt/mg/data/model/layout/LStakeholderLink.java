package com.sbt.mg.data.model.layout;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static com.sbt.mg.data.model.layout.LayoutTags.CODE_TAG;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
public class LStakeholderLink {

    public static final String STAKEHOLDER_LINK_TAG = "stakeholder-link";

    @JacksonXmlProperty(isAttribute = true) private final String code;

    @JacksonXmlProperty(localName = LStatus.STATUS_TAG)
    @JacksonXmlElementWrapper(useWrapping = false)
    private final List<LStatus> statuses = new ArrayList<>();

    @JsonCreator
    public LStakeholderLink(@JacksonXmlProperty(localName = CODE_TAG) String code
    ) {
        this.code = code;
    }
}
