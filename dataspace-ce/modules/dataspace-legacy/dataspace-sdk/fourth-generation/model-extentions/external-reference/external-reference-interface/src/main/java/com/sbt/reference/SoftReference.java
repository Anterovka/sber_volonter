package com.sbt.reference;


public interface SoftReference<T> {
    Class<T> getEntityClass();

    String getEntityId();
}
