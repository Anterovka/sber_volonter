package com.sbt.converter.test;

import com.sbt.xmlmarker.CompositeId;
import com.sbt.xmlmarker.SystemClass;

@SystemClass
@CompositeId(fields = {"code"})
public interface SingleCompositeIdClass {

    String getCode();

}
