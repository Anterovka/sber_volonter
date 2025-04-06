package com.sbt.reference;

import com.sbt.mg.data.model.ReferenceGenerateStrategy;

public class ReferenceGenerateInfo {

    private final String collectionTypeName;
    private final ReferenceGenerateStrategy referenceGenerateStrategy;

    public ReferenceGenerateInfo(String collectionTypeName, ReferenceGenerateStrategy referenceGenerateStrategy) {
        this.collectionTypeName = collectionTypeName;
        this.referenceGenerateStrategy = referenceGenerateStrategy;
    }

    public String getCollectionTypeName() {
        return collectionTypeName;
    }

    public ReferenceGenerateStrategy getReferenceGenerateStrategy() {
        return referenceGenerateStrategy;
    }
}
