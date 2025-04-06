package com.sbt.model.exception;

import com.sbt.mg.data.model.MergeKind;
import com.sbt.mg.data.model.XmlModelExternalType;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class UnknownMergeKindException extends CheckXmlModelException {
    public UnknownMergeKindException(XmlModelExternalType externalType) {
        super(join("Unknown type of merge/deduplication mergeKind=", externalType.getMergeKind(), " in ", externalType.toString()),
            join("Select one of the listed types of merging/deduplication: ", MergeKind.values()));
    }
}
