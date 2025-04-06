package com.sbt.dataspace.pdm.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class NotDefinedPreviousModelVersionException extends CheckXmlModelException {
    public NotDefinedPreviousModelVersionException() {
super("The previous version of the model needed to perform the optimization changelog has not been defined." +
"Presumably, before optimization, there were no model releases, or there were only \"-SNAPSHOT\" builds.",
"For optimization execution, it is necessary to have a model release issued from DataSpace version >= 1.9");
    }
}
