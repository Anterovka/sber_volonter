package com.sbt.converter.test;

import com.sbt.xmlmarker.CompositeId;
import com.sbt.xmlmarker.SystemClass;

@SystemClass
@CompositeId(fields = {"ser", "number"})
public interface CompositeIdClass {

    String getSer();

    String getNumber();

    String getCode();
}
