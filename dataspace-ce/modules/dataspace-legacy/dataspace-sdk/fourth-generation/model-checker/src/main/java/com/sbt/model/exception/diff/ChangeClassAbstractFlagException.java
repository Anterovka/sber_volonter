package com.sbt.model.exception.diff;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class ChangeClassAbstractFlagException extends CheckXmlModelException {

    public ChangeClassAbstractFlagException(boolean prevAbstractFlag, boolean newAbstractFlag) {
        super(join("Violation of backward compatibility of the abstract class attribute. Previously, the abstractness feature was:",
                prevAbstractFlag, "now:", newAbstractFlag),
            "The flag of the abstract class cannot be changed.");
    }
}
