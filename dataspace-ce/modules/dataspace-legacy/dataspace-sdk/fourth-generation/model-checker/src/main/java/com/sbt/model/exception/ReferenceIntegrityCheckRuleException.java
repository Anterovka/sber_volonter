package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassReference;
import com.sbt.mg.data.model.usermodel.UserXmlModelClassReference;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

/**
 * Exception: Using the check-exists tag only to refer to the class model link
 */
public class ReferenceIntegrityCheckRuleException extends CheckXmlModelException {
    /**
     * @param reference properties with violation of the rule
     */
    public ReferenceIntegrityCheckRuleException(XmlModelClassReference reference) {
        super(join(String.format("Attribute \"%s\" of property", UserXmlModelClassReference.INTEGRITY_CHECK_TAG),
                reference.getName(),
                "class", reference.getModelClass().getName(),
                "with a value of true can be used only with the class model type."),
            "Fix the type or exclude the attribute");
    }
}
