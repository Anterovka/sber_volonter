package com.sbt.mg.data.model.subscription;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@JacksonXmlRootElement(localName = "subscriptions")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder("id")
@Getter
@Setter
public class SubscriptionXmlPojo {
    @JacksonXmlProperty(isAttribute = true)
    public String id;
    @JacksonXmlProperty(isAttribute = true)
    public String name;
    @JacksonXmlProperty(isAttribute = true)
    public String description;
    @JacksonXmlProperty(isAttribute = true)
    public String target;
    @JacksonXmlProperty(isAttribute = true)
    public String eventType;
    @JacksonXmlProperty(isAttribute = true)
    public String callback;
    @JacksonXmlProperty(isAttribute = true)
    public Boolean async;
    @JacksonXmlProperty(isAttribute = true)
    public Boolean blocking;
    @JacksonXmlProperty(isAttribute = true)
    public OffsetDateTime validTill;
    @JacksonXmlProperty(isAttribute = true)
    public String maxRetryAttempts;
    @JacksonXmlProperty(isAttribute = true)
    public String timeoutMs;
    @JacksonXmlProperty(isAttribute = true)
    public String retryDelayMs;
    public String ownerId;
    public String criteria;
    public String query;
    public String template;
    public String headers;
    @JacksonXmlProperty(isAttribute = true)
    public String idempotenceHeaderName;
}