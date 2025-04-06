package com.sbt.model.diff;

import com.sbt.dataspace.pdm.ParameterContext;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import static com.sbt.mg.ModelHelper.getXmlModelPropertyDeclaredFields;

public interface DiffHandler {
    void handler(XmlModel newModel, XmlModel baseModel, ParameterContext parameterContext);

    default void cloneProperty(XmlModelClassProperty dest,
                               XmlModelClassProperty src) {

        Map<String, Object> allChanges = dest.getAllChanges();
        XmlModelClass modelClass = dest.getModelClass();

        // Clone the value of all properties from the new model to the old one
        Field[] declaredFields = getXmlModelPropertyDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            if (Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            try {
                field.set(dest, field.get(src));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // Restore ownership to model class
        dest.setModelClass(modelClass);

        // Restores the list of changes
        for (Map.Entry<String, Object> entry : allChanges.entrySet()) {
            dest.addChangedProperty(entry.getKey(), entry.getValue());
        }
    }

}
