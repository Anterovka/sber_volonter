package com.sbt.mg.data.model.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sbt.mg.data.model.CollectionType;
import com.sbt.mg.data.model.usermodel.interfaces.UserPropertyFromXml;

public interface PropertyFromXml extends UserPropertyFromXml {
    String getName();

    PropertyFromXml setName(String name);

    String getType();

    PropertyFromXml setType(String type);

    CollectionType getCollectionType();

    PropertyFromXml setCollectionType(CollectionType collectionType);

    boolean isEnum();

    PropertyFromXml setEnum(Boolean isEnum);

    Boolean isExternalLink();

    PropertyFromXml setExternalLink(Boolean externalLink);

    @JsonIgnore
    default String getTypeAsString() {
        String type = getType();

        if (getCollectionType() == CollectionType.SET) {
            type = "Set<" + type + ">";
        } else if (getCollectionType() == CollectionType.LIST) {
            type = "List<" + type + ">";
        }
        return type;
    }
}
