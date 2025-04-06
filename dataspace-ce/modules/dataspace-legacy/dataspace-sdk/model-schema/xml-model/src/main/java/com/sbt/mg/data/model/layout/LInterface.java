package com.sbt.mg.data.model.layout;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

import static com.sbt.mg.data.model.layout.LayoutTags.NAME_TAG;
import static com.sbt.mg.data.model.layout.LayoutTags.X_TAG;
import static com.sbt.mg.data.model.layout.LayoutTags.Y_TAG;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({NAME_TAG, X_TAG, Y_TAG})
public class LInterface extends Coordinates {

    public static final String INTERFACE_TAG = "interface";

    @JacksonXmlProperty(isAttribute = true)
    private final String name;

    @JacksonXmlProperty(localName = LProperty.PROPERTY_TAG)
    @JacksonXmlElementWrapper(useWrapping = false)
    private final List<LProperty> properties = new ArrayList<>();

    @JacksonXmlProperty(localName = LReference.REFERENCE_TAG)
    @JacksonXmlElementWrapper(useWrapping = false)
    private final List<LReference> references = new ArrayList<>();

    @JsonCreator
    public LInterface(@JacksonXmlProperty(localName = X_TAG) Integer x,
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

    @JacksonXmlProperty(localName = LReference.REFERENCE_TAG)
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<LReference> getReferences() {
        return references;
    }

    @JacksonXmlProperty(localName = LReference.REFERENCE_TAG)
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setReferences(List<LReference> references) {
        this.references.addAll(references);
    }
}
