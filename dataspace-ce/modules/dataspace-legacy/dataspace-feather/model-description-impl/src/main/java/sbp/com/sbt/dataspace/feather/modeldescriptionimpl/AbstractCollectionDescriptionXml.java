package sbp.com.sbt.dataspace.feather.modeldescriptionimpl;

import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * Abstract XML description of collection
 */
class AbstractCollectionDescriptionXml extends AbstractPropertyDescriptionXml {

    @XmlAttribute
    String tableName;
    @XmlAttribute
    String ownerColumnName;
}
