package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassEnum;
import com.sbt.mg.data.model.XmlModelInterface;
import com.sbt.mg.data.model.XmlQuery;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

/**
 * Exception: The class name is reserved for the needs of the SDK
 */
public class EntityNameAsJavaReservedWordException extends CheckXmlModelException {
    private EntityNameAsJavaReservedWordException(String entityName, String entityType) {
        super(join("Name", entityType, "\"" + entityName + "\"", "reserved by Java language."),
            "Fix according to requirements. The list of reserved words is described in the specification for Java.");
    }

    public static EntityNameAsJavaReservedWordException of(XmlModelClass modelClass) {
        return new EntityNameAsJavaReservedWordException(modelClass.getName(), "class");
    }

    public static EntityNameAsJavaReservedWordException of(XmlModelInterface modelInterface) {
        return new EntityNameAsJavaReservedWordException(modelInterface.getName(), "интерфейса");
    }

    public static EntityNameAsJavaReservedWordException of(XmlModelClassEnum modelEnum) {
        return new EntityNameAsJavaReservedWordException(modelEnum.getName(), "enum класса");
    }

    public static EntityNameAsJavaReservedWordException of(XmlQuery xmlQuery) {
        return new EntityNameAsJavaReservedWordException(xmlQuery.getName(), "query class");
    }
}
