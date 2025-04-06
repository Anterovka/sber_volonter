package com.sbt.model.exception.optimizechangelog;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class PdmNotFoundException extends CheckXmlModelException {
    public PdmNotFoundException() {
        super("Upon performing a build with optimization, the changelog does not find the previous state of the model (the file pdm.xml).Optimization is performed only if previous releases are available.",
            "Release a release(-s) without optimizing the changelog");
    }
}
