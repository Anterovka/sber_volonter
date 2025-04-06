package com.sbt.mg.exception.checkmodel;

public class ClassDuplicationWithReservedException extends CheckXmlModelException {
    public ClassDuplicationWithReservedException(String className) {
        super(join("A collision of system class names with custom ones has been detected.",
                "Intersection by class name", className),
            join("It is necessary to rename the class with the name", className));
    }
}
