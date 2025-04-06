package com.sbt.converter.test;

import com.sbt.parameters.enums.IdCategory;
import com.sbt.xmlmarker.Id;
import com.sbt.xmlmarker.SystemClass;

@SystemClass
@Id(IdCategory.NO_ID)
public interface NoIdClass {
    String getCode();
}
