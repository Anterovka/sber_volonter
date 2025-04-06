package com.sbt.mg.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

public class EntityDiff {
    private final Map<String, Object> elementDiffs;

    public EntityDiff() {
        elementDiffs = new HashMap<>();
    }

    public boolean propertyChanged(String propertyName) {
        return elementDiffs.containsKey(propertyName);
    }

    public EntityDiff addChangedProperty(String propertyName, Object oldValue) {
        elementDiffs.put(propertyName, oldValue);
        return this;
    }

    public <T> T getOldValueChangedProperty(String propertyName) {
        return (T) elementDiffs.get(propertyName);
    }

    @JsonIgnore
    public Map<String, Object> getAllChanges(){
        return elementDiffs;
    }
}
