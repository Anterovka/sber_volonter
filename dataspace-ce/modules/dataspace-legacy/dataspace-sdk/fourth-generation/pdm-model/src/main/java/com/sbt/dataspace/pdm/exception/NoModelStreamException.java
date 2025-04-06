package com.sbt.dataspace.pdm.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class NoModelStreamException extends CheckXmlModelException {
    public NoModelStreamException() {
        super("In the passed streams, the main model stream is missing.",
            "It is necessary to add a stream with the MODEL key to the streams transmitted for model uploading.");
    }
}
