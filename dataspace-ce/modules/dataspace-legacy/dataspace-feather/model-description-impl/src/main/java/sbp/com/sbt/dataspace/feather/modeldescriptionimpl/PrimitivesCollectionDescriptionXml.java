package sbp.com.sbt.dataspace.feather.modeldescriptionimpl;

import sbp.com.sbt.dataspace.feather.modeldescription.DataType;

import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * XML description of the primitives collection
 */
class PrimitivesCollectionDescriptionXml extends AbstractCollectionDescriptionXml {

    @XmlAttribute
    DataType type;
    @XmlAttribute
    String enumType;
}
