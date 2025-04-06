package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class TablePrefixNameChangeException extends CheckXmlModelException {
    public TablePrefixNameChangeException(String tablePrefixPrev, String tablePrefixCur) {
        super(join("It is forbidden to change the prefix name in the table. It was '", tablePrefixPrev, "', it became '", tablePrefixCur, "'."),
            join("leave the prefix with the value of", tablePrefixPrev, ".Tag name:", XmlModel.TABLE_PREFIX_TAG));
    }
}
