package com.sbt.mg.data.model.usermodel.interfaces;

import com.sbt.mg.data.model.CollectionType;

public interface UserPropertyFromXml {

    String getName();

    UserPropertyFromXml setName(String name);

    String getType();

    UserPropertyFromXml setType(String type);

    CollectionType getCollectionType();

    UserPropertyFromXml setCollectionType(CollectionType collectionType);
}
