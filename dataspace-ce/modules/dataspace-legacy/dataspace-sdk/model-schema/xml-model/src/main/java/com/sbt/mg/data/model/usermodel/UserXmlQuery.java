package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.Helper;
import com.sbt.mg.data.model.EntityDiff;
import com.sbt.mg.data.model.interfaces.XmlTagName;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.sbt.mg.data.model.usermodel.UserXmlQueryId.ID_TAG;
import static com.sbt.mg.data.model.usermodel.UserXmlQueryProperty.PROPERTY_TAG;

@XmlTagName(UserXmlQuery.QUERY_TAG)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserXmlQuery<
        T extends UserXmlQueryParam,
        V extends UserXmlQueryId,
        U extends UserXmlQueryProperty,
        W extends UserXmlQuerySql> extends EntityDiff {

    public static final String QUERY_TAG = "query";

    public static final String NAME_TAG = "name";
    public static final String LABEL_TAG = "label";
    public static final String DESCRIPTION_TAG = "description";

    public static final String PARAMS_TAG = "params";
    public static final String IMPLEMENTATIONS_TAG = "implementations";

    protected String name;
    protected String label;
    protected String description;

    protected V id;

    protected List<T> params = new ArrayList<>();
    protected List<U> properties = new ArrayList<>();
    protected List<W> implementations = new ArrayList<>();

    public UserXmlQuery(@Nonnull @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                        @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG) String label,
                        @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG) String description
    ) {
        this.name = name;
        this.label = label != null ? Helper.replaceIllegalSymbols(label) : null;
        this.description = description != null ? Helper.replaceIllegalSymbols(description) : null;
    }

    @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG)
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JacksonXmlProperty(isAttribute = true, localName = ID_TAG)
    public V getId() {
        return id;
    }

    @JacksonXmlProperty(isAttribute = true, localName = ID_TAG)
    public void setId(V id) {
        this.id = id;
    }

    @JacksonXmlProperty(localName = PARAMS_TAG)
    public List<T> getParams() {
        return params;
    }

    @JacksonXmlProperty(isAttribute = true, localName = PARAMS_TAG)
    public void setParams(List<T> params) {
        this.params.clear();
        // for some reason, the deserializer returns null
        if (params != null) {
            params.forEach(this::addParam);
        }
    }

    public void addParam(T param) {
        params.add(param);
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = PROPERTY_TAG)
    public List<U> getProperties() {
        return properties;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = PROPERTY_TAG)
    public void setProperties(List<U> properties) {
        properties.forEach(this::addProperty);
    }

    public void addProperty(U property) {
        properties.add(property);
    }

    @JacksonXmlElementWrapper(localName = IMPLEMENTATIONS_TAG)
    @JacksonXmlProperty(isAttribute = true, localName = UserXmlQuerySql.TAG)
    public List<W> getImplementations() {
        return implementations;
    }

    @JacksonXmlElementWrapper(localName = IMPLEMENTATIONS_TAG)
    @JacksonXmlProperty(isAttribute = true, localName = UserXmlQuerySql.TAG)
    public void setImplementations(List<W> implementations) {
        this.implementations.clear();
        implementations.forEach(this::addImplementation);
    }

    public void addImplementation(W sql) {
        implementations.add(sql);
    }
}
