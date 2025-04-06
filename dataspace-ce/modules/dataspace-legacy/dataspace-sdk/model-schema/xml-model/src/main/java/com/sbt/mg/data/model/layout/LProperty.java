package com.sbt.mg.data.model.layout;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;

import static com.sbt.mg.data.model.layout.LayoutTags.NAME_TAG;
import static com.sbt.mg.data.model.layout.LayoutTags.X_TAG;
import static com.sbt.mg.data.model.layout.LayoutTags.Y_TAG;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@JsonPropertyOrder({NAME_TAG, X_TAG, Y_TAG})
public class LProperty extends Coordinates {

    public static final String PROPERTY_TAG = "property";

    @JacksonXmlProperty(isAttribute = true) private final String name;

    @JsonCreator
    public LProperty(@JacksonXmlProperty(localName = X_TAG) Integer x,
                     @JacksonXmlProperty(localName = Y_TAG) Integer y,
                     @JacksonXmlProperty(localName = NAME_TAG) String name
    ) {
        super(x, y);
        this.name = name;
    }
}
