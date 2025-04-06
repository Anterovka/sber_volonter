package com.sbt.model.exception;

import com.sbt.mg.data.model.typedef.XmlTypeDef;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;
import com.sbt.mg.jpa.JpaConstants;

import java.util.Collection;

public class TypeDefUnicodeStringLengthOverheadException extends CheckXmlModelException {
    public TypeDefUnicodeStringLengthOverheadException(Collection<XmlTypeDef> overheadUnicodeStringProperties) {
        super(join("Exceeding the length of a Unicode string by", JpaConstants.MAX_UNICODE_STRING_LENGTH, "characters.",
                "Error in type definitions:", collectTypeDefNames(overheadUnicodeStringProperties)),
            join("Use Text instead of UnicodeString type"));
    }
}
