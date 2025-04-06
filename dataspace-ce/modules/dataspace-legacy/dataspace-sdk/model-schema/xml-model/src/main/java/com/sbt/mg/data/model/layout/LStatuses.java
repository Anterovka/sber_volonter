package com.sbt.mg.data.model.layout;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

import static com.sbt.mg.data.model.layout.LayoutTags.CLASS_TAG;
import static com.sbt.mg.data.model.layout.LayoutTags.X_TAG;
import static com.sbt.mg.data.model.layout.LayoutTags.Y_TAG;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({CLASS_TAG, X_TAG, Y_TAG})
public class LStatuses extends Coordinates {

    public static final String STATUSES_TAG = "statuses";

    @JacksonXmlProperty(isAttribute = true, localName = CLASS_TAG)
    private final String clazz;

    @JacksonXmlProperty(localName = LStakeholderLink.STAKEHOLDER_LINK_TAG)
    @JacksonXmlElementWrapper(useWrapping = false)
    private final List<LStakeholderLink> stakeholderLinks = new ArrayList<>();

    @JsonCreator
    public LStatuses(@JacksonXmlProperty(localName = X_TAG) Integer x,
                     @JacksonXmlProperty(localName = Y_TAG) Integer y,
                     @JacksonXmlProperty(localName = CLASS_TAG) String clazz
    ) {
        super(x, y);
        this.clazz = clazz;
    }

    public String getClazz() {
        return clazz;
    }

    @JacksonXmlProperty(localName = LStakeholderLink.STAKEHOLDER_LINK_TAG)
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<LStakeholderLink> getStakeholderLinks() {
        return stakeholderLinks;
    }

    @JacksonXmlProperty(localName = LStakeholderLink.STAKEHOLDER_LINK_TAG)
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setStakeholderLinks(List<LStakeholderLink> stakeholderLinks) {
        this.stakeholderLinks.addAll(stakeholderLinks);
    }
}
