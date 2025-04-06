package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class ScaleNotInBoundsException extends CheckXmlModelException {
    public ScaleNotInBoundsException(XmlModelClassProperty property, Integer scale, Integer length) {
        super(join("The scale of the scale field of type BigDecimal is not correct. Error in the field",
                propertyInCLass("е", property)),
            join("The value of scale must be greater than or equal to zero and less than the length (length) of the field.(scale =",
                scale, ", length =", length, ")"));
    }
}
