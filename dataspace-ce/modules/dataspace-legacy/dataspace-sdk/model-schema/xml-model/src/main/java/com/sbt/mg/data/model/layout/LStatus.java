package com.sbt.mg.data.model.layout;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;

import static com.sbt.mg.data.model.layout.LayoutTags.CODE_TAG;
import static com.sbt.mg.data.model.layout.LayoutTags.X_TAG;
import static com.sbt.mg.data.model.layout.LayoutTags.Y_TAG;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@JsonPropertyOrder({CODE_TAG, X_TAG, Y_TAG})
public class LStatus extends Coordinates {

    public static final String STATUS_TAG = "status";

    @JacksonXmlProperty(isAttribute = true) private final String code;

    @JsonCreator
    public LStatus(@JacksonXmlProperty(localName = X_TAG) Integer x,
                   @JacksonXmlProperty(localName = Y_TAG) Integer y,
                   @JacksonXmlProperty(localName = CODE_TAG) String code
    ) {
        super(x, y);
        this.code = code;
    }
}
