package com.sbt.mg.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlObject;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.mg.data.model.usermodel.UserXmlModelInterface;
import com.sbt.mg.exception.checkmodel.PropertyNameAlreadyDefinedException;
import com.sbt.mg.exception.checkmodel.PropertyNotFoundInInterfaceException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlTagName(XmlModel.INTERFACE_TAG)
public class XmlModelInterface extends UserXmlModelInterface<XmlModelInterfaceProperty> implements XmlObject {

    public static final String DESCRIPTION_TAG = "description";

    private XmlModel model;
    private String description = "";

    public XmlModelInterface() {

    }

    @JsonCreator
    public XmlModelInterface(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                             @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG) String label) {
        super(name, label);
        this.label = StringUtils.isEmpty(label) ? "" : label;
    }

    /**
     * Overrides properties on the interface by linking to it
     *
     * @param properties the properties
     */
    @JsonIgnore
    public void setProperties(List<XmlModelInterfaceProperty> properties) {
        properties.clear();
        addProperties(properties);
    }

    /** Adds a property to the interface, binding it.
     * If the property has already been added, then the PropertyNameAlreadyDefinedException is thrown. */
    @Override
    public void addProperty(XmlModelInterfaceProperty property) {
        if (properties.containsKey(property.getName())) {
            throw new PropertyNameAlreadyDefinedException(property, this.name);
        }
        properties.put(property.getName(), property);
        property.setModelInterface(this);
    }

    /** Adds a property without checking if a property with that name already exists in the interface */
    // didn't bother to check why such trickery was needed
    public void addPropertyWithoutCheck(XmlModelInterfaceProperty property) {
        this.properties.put(property.getName(), property);
        property.setModelInterface(this);
    }

    /** Getting property by name. If the property is not found, then PropertyNotFoundInInterfaceException */
    public XmlModelInterfaceProperty getProperty(String name) {
        XmlModelInterfaceProperty property = properties.get(name);
        if (property == null) {
            throw new PropertyNotFoundInInterfaceException(name, this);
        }
        return property;
    }

    /** Returns the property by name */
    public Optional<XmlModelInterfaceProperty> findProperty(String name) {
        return Optional.ofNullable(properties.get(name));
    }

    /** Does the interface contain a property with the given name? */
    public boolean containsProperty(String propertyName) {
        return properties.containsKey(propertyName);
    }


    /**
     * Get model
     */
    @JsonIgnore
    public XmlModel getModel() {
        return model;
    }

    /**
     * Set model
     */
    public void setModel(XmlModel model) {
        this.model = model;
    }

    public XmlModelInterface setDescription(String description) {
        this.description = description;
        return this;
    }

    /** Get a list of properties */
    @Override
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = PROPERTY_TAG)
    public List<XmlModelInterfaceProperty> getPropertiesAsList() {
        return new ArrayList<>(properties.values());
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        XmlModelInterface iface = (XmlModelInterface) o;
        return Objects.equals(name, iface.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Interface{" +
                "name='" + name + '\'' +
                '}';
    }
}
