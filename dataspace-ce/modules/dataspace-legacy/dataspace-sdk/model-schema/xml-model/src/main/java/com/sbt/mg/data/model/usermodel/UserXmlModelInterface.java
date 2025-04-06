package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.EntityDiff;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.mg.exception.checkmodel.PropertyNameAlreadyDefinedException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlTagName(XmlModel.INTERFACE_TAG)
public class UserXmlModelInterface<T extends UserXmlModelInterfaceProperty> extends EntityDiff {

    public static final String NAME_TAG = "name";
    public static final String LABEL_TAG = "label";
    public static final String PROPERTY_TAG = "property";

    protected String name;
    protected String label;

    @JsonIgnore
    protected final Map<String, T> properties = new LinkedHashMap<>();

    @JsonCreator
    public UserXmlModelInterface(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                             @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG) String label) {
        this.name = name;
        this.label = label;
    }

    public UserXmlModelInterface() {
    }

    @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG)
    public String getName() {
        return name;
    }


    @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG)
    public String getLabel() {
        return label;
    }

    /** Adds the passed properties to the interface, linking it with them */
    @JsonSetter(value = PROPERTY_TAG)
    @JacksonXmlElementWrapper(useWrapping = false)
    public void addProperties(List<T> properties) {
        properties.forEach(this::addProperty);
    }

    /** Adds a property to the interface, binding it.
     * If the property has already been added, then the PropertyNameAlreadyDefinedException is thrown. */
    public void addProperty(T property) {
        if (properties.containsKey(property.getName())) {
            throw new PropertyNameAlreadyDefinedException(property, this.name);
        }
        properties.put(property.getName(), property);
    }

    /** Get a list of properties */
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = PROPERTY_TAG)
    public List<T> getPropertiesAsList() {
        return new ArrayList<>(properties.values());
    }
}
