package com.sbt.mg.data.model.interfaces;

import com.sbt.mg.data.model.CollectionType;

public interface ReferenceFromXml {
    String getName();

    ReferenceFromXml setName(String name);

    String getType();

    ReferenceFromXml setType(String type);

    CollectionType getCollectionType();

    ReferenceFromXml setCollectionType(CollectionType collectionType);
}
