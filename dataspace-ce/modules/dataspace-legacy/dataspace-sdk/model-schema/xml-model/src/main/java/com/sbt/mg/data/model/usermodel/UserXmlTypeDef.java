package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.exception.checkmodel.ScalePrecisionBothInitException;

public class UserXmlTypeDef {

    public static final String NAME_TAG = "name";
    public static final String TYPE_TAG = "type";
    public static final String LENGTH_TAG = "length";
    public static final String PRECISION_TAG = "precision";
    public static final String SCALE_TAG = "scale";

    private final String name;
    private final String type;
    private final Integer length;
    private final Integer scale;

    public UserXmlTypeDef(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                          @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG) String type,
                          @JacksonXmlProperty(isAttribute = true, localName = LENGTH_TAG) Integer length,
                          @JacksonXmlProperty(isAttribute = true, localName = SCALE_TAG) Integer scale,
                          @JacksonXmlProperty(isAttribute = true, localName = PRECISION_TAG) Integer precision) {
        this.name = name;
        this.type = type;
        this.length = length;

        if (precision != null && scale != null) {
            throw new ScalePrecisionBothInitException(name);
        }
        this.scale = precision == null ? scale : precision;
    }

    @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG)
    public String getName() {
        return name;
    }

    @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG)
    public String getType() {
        return type;
    }

    @JacksonXmlProperty(isAttribute = true, localName = LENGTH_TAG)
    public Integer getLength() {
        return length;
    }

    @JacksonXmlProperty(isAttribute = true, localName = SCALE_TAG)
    public Integer getScale() {
        return scale;
    }
}
