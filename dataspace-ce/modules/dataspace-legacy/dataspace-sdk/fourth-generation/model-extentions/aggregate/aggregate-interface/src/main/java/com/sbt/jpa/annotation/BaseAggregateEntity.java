package com.sbt.jpa.annotation;

public interface BaseAggregateEntity<T> {
    T getAggregateRoot();

    void setAggregateRoot(T aggregateRoot);
}
