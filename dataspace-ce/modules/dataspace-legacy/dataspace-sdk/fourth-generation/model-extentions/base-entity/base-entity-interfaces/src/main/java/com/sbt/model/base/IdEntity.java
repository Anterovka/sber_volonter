package com.sbt.model.base;

public interface IdEntity<T> {
    T getObjectId();

    void setObjectId(T objectId);
}
