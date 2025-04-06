package com.sbt.model.exception;

import com.sbt.mg.data.model.typedef.XmlTypeDef;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Collection;

import static com.sbt.mg.jpa.JpaConstants.MAX_BIG_DECIMAL_LENGTH;

public class TypeDefBigDecimalLengthOverheadException extends CheckXmlModelException {

    public TypeDefBigDecimalLengthOverheadException(Collection<XmlTypeDef> overheadBigDecimal) {
        super(join("Exceeding the number of characters allocated for numbers =", MAX_BIG_DECIMAL_LENGTH,
                ". Type definition error", collectTypeDefNames(overheadBigDecimal)),
            join("Reduce the length"));
    }
}
