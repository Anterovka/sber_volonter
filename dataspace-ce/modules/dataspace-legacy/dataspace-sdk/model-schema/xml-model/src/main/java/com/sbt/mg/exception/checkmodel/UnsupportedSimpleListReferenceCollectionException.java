package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.XmlModelClassProperty;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Exception: Unsupported collection
 */
public class UnsupportedSimpleListReferenceCollectionException extends CheckXmlModelException {
    /**
     * @param property unsupported collection type
     */
    public UnsupportedSimpleListReferenceCollectionException(XmlModelClassProperty property) {
        super(join("Collection for enum and some primitives [",
                ModelHelper.TYPES_INFO.entrySet().stream()
                    .filter(typeInfo -> !typeInfo.getValue().isCollect())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.joining(",")),
                "] is prohibited.",
                propertyInCLass("о", property)),
            join("Create an entity with a property whose type is", property.getType()));
    }
}
