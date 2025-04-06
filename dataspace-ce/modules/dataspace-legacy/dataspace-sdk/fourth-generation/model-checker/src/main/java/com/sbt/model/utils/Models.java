package com.sbt.model.utils;

import com.sbt.mg.data.model.PropertyType;
import com.sbt.mg.data.model.TypeInfo;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.XmlModelInterface;
import com.sbt.mg.data.model.XmlModelInterfaceProperty;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.model.exception.PropertyTypeIsInterfaceException;
import com.sbt.model.exception.UnknownTypeException;

import java.util.Locale;

import static com.sbt.mg.ModelHelper.TYPES_INFO;

public class Models {

    /**
     * Returns the physical type of the property. For example, for links, this will be string or an embedded object.
     * Sets:
     * - category of property (Primitive or reference)
     * - writing type of property leads to the desired
     * - the link is composite, then it sets the identifier class and the embeddable attribute
     */
    public static void fillCategoryAndTypeInfo(XmlModelClassProperty classProperty) {
        fillCategory(classProperty);
        fillTypeInfo(classProperty);
    }

    private static void fillCategory(XmlModelClassProperty classProperty) {
        String type = classProperty.getType();

        TypeInfo typeInfo = TYPES_INFO.get(type.toLowerCase(Locale.ENGLISH));

        if (typeInfo != null) {
            classProperty.setCategory(PropertyType.PRIMITIVE);
        } else {
            XmlModel model = classProperty.getModelClass().getModel();
            XmlModelClass referenceClass = model.getClassNullable(type);
            if (referenceClass != null) {
                classProperty.setCategory(PropertyType.REFERENCE);
            } else if (model.containsEnum(type)) {
                classProperty.setCategory(PropertyType.PRIMITIVE);
            } else if (model.getInterfaceNullable(type) != null) {
                throw new PropertyTypeIsInterfaceException(classProperty);
            } else {
                throw new UnknownTypeException(classProperty);
            }
        }
    }

    private static void fillTypeInfo(XmlModelClassProperty classProperty) {
        String type = classProperty.getType();

        TypeInfo typeInfo = TYPES_INFO.get(type.toLowerCase(Locale.ENGLISH));

        if (typeInfo != null) {
// If typeInfo is String and the length exceeds 4k, then translate to text
            Integer length = classProperty.getLength();
            if ("string".equals(typeInfo.getHbmName()) && length != null && length > JpaConstants.MAX_STRING_LENGTH ||
                "text".equalsIgnoreCase(type)) {
                typeInfo = TYPES_INFO.get("text");
                classProperty.setLength(4001);
            }
// подменяем тип свойства
            classProperty.setType(typeInfo.getJavaName());
        } else {
// if the property is not primitive
            XmlModel model = classProperty.getModelClass().getModel();
            XmlModelClass referenceClass = model.getClassNullable(type);
            if (referenceClass != null) {
// if the property is a link
                XmlModelClass embeddedIdClass = model.getClassNullable(referenceClass.getId().getType());
// If the identifier is an embedded class
                if (embeddedIdClass != null) {
                    classProperty.setReferenceType(embeddedIdClass.getName());
                    classProperty.setEmbedded(true);
                    typeInfo = TYPES_INFO.get("null");
                } else {
// if the identifier is not an embedded object
                    typeInfo = TYPES_INFO.get(referenceClass.getId()
                        .getType().toLowerCase(Locale.ENGLISH));
                }
            } else if (model.containsEnum(type)) {
// If the type is enum, then we return the type as string, do not change the property type, and set the category to "primitive"
                typeInfo = TYPES_INFO.get("string");
            } else if (model.getInterfaceNullable(type) != null) {
                throw new PropertyTypeIsInterfaceException(classProperty);
            } else {
                throw new UnknownTypeException(classProperty);
            }
        }

        classProperty.setTypeInfo(typeInfo);
    }

    /**
     * Calculation of TypeInfo by property
     * Setting Type on property if it is a simple type
     */
    //TODO extract set from get method
    public static TypeInfo getTypeInfoForProperty(XmlModelInterfaceProperty interfaceProperty) {
        String type = interfaceProperty.getType();
        TypeInfo typeInfo = TYPES_INFO.get(type.toLowerCase(Locale.ENGLISH));
        if (typeInfo != null) {
            interfaceProperty.setType(typeInfo.getJavaName());
        } else {
            XmlModel model = interfaceProperty.getModelInterface().getModel();
            XmlModelClass referenceClass = model.getClassNullable(type);
            // type of property class
            if (referenceClass != null) {
                //The text contains no Russian words or phrases to be translated into English and replaced in the original text.
                XmlModelClass embeddedIdClass = model.getClassNullable(referenceClass.getId().getType());
                if (embeddedIdClass != null) {
                    typeInfo = TYPES_INFO.get("null");
                } else {
                    typeInfo = TYPES_INFO.get(referenceClass.getId()
                        .getType().toLowerCase(Locale.ENGLISH));
                }
            } else if (model.containsEnum(type)) {
                typeInfo = TYPES_INFO.get("string");
            } else {
                XmlModelInterface xmlModelInterface = model.getInterfaceNullable(type);
                if (xmlModelInterface != null) {
                    typeInfo = TYPES_INFO.get("null");
                } else {
                    throw new UnknownTypeException(interfaceProperty);
                }
            }
        }
        return typeInfo;
    }
}
