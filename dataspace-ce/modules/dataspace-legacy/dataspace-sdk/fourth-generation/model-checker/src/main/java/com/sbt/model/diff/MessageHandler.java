package com.sbt.model.diff;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;


public class MessageHandler {

    // a small decoration for the message - spaces and a hyphen at the beginning of the message
    private static String wrapMessage(String message) {
        return String.format(" - %s", message);
    }

    public static String getMessageChangeType(XmlModelClassProperty prevProperty, XmlModelClassProperty newProperty, String oldType) {
        return wrapMessage(String.format("in the property (%s) of the class (%s) the type has been changed from [%s] to [%s]",
            prevProperty.getName(), prevProperty.getModelClass().getName(), oldType, newProperty.getType()));
    }

    public static String getMessageReductionLengthType(XmlModelClassProperty prevProperty, int oldLength, int newLength) {
        return wrapMessage(String.format("in the property (%s) of the class (%s), the dimension has been reduced from [%d] to [%d]",
            prevProperty.getName(), prevProperty.getModelClass().getName(), oldLength, newLength));
    }

    public static String getMessageReductionScaleType(XmlModelClassProperty prevProperty, int oldScale, int newScale) {
        return wrapMessage(String.format("in the property (%s) of the class (%s), the number of decimal places has been reduced from [%d] to [%d]",
            prevProperty.getName(), prevProperty.getModelClass().getName(), oldScale, newScale));
    }

    public static String getMessageChangeCollectionType(XmlModelClassProperty prevProperty, boolean isCollection) {
        return wrapMessage(String.format("property (%s) of class (%s) became [%s]",
            prevProperty.getName(), prevProperty.getModelClass().getName(), isCollection ? "collection" : "non-collection"));
    }

    public static String getMessageRemoveProperty(XmlModelClassProperty property) {
        return wrapMessage(String.format("property (%s) of class (%s) is deleted",
            property.getName(), property.getModelClass().getName()));
    }

    public static String getMessageRemoveClass(XmlModelClass xmlModelClass) {
        return wrapMessage(String.format("class (%s) deleted", xmlModelClass.getName()));
    }

    public static String getMessageChangeEmbeddableClass(XmlModelClass xmlModelClass) {
        return wrapMessage(String.format("the sign of the embeddable class (%s) is changed", xmlModelClass.getName()));
    }

    public static String getMessageChangeEmbeddedProperty(XmlModelClassProperty property) {
        return wrapMessage(String.format("the indicator of the embedded property (%s) of the class (%s) has been changed",
            property.getName(), property.getModelClass().getName()));
    }

    public static String getMessageChangePropertyToReferenceRoot(XmlModelClassProperty property) {
        return wrapMessage(String.format("property (%s) of class (%s) has become a reference to an external object (the property tag was changed to reference in model.xml)",
            property.getName(), property.getModelClass().getName()));
    }

    public static String getMessageChangeReferenceRootToProperty(XmlModelClassProperty property) {
        return wrapMessage(String.format("link (%s.%s) to the external object has become a property (the reference tag was changed to property in model.xml)",
            property.getModelClass().getName(), property.getName()));
    }

    public static String getMessageChangeStrategy(XmlModelClass xmlModelClass) {
        return wrapMessage(String.format("the inheritance strategy has been changed to (%s) for the class (%s)",
            xmlModelClass.getStrategy(), xmlModelClass.getName()));
    }

    public static String getMessageChangeAbstractClass(XmlModelClass xmlModelClass) {
        return wrapMessage(String.format("changed abstract flag to (%s) for class (%s)",
            Boolean.TRUE.equals(xmlModelClass.isAbstract()) ? "true" : "false", xmlModelClass.getName()));
    }

}
