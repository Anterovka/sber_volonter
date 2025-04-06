package sbp.com.sbt.dataspace.feather.modeldescriptionimpl;

import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * Abstract XML description of property
 */
class AbstractPropertyDescriptionXml {

    @XmlAttribute
    String name;
    @XmlAttribute
    String columnName;
}
