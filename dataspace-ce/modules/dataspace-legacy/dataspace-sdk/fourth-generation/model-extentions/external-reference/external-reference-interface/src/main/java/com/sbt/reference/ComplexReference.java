package com.sbt.reference;


public interface ComplexReference<R, T> extends SoftReference<T> {
    Class<R> getRootEntityClass();

    String getRootEntityId();
}
