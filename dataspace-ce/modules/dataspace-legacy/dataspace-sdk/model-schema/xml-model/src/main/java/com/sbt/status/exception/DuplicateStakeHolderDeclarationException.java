package com.sbt.status.exception;

import java.util.Arrays;

public class DuplicateStakeHolderDeclarationException extends XmlStatusException {
    public DuplicateStakeHolderDeclarationException(String stakeholderName, String... modelClassNames) {
        super(join("In the class hierarchy, there is no need to declare the same observer in the child class",
                "if it has already been declared for the parent. The Observer '", stakeholderName, "' is defined for classes",
                Arrays.toString(modelClassNames), ", which are in the same inheritance hierarchy."),
            "Leave the announcement of identical observers (stakeholders) in elements of status-classes only for the ancestor.");
    }
}
