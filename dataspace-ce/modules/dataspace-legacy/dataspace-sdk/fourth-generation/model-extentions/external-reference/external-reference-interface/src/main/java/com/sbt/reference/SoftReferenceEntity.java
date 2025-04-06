package com.sbt.reference;

public interface SoftReferenceEntity<T extends SoftReference<?>, F>  {
    T getReference();
    F getBackReference();
}
