package com.sbt.aggregator.exception;

import com.sbt.mg.data.model.XmlModelClass;

public class ManualAffinityInitialisingException extends AggregateException {

    public ManualAffinityInitialisingException(String modelClass) {
        super(join("Forbidden to set attribute", XmlModelClass.AFFINITY_TAG, "manually. Description error in class", modelClass),
            join("Remove the explicit setting of the tag", XmlModelClass.AFFINITY_TAG, ". The configuration is calculated automatically."));
    }
}
