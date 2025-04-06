package com.sbt.mg.data.model.layout;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;

import static com.sbt.mg.data.model.layout.LayoutTags.X_TAG;
import static com.sbt.mg.data.model.layout.LayoutTags.Y_TAG;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
public class LModel extends Coordinates {

    public static final String MODEL_TAG = "model";

    public LModel() {
        super(0, 0);
    }

    @JsonCreator
    public LModel(@JacksonXmlProperty(localName = X_TAG) Integer x,
                  @JacksonXmlProperty(localName = Y_TAG) Integer y) {
        super(x, y);
    }

    @Override
    public String toString() {
        return "Model{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
