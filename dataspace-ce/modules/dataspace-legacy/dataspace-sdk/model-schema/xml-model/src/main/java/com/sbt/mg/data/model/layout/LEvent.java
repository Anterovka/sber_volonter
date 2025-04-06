package com.sbt.mg.data.model.layout;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import static com.sbt.mg.data.model.layout.LayoutTags.NAME_TAG;
import static com.sbt.mg.data.model.layout.LayoutTags.X_TAG;
import static com.sbt.mg.data.model.layout.LayoutTags.Y_TAG;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@JsonPropertyOrder({NAME_TAG, X_TAG, Y_TAG})
public class LEvent extends Coordinates {

    public static final String ENUM_TAG = "event";

    @JacksonXmlProperty(isAttribute = true)
    private final String name;

    @JacksonXmlProperty(localName = LProperty.PROPERTY_TAG)
    @JacksonXmlElementWrapper(useWrapping = false)
    private final List<LProperty> properties = new ArrayList<>();

    @JsonCreator
    public LEvent(@JacksonXmlProperty(localName = X_TAG) Integer x,
                  @JacksonXmlProperty(localName = Y_TAG) Integer y,
                  @JacksonXmlProperty(localName = NAME_TAG) String name
    ) {
        super(x, y);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @JacksonXmlProperty(localName = LProperty.PROPERTY_TAG)
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<LProperty> getProperties() {
        return properties;
    }

    @JacksonXmlProperty(localName = LProperty.PROPERTY_TAG)
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setProperties(List<LProperty> properties) {
        this.properties.addAll(properties);
    }
}
