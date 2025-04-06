package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Collection;

import static com.sbt.mg.jpa.JpaConstants.MAX_BIG_DECIMAL_LENGTH;

public class BigDecimalLengthOverheadException extends CheckXmlModelException {

    public BigDecimalLengthOverheadException(XmlModelClass modelClass,
                                             Collection<XmlModelClassProperty> overheadBigDecimal) {
        super(join("Exceeding the number of characters allocated for numbers =", MAX_BIG_DECIMAL_LENGTH, ". Error in properties",
                collectClassProperties(overheadBigDecimal), "class", modelClass.getName()),
            join("Reduce the length"));
    }
}
