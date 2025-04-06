package com.sbt.mg.data.model.layout;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;

@Getter
public abstract class Coordinates {

    @JacksonXmlProperty(isAttribute = true) protected Integer x;
    @JacksonXmlProperty(isAttribute = true) protected Integer y;

    public Coordinates(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }
}
