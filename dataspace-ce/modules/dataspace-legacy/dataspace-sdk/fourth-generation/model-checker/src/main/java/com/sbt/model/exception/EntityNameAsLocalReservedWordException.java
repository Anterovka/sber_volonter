package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassEnum;
import com.sbt.mg.data.model.XmlModelInterface;
import com.sbt.mg.data.model.XmlQuery;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

/**
 * Exception: The class name is reserved for the needs of the SDK
 */
public class EntityNameAsLocalReservedWordException extends CheckXmlModelException {
    private EntityNameAsLocalReservedWordException(String entityName, String entityType) {
        super(join("Name", entityType, "\"" + entityName + "\"", " is reserved for the needs of the SDK."),
            "Correct according to requirements. The list of reserved words is described in the documentation.");
    }

    public static EntityNameAsLocalReservedWordException of(XmlModelClass modelClass) {
        return new EntityNameAsLocalReservedWordException(modelClass.getName(), "class");
    }

    public static EntityNameAsLocalReservedWordException of(XmlModelInterface modelInterface) {
        return new EntityNameAsLocalReservedWordException(modelInterface.getName(), "interface");
    }

    public static EntityNameAsLocalReservedWordException of(XmlModelClassEnum modelEnum) {
        return new EntityNameAsLocalReservedWordException(modelEnum.getName(), "enum class");
    }

    public static EntityNameAsLocalReservedWordException of(XmlQuery xmlQuery) {
        return new EntityNameAsLocalReservedWordException(xmlQuery.getName(), "request");
    }
}
