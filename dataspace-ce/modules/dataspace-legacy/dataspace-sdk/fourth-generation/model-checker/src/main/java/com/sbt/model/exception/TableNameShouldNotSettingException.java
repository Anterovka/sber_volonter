package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import static com.sbt.mg.data.model.XmlModelClass.TABLE_NAME_TAG;

public class TableNameShouldNotSettingException extends CheckXmlModelException {

    public TableNameShouldNotSettingException(XmlModelClass modelClass) {
        super(join("It is forbidden to specify table names. Error when explicitly specifying the table name=", modelClass.getTableName(),
                " In the class=", modelClass.getName()),
            join("Удалите tag=", TABLE_NAME_TAG));
    }

}
