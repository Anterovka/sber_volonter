package com.sbt.mg.data.model.layout;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;

import static com.sbt.mg.data.model.layout.LayoutTags.NAME_TAG;
import static com.sbt.mg.data.model.layout.LayoutTags.VALUE_TAG;
import static com.sbt.mg.data.model.layout.LayoutTags.X_TAG;
import static com.sbt.mg.data.model.layout.LayoutTags.Y_TAG;

@JacksonXmlRootElement(localName = LEnumValue.VALUE)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@JsonPropertyOrder({NAME_TAG, X_TAG, Y_TAG, })
public class LEnumValue extends Coordinates {

    public static final String VALUE = VALUE_TAG;

    @JacksonXmlProperty(isAttribute = true) private final String name;

    @JsonCreator
    public LEnumValue(@JacksonXmlProperty(localName = X_TAG) Integer x,
                      @JacksonXmlProperty(localName = Y_TAG) Integer y,
                      @JacksonXmlProperty(localName = NAME_TAG) String name
    ) {
        super(x, y);
        this.name = name;
    }
}
