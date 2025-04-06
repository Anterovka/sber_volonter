package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Map;

public class InvalidRegularExpressionInMaskTagException extends CheckXmlModelException {
    public InvalidRegularExpressionInMaskTagException(XmlModelClass modelClass,
                                                      Map<XmlModelClassProperty, String> propsWithInvalidRegexpInMaskTag) {
        super(join("For properties, an incorrect regular expression is specified in the mask attribute. Error in property(ies)",
                collectClassProperties(propsWithInvalidRegexpInMaskTag), "класса", modelClass.getName()),
            join("It is necessary to change the specified regular expressions to valid ones"));
    }
}
