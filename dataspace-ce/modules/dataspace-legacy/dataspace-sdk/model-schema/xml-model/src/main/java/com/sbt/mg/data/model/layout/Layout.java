package com.sbt.mg.data.model.layout;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

import static com.sbt.mg.data.model.layout.LModel.MODEL_TAG;

@JacksonXmlRootElement(localName = Layout.LAYOUT_TAG)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Layout {

    public static final String LAYOUT_TAG = "layout";
    public static final String ENUMS_TAG = "enums";
    public static final String CLASSES_TAG = "classes";
    public static final String INTERFACES_TAG = "interfaces";
    public static final String EVENTS_TAG = "events";

    private final LModel model;
    private final List<LEnum> enums = new ArrayList<>();
    private final List<LClass> classes = new ArrayList<>();
    private final List<LStatuses> statuses = new ArrayList<>();
    private final List<LInterface> interfaces = new ArrayList<>();
    private final List<LEvent> events = new ArrayList<>();

    @JsonCreator
    public Layout(@JacksonXmlProperty(localName = MODEL_TAG) LModel model) {
        this.model = model;
    }

    @JacksonXmlProperty
    public LModel getModel() {
        return model;
    }

    @JacksonXmlProperty(localName = LEnum.ENUM_TAG)
    @JacksonXmlElementWrapper(localName = ENUMS_TAG)
    public List<LEnum> getEnums() {
        return enums;
    }

    @JacksonXmlProperty(localName = LClass.CLASS_TAG)
    @JacksonXmlElementWrapper(localName = CLASSES_TAG)
    public List<LClass> getClasses() {
        return classes;
    }

    @JacksonXmlProperty(localName = LStatuses.STATUSES_TAG)
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<LStatuses> getStatuses() {
        return statuses;
    }

    @JacksonXmlProperty(localName = LStatuses.STATUSES_TAG)
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setStatuses(List<LStatuses> statuses) {
        this.statuses.addAll(statuses);
    }

    @JacksonXmlProperty(localName = LInterface.INTERFACE_TAG)
    @JacksonXmlElementWrapper(localName = INTERFACES_TAG)
    public void setInterfaces(List<LInterface> interfaces) {
        this.interfaces.addAll(interfaces);
    }

    @JacksonXmlProperty(localName = LInterface.INTERFACE_TAG)
    @JacksonXmlElementWrapper(localName = INTERFACES_TAG)
    public List<LInterface> getInterfaces() {
        return interfaces;
    }

    @JacksonXmlProperty(localName = LEvent.ENUM_TAG)
    @JacksonXmlElementWrapper(localName = EVENTS_TAG)
    public List<LEvent> getEvents() {
        return events;
    }

    @JacksonXmlProperty(localName = LEvent.ENUM_TAG)
    @JacksonXmlElementWrapper(localName = EVENTS_TAG)
    public void setEvents(List<LEvent> events) {
        this.events.addAll(events);
    }

}
