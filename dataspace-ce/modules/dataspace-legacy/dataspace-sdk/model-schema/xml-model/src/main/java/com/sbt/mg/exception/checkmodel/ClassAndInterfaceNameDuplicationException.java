package com.sbt.mg.exception.checkmodel;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class ClassAndInterfaceNameDuplicationException extends CheckXmlModelException {
    public ClassAndInterfaceNameDuplicationException(Collection<String> classNames) {
        super(join("When describing the model, both a class and an interface with the same name were found",
                StringUtils.join(classNames, ",")),
            join("It is necessary to rename one of the objects with the name",
                StringUtils.join(classNames, ",")));
    }
}
