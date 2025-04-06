package com.sbt.dictionary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class DictObject {

    private final String type;
    private final Object id;
    private final Collection<DictObject> depends = new ArrayList<>();
    private boolean handled;

    public DictObject(String type, Object id) {
        this.type = type;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public Object getId() {
        return id;
    }

    public boolean isHandled() {
        return handled;
    }

    public void handle() {
        this.handled = true;
    }

    public Collection<DictObject> getDepends() {
        return this.depends;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DictObject that = (DictObject) o;
        return type.equals(that.type) && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id);
    }

    @Override
    public String toString() {
        return "Object{" +
                "type='" + type + '\'' +
                ", id=" + id +
                ", depends=(" + depends.stream()
                .map(it -> String.format("%s:%s", it.getType(), it.getId()))
                .collect(Collectors.joining(", ")) +
                ")}";
    }
}
