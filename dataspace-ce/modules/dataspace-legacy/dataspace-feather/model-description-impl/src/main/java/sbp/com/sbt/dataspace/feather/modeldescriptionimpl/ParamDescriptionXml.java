package sbp.com.sbt.dataspace.feather.modeldescriptionimpl;

import sbp.com.sbt.dataspace.feather.modeldescription.DataType;

import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * XML parameter description
 */
public class ParamDescriptionXml {

    @XmlAttribute
    String name;
    @XmlAttribute
    DataType type;
    @XmlAttribute
    boolean collection;
    @XmlAttribute
    String defaultValue;
}
