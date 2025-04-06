package com.sbt.mg.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.interfaces.XmlObject;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.mg.data.model.usermodel.UserXmlQuery;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

import static com.sbt.mg.data.model.usermodel.UserXmlQueryId.ID_TAG;

@XmlTagName(UserXmlQuery.QUERY_TAG)
public class XmlQuery extends UserXmlQuery<XmlQueryParam, XmlQueryId, XmlQueryProperty, XmlQuerySql> implements XmlObject {

    private static final String DEPRECATED_TAG = "isDeprecated";
    private static final String REMOVED_TAG = "isRemoved";
    private static final String VERSION_DEPRECATED_TAG = "ver-deprecated";
    private static final String VERSION_REMOVED_TAG = "ver-removed";

    private XmlModel model;

    private Boolean isDeprecated;
    private Boolean isRemoved;
    private String versionDeprecated;
    private String versionRemoved;

    public XmlQuery(@Nonnull @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                    @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG) String label,
                    @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG) String description,
                    @JacksonXmlProperty(isAttribute = true, localName = DEPRECATED_TAG) Boolean isDeprecated,
                    @JacksonXmlProperty(isAttribute = true, localName = REMOVED_TAG) Boolean isRemoved,
                    @JacksonXmlProperty(isAttribute = true, localName = VERSION_DEPRECATED_TAG) String versionDeprecated,
                    @JacksonXmlProperty(isAttribute = true, localName = VERSION_REMOVED_TAG) String versionRemoved
                    ) {
        super(name, label, description);
        this.isDeprecated = isDeprecated;
        this.isRemoved = isRemoved;
        this.versionDeprecated = versionDeprecated;
        this.versionRemoved = versionRemoved;
    }

    @Override
    @JacksonXmlProperty(isAttribute = true, localName = ID_TAG)
    public void setId(XmlQueryId id) {
        this.id = id;
        id.setXmlQuery(this);
    }

    public XmlQuery setDeprecated(Boolean isDeprecated) {
        this.isDeprecated = isDeprecated;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = DEPRECATED_TAG)
    public Boolean isDeprecated() {
        return isDeprecated == null ? Boolean.FALSE : isDeprecated;
    }

    @JacksonXmlProperty(isAttribute = true, localName = REMOVED_TAG)
    public Boolean isRemoved() {
        return isRemoved == null ? Boolean.FALSE : isRemoved;
    }

    public void setRemoved(Boolean removed) {
        isRemoved = removed;
    }

    @JacksonXmlProperty(isAttribute = true, localName = VERSION_DEPRECATED_TAG)
    public String getVersionDeprecated() {
        return versionDeprecated;
    }

    public void setVersionDeprecated(String versionDeprecated) {
        this.versionDeprecated = versionDeprecated;
    }

    @JacksonXmlProperty(isAttribute = true, localName = VERSION_REMOVED_TAG)
    public String getVersionRemoved() {
        return versionRemoved;
    }

    public void setVersionRemoved(String versionRemoved) {
        this.versionRemoved = versionRemoved;
    }

    @JacksonXmlProperty(isAttribute = true, localName = PARAMS_TAG)
    public List<XmlQueryParam> getParams() {
        return params;
    }

    @Override
    @JacksonXmlProperty(isAttribute = true, localName = PARAMS_TAG)
    public void setParams(List<XmlQueryParam> params) {
        this.params.clear();
        // for some reason, the deserializer returns null
        if (params != null) {
            params.forEach(this::addParam);
        }
    }

    public void addParam(XmlQueryParam param) {
        params.add(param);
        param.setXmlQuery(this);
    }

    public void addProperty(XmlQueryProperty property) {
        properties.add(property);
        property.setXmlQuery(this);
    }


    @JacksonXmlProperty(isAttribute = true, localName = IMPLEMENTATIONS_TAG)
    public List<XmlQuerySql> getImplementations() {
        return implementations;
    }

    @JacksonXmlProperty(isAttribute = true, localName = IMPLEMENTATIONS_TAG)
    public void setImplementations(List<XmlQuerySql> implementations) {
        this.implementations.clear();
        implementations.forEach(this::addImplementation);
    }

    @Override
    public void addImplementation(XmlQuerySql sql) {
        sql.setXmlQuery(this);
        implementations.add(sql);
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
    public XmlQuery setModel(XmlModel model) {
        this.model = model;
        return this;
    }

    @JsonIgnore
    public List<XmlQueryProperty> getPropertiesAsList() {
        return properties;
    }

    @JsonIgnore
    public XmlQuery copy() {
        XmlQuery query = new XmlQuery(name, label, description, isDeprecated, isRemoved, versionDeprecated, versionRemoved);
        if (id != null) {
            query.setId(id.copy().setXmlQuery(query));
        }
        getImplementations().forEach(impl -> query.addImplementation(impl.copy().setXmlQuery(query)));
        getParams().forEach(param -> query.addParam(param.copy().setXmlQuery(query)));
        getProperties().forEach(prop -> query.addProperty(prop.copy().setXmlQuery(query)));
        return query;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        XmlQuery that = (XmlQuery) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
