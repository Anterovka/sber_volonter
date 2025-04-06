package sbp.com.sbt.dataspace.feather.modeldescriptionimpl;

import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * XML description of the links collection
 */
class ReferencesCollectionDescriptionXml extends AbstractCollectionDescriptionXml {

    @XmlAttribute(name = "entity")
    String entityType;
}
