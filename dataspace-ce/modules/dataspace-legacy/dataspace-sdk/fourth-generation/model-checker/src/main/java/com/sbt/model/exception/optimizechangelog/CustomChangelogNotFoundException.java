package com.sbt.model.exception.optimizechangelog;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class CustomChangelogNotFoundException extends CheckXmlModelException {

    public CustomChangelogNotFoundException() {
        super("In previous releases, custom Liquibase scripts were present (custom-changelog)." +
                "When forming an optimized changelog, these scripts will be excluded. ",
            "Add necessary changes from previous user scripts to the new custom-changelog.xml," +
                "having provided the appropriate preConditions to prevent the repeated application of previously made changes," +
                "either, if changes from previous custom scripts are not needed, set the build flag skipCustomChangelogCheck = true");
    }

}
