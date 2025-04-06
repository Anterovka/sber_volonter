package com.sbt.model.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class UserUnusedSchemaItemsSectionException extends CheckXmlModelException {
    public UserUnusedSchemaItemsSectionException() {
        super(join("In the model description (model.xml file), the user is not allowed to add the служебная секция <unusedSchemaItems>"),
            "Remove the <unusedSchemaItems> section from model.xml");
    }
}
