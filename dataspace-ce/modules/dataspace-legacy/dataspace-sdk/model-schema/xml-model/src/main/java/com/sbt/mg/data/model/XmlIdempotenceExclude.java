package com.sbt.mg.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.mg.data.model.usermodel.UserXmlIdempotenceExclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@XmlTagName(XmlModelClass.IDEMPOTENCE_EXCLUDE_TAG)
public class XmlIdempotenceExclude extends UserXmlIdempotenceExclude<Property> {
}
