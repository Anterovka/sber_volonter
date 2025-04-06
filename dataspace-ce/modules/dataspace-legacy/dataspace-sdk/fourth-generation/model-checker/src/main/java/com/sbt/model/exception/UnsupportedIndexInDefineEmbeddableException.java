package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.model.exception.parent.CheckModelException;

import java.util.Collection;

public class UnsupportedIndexInDefineEmbeddableException extends CheckModelException {

    public UnsupportedIndexInDefineEmbeddableException(Collection<XmlModelClassProperty> properties, XmlModelClass modelClass) {
        super(join("In the declaration of an embeddable class, it is forbidden to set index and unique on its fields. Error in the class",
                modelClass.getName(), "in properties", collectClassProperties(properties)),
            "Provide indices (unique index) over fields of the embeddable class in the class that uses it.");
    }

    public UnsupportedIndexInDefineEmbeddableException(Collection<XmlModelClass> collectionClazz) {
        super(join("In the declaration of an embeddable class, it is forbidden to set indexes. Error in the class(es)",
            collectClasses(collectionClazz), "."), "Specify the necessary indexes on the fields of the embeddable class in the class that uses it.");
    }

}
