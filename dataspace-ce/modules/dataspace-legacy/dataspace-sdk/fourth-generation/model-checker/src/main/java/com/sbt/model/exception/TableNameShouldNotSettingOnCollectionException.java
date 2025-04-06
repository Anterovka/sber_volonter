package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import static com.sbt.mg.data.model.XmlModelClassProperty.COLLECTION_TABLE_TAG;

public class TableNameShouldNotSettingOnCollectionException extends CheckXmlModelException {

    public TableNameShouldNotSettingOnCollectionException(XmlModelClass modelClass, XmlModelClassProperty property) {
        super(join("It is forbidden to specify table names. Error when explicitly specifying the table name=", property.getCollectionTableName(),
                ", In the class=", modelClass.getName(), " for the attribute=", property.getName()),
            join("Удалите tag=", COLLECTION_TABLE_TAG));
    }

}
