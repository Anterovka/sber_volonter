package sbp.com.sbt.dataspace.feather.modeldescriptionimpl;

import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * XML description of the link
 */
class ReferenceDescriptionXml extends AbstractPropertyDescriptionXml {

    @XmlAttribute(name = "entity")
    String entityType;
    @XmlAttribute
    boolean mandatory;
    @XmlAttribute
    String entityReferencePropertyName;
    @XmlAttribute
    String entityReferencesCollectionPropertyName;
}
