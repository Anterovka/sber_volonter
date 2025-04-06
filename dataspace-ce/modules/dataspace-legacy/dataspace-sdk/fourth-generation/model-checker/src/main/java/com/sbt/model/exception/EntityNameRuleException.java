package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassEnum;
import com.sbt.mg.data.model.XmlModelInterface;
import com.sbt.mg.data.model.XmlQuery;
import com.sbt.mg.exception.GeneralSdkException;
import com.sbt.model.exception.parent.CheckModelException;

import java.util.HashSet;
import java.util.Set;

import static com.sbt.mg.ModelHelper.TYPES_INFO;

/**
 * Exception: The class name is already set in the base module model
 */
public class EntityNameRuleException extends CheckModelException {
    public EntityNameRuleException() {
        super();
    }

    /**
     * @param entityName Name of the class
     */
    protected EntityNameRuleException(String entityName, String entityType, int maxNameLength) {
        super(GeneralSdkException.join("Name", entityType, "must start with an uppercase (capital) letter,",
                "not to contain Cyrillic characters, not to be empty, not to match the names of primitive types (",
                getPrimitiveNames(), ") and not exceed the length", maxNameLength,
            ". Error in the name", entityType, ":", entityName),
            "Fix according to the requirements");
    }

    public static ClassNameRuleException of(XmlModelClass modelClass, int maxNameLength) {
        return new ClassNameRuleException(modelClass, maxNameLength);
    }

    public static InterfaceNameRuleException of(XmlModelInterface modelInterface, int maxNameLength) {
        return new InterfaceNameRuleException(modelInterface, maxNameLength);
    }

    public static EnumNameRuleException of(XmlModelClassEnum modelEnum, int maxNameLength) {
        return new EnumNameRuleException(modelEnum, maxNameLength);
    }

    public static UserQueryNameRuleException of(XmlQuery xmlQuery, int maxNameLength) {
        return new UserQueryNameRuleException(xmlQuery, maxNameLength);
    }

    private static String getPrimitiveNames() {
        Set<String> types = new HashSet<>();
        TYPES_INFO.values().stream()
                .filter(it -> !"null".equals(it.getJavaName()))
                .forEach(typeInfo -> {
                    String javaName = typeInfo.getJavaName();
                    types.add(javaName);
                    typeInfo.getNames().stream()
                            .filter(name -> !name.contains(javaName.toLowerCase()))
                            .forEach(name -> types.add(name));
                });
        return String.join(", ", types);
    }
}
