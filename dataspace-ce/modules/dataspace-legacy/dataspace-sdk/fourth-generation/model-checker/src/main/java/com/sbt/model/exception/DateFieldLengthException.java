package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Collection;

import static com.sbt.mg.jpa.JpaConstants.MAX_DATE_LENGTH;

public class DateFieldLengthException extends CheckXmlModelException {

    public DateFieldLengthException(String className, Collection<XmlModelClassProperty> dateProperties) {
        super(join("For fields with date types, you cannot set the length to more than", MAX_DATE_LENGTH, "or less than 1 (except for LocalDateTime and OffsetDateTime, which allow length = 0, meaning time without milliseconds).",
                "Error in properties",
                collectClassProperties(dateProperties), "of class", className),
            join("Set length according to requirements."));
    }
}
