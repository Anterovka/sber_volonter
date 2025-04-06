package sbp.com.sbt.dataspace.feather.modeldescriptionimpl;

import sbp.com.sbt.dataspace.feather.modeldescription.DataType;

import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * XML description of primitive
 */
class PrimitiveDescriptionXml extends AbstractPropertyDescriptionXml {

    @XmlAttribute
    DataType type;
    @XmlAttribute
    boolean mandatory;
    @XmlAttribute
    String enumType;
}
