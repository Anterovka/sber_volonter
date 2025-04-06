package com.sbt.status.exception;

public class DuplicateStatusClassesDeclareException extends XmlStatusException {
    public DuplicateStatusClassesDeclareException(String modelClassName) {
        super(join("Duplication of observer declaration is detected for class ('element status-classes') '",
                modelClassName, "'."),
            "Collect stakeholder announcements for the specified class into a single element status-classes");
    }
}
