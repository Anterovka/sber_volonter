package com.sbt.mg.data.model.layout;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;

import java.util.List;

import static com.sbt.mg.data.model.layout.LayoutTags.NAME_TAG;
import static com.sbt.mg.data.model.layout.LayoutTags.VALUE_TAG;
import static com.sbt.mg.data.model.layout.LayoutTags.X_TAG;
import static com.sbt.mg.data.model.layout.LayoutTags.Y_TAG;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@JsonPropertyOrder({NAME_TAG, X_TAG, Y_TAG})
public class LEnum extends Coordinates {

    public static final String ENUM_TAG = "enum";

    @JacksonXmlProperty(isAttribute = true)
    private final String name;
    @JacksonXmlProperty(localName = VALUE_TAG)
    @JacksonXmlElementWrapper(useWrapping = false)
    private final List<LEnumValue> values;

    @JsonCreator
    public LEnum(@JacksonXmlProperty(localName = X_TAG) Integer x,
                 @JacksonXmlProperty(localName = Y_TAG) Integer y,
                 @JacksonXmlProperty(localName = NAME_TAG) String name,
                 @JacksonXmlProperty(localName = VALUE_TAG) List<LEnumValue> values
    ) {
        super(x, y);
        this.name = name;
        this.values = values;
    }
}
