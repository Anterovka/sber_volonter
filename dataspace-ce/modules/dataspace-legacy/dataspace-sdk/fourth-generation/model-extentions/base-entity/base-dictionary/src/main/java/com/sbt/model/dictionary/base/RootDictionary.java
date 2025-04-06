package com.sbt.model.dictionary.base;

import com.sbt.mg.jpa.JpaConstants;
import com.sbt.model.base.IdEntity;
import com.sbt.parameters.enums.Changeable;
import com.sbt.parameters.enums.IdCategory;
import com.sbt.xmlmarker.Access;
import com.sbt.xmlmarker.Dictionary;
import com.sbt.xmlmarker.Id;
import com.sbt.xmlmarker.Label;
import com.sbt.xmlmarker.Name;

@Label("Aggregate class of reference")
@Name(RootDictionary.ROOT_DICTIONARY_NAME)
@Access(Changeable.READ_ONLY)
@Dictionary
@Id(IdCategory.MANUAL)
public interface RootDictionary extends IdEntity<String> {

    String ROOT_DICTIONARY_NAME = JpaConstants.ROOT_DICTIONARY_CLASS_NAME;

}
