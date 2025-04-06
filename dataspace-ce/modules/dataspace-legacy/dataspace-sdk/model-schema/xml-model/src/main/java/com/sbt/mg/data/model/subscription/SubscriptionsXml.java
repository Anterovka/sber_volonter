package com.sbt.mg.data.model.subscription;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JacksonXmlRootElement(localName = "subscriptions")
public class SubscriptionsXml {
    @JacksonXmlProperty(isAttribute = true) String xmlns  = "DataspaceSubscriptions";
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = "subscription")
    private List<SubscriptionXmlPojo> subscriptions = new ArrayList<>();
}
