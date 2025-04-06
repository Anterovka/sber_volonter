package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.usermodel.UserXmlModelClass;

import java.util.Collection;
import java.util.stream.Collectors;

public class CloneablePositionException extends CheckXmlModelException {
    public CloneablePositionException(Collection<XmlModelClass> xmlModelClassList) {
        super(String.format(
                "For class%s %s, you cannot use the attribute %s.",
                xmlModelClassList.size() == 1 ? "а" : "ов",
                xmlModelClassList.stream().map(UserXmlModelClass::getName).collect(Collectors.joining(", ")),
                XmlModelClass.CLONEABLE_TAG
            )
        );
    }
}
