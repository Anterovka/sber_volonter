package com.sbt.model.base;

import com.sbt.parameters.enums.Changeable;
import com.sbt.xmlmarker.Abstract;
import com.sbt.xmlmarker.Access;
import com.sbt.xmlmarker.Label;
import com.sbt.xmlmarker.Name;
import com.sbt.xmlmarker.NoAffinity;
import com.sbt.xmlmarker.SingleClass;

@Label("Main model class")
@Name(Entity.CLASS_NAME)
@Access(Changeable.SYSTEM)
@NoAffinity
@Abstract
@SingleClass
public interface Entity {
    String CLASS_NAME = "Entity";
}
