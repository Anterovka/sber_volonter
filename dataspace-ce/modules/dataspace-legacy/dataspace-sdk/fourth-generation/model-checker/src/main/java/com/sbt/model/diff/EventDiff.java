package com.sbt.model.diff;

import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.event.XmlEvent;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EventDiff {

    public void handler(XmlModel newModel, XmlModel baseModel) {
        if (baseModel == null) {
            return;
        }

        addNewEventsToBaseModel(newModel, baseModel);
    }

    private void addNewEventsToBaseModel(XmlModel newModel, XmlModel baseModel) {
        List<XmlEvent> absentEvents =
                newModel.getEvents().stream().filter(newEvent ->
                        baseModel.getEvents().stream().noneMatch(it -> Objects.equals(newEvent.getName(), it.getName()))
                ).collect(Collectors.toList());
        baseModel.addEvents(absentEvents);
    }

}
