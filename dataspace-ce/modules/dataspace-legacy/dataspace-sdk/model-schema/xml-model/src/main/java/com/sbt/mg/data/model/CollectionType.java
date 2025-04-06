package com.sbt.mg.data.model;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * The type of collection
 */
public enum CollectionType {
    /**
     * Set
     */
    SET(Set.class),
    /**
     * List
     */
    LIST(List.class);

    private final Class<? extends Collection> collectionJavaClass;

    CollectionType(Class<? extends Collection> collectionJavaClass) {
        this.collectionJavaClass = collectionJavaClass;
    }

    public Class<? extends Collection> getCollectionJavaClass() {
        return collectionJavaClass;
    }
}
