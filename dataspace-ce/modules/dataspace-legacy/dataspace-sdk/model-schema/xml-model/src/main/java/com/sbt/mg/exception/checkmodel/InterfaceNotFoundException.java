package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.exception.AnyPositionException;

public class InterfaceNotFoundException extends AnyPositionException {
    public InterfaceNotFoundException(String interfaceName) {
        super(join("Interface", interfaceName, "not found in model"), "");
    }
}
